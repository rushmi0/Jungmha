package org.jungmha.routes.api.v1.user.account

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.coroutineScope
import org.jungmha.constants.DogWalkerUpdateField
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.domain.response.DogWalkersInfo
import org.jungmha.domain.response.EncryptedData
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.jungmha.security.xss.XssDetector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

import org.jungmha.constants.Waring.BAD_REQUEST_USER_NOT_FOUND
import org.jungmha.constants.Waring.BAD_REQUEST_UPDATE_FAILED
import org.jungmha.constants.Waring.BAD_REQUEST_XSS_DETECTED
import org.jungmha.constants.Waring.OK_ALL_FIELDS_UPDATED
import org.jungmha.constants.Waring.OK_UPDATE_SUCCESSFUL


@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogWalkersController @Inject constructor(
    private val walkerService: DogsWalkersServiceImpl,
    private val userService: UserServiceImpl,
    private val token: Token,
    private val aes: AES
) {

    @Get(
        uri = "auth/user/dogwalkers/id/{id}",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getSingleDogWalkersInfo(id: Int): Any? {
        return walkerService.getSingleDogWalkersInfo(id) ?: HttpResponse.badRequest("Not found")
    }


    @Get(
        uri = "auth/user/dogwalkers/{name}",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPersonalInfo(
        @Header("Access-Token") access: String,
        name: String
    ): MutableHttpResponse<out Any?>? {
        return try {
            // ตรวจสอบความถูกต้องของ Token และการอนุญาตของผู้ใช้
            val userDetails: TokenObject = token.viewDetail(access)
            val verify = token.verifyToken(access)
            val permission: String = userDetails.permission
            val name = userDetails.userName

            // ตรวจสอบความถูกต้องของ Token และสิทธิ์การใช้งาน
            return if (verify && permission == "view") {
                coroutineScope {
                    processSearching(name)
                }
            } else {
                LOG.warn("Invalid token or insufficient permission for user: $name")
                HttpResponse.badRequest("Invalid token or insufficient permission")
            }
        } catch (e: Exception) {
            LOG.error("Error processing getPersonalInfo", e)
            HttpResponse.serverError("Internal server error: ${e.message}")
        }
    }

    private suspend fun processSearching(name: String): MutableHttpResponse<out Any?> {
        // เรียกดูข้อมูลบัญชีผู้ใช้คนนั้นๆ
        val userInfo: DogWalkersInfo? = walkerService.getDogWalkersInfo(name)

        return if (userInfo != null) {
            userInfo.processEncrypting(name)
        } else {
            LOG.warn("User info not found for user: $name")
            HttpResponse.notFound("User info not found")
        }
    }

    private suspend fun DogWalkersInfo.processEncrypting(name: String): MutableHttpResponse<out Any?> {
        // นำข้อมูลมา Encrypt
        val shareKey = userService.findUser(name)?.sharedKey.toString()
        val encrypted = aes.encrypt(this.toString(), shareKey)
        return HttpResponse.ok(EncryptedData(encrypted))
        //return HttpResponse.ok(this)
    }



    // �� ──────────────────────────────────────────────────────────────────────────────────────── �� \\


    @Patch(
        uri = "auth/user/dogwalkers",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun editPersonalInfo(
        @Header("Access-Token") access: String,
        @Body payload: EncryptedData
    ): MutableHttpResponse<out Any?>? {
        return try {
            // ตรวจสอบความถูกต้องของ Token และการอนุญาตของผู้ใช้
            val userDetails: TokenObject = token.viewDetail(access)
            val verify = token.verifyToken(access)
            val permission: String = userDetails.permission
            val name = userDetails.userName

            // ตรวจสอบความถูกต้องของ Token และสิทธิ์การใช้งาน
            return if (verify && permission == "edit") {
                coroutineScope {
                    processDecrypting(name, payload)
                }
            } else {
                LOG.warn("Invalid token or insufficient permission for user: $name")
                HttpResponse.badRequest("Invalid token or insufficient permission")
            }
        } catch (e: Exception) {
            LOG.error("Error processing editPersonalInfo", e)
            HttpResponse.serverError("Internal server error: ${e.message}")
        }
    }


    private suspend fun processDecrypting(
        name: String,
        payload: EncryptedData
    ): MutableHttpResponse<out Any?> {
        return try {
            val userInfo = userService.findUser(name) ?: return HttpResponse.badRequest(BAD_REQUEST_USER_NOT_FOUND)
            val userID = userInfo.userID
            val shareKey = userInfo.sharedKey

            val decryptedData: Map<String, Any> = aes.decrypt(payload.content, shareKey)
            val updateQueue: Queue<String> = buildUpdateQueue(decryptedData)

            for (field in updateQueue) {
                val newValue = decryptedData[field]?.toString() ?: continue

                if (XssDetector.containsXss(newValue)) {
                    return HttpResponse.badRequest(BAD_REQUEST_XSS_DETECTED)
                }

                val result = processFieldUpdate(userID, field, newValue)

                // ตรวจสอบว่า result เป็น MutableHttpResponse และมีค่า status และไม่เท่ากับ null
                if (result.status != null && result.status != HttpStatus.OK) {
                    return result
                }

            }

            return HttpResponse.ok(OK_ALL_FIELDS_UPDATED)
        } catch (e: IllegalArgumentException) {
            LOG.warn("Invalid Field", e)
            return HttpResponse.badRequest("Invalid Field")
        }
    }


    private fun buildUpdateQueue(decryptedData: Map<String, Any?>): Queue<String> {
        val updateQueue = ArrayDeque<String>()
        for (field in DogWalkerUpdateField.entries) {
            if (decryptedData[field.key] != null) {
                updateQueue.add(field.key)
            }
        }
        return updateQueue
    }


    private suspend fun processFieldUpdate(
        userID: Int,
        fieldName: String,
        newValue: String
    ): MutableHttpResponse<out Any?> {
        return try {
            when (fieldName) {
                "email", "phoneNumber" -> processFieldUpdateForEmailOrPhoneNumber(userID, fieldName, newValue)
                else -> processFieldUpdateForOtherFields(userID, fieldName, newValue)
            }
        } catch (e: Exception) {
            LOG.error("Error updating field [$fieldName] for user ID [$userID]", e)
            return HttpResponse.serverError("Internal server error: ${e.message}")
        }
    }


    private suspend fun processFieldUpdateForEmailOrPhoneNumber(
        userID: Int,
        fieldName: String,
        newValue: String
    ): MutableHttpResponse<out Any?> {
        if (XssDetector.containsXss(newValue)) {
            return HttpResponse.badRequest(BAD_REQUEST_XSS_DETECTED)
        }

        val statement: Boolean = userService.updateSingleField(userID, fieldName, newValue)

        return if (statement) {
            HttpResponse.ok(OK_UPDATE_SUCCESSFUL.format(fieldName))
        } else {
            HttpResponse.badRequest(BAD_REQUEST_UPDATE_FAILED.format(fieldName, newValue))
        }
    }

    private suspend fun processFieldUpdateForOtherFields(
        userID: Int,
        fieldName: String,
        newValue: String
    ): MutableHttpResponse<out Any?> {
        if (XssDetector.containsXss(newValue)) {
            return HttpResponse.badRequest(BAD_REQUEST_XSS_DETECTED)
        }

        val id = walkerService.getSingleDogWalkersInfo(userID)?.userID!!
        val statement: Boolean = walkerService.updateSingleField(id, fieldName, newValue)

        return if (statement) {
            HttpResponse.ok(OK_UPDATE_SUCCESSFUL.format(fieldName))
        } else {
            HttpResponse.badRequest(BAD_REQUEST_UPDATE_FAILED.format(fieldName, newValue))
        }
    }


    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogWalkersController::class.java)
    }

}