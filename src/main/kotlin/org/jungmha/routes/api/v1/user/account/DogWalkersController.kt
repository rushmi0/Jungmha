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
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.domain.request.DogWalkerUpdateField
import org.jungmha.domain.response.DogWalkersInfo
import org.jungmha.domain.response.EncryptedData
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.jungmha.security.xss.XssDetector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

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
        uri = "auth/user/dogwalkers/{name}",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPersonalInfo(
        //@Header("Access-Token") access: String
        name: String
    ): MutableHttpResponse<out Any?>? {
        return try {
            // ตรวจสอบความถูกต้องของ Token และการอนุญาตของผู้ใช้
//            val userDetails: TokenObject = token.viewDetail(access)
//            val verify = token.verifyToken(access)
//            val permission: String = userDetails.permission
//            val name = userDetails.userName

            // ตรวจสอบความถูกต้องของ Token และสิทธิ์การใช้งาน
            // verify && permission == "view"
            return if (true) {
                coroutineScope {
                    processSearching(name)
                }
            } else {
                NormalController.LOG.warn("Invalid token or insufficient permission for user: $name")
                HttpResponse.badRequest("Invalid token or insufficient permission")
            }
        } catch (e: Exception) {
            NormalController.LOG.error("Error processing getPersonalInfo", e)
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
        //val shareKey = userService.findUser(name)?.sharedKey.toString()
        //val encrypted = aes.encrypt(this.toString(), shareKey)
        //return HttpResponse.ok(EncryptedData(encrypted))
        return HttpResponse.ok(this)
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


    private suspend fun processDecrypting(name: String, payload: EncryptedData): MutableHttpResponse<out Any?> {
        return try {
            val userInfo = userService.findUser(name) ?: return HttpResponse.badRequest("User not found")
            val userID = userInfo.userID
            val shareKey = userInfo.sharedKey

            val decryptedData = aes.decrypt(payload.content, shareKey)
            val updateQueue = buildUpdateQueue(decryptedData)

            for (field in updateQueue) {
                val newValue = decryptedData[field]?.toString() ?: continue
                if (XssDetector.containsXss(newValue)) {
                    return HttpResponse.badRequest("Cross-site scripting detected")
                }

                val result = processFieldUpdate(userID, field, newValue)
                if (result.status != HttpStatus.OK) {
                    return result
                }
            }

            return HttpResponse.ok("All fields updated successfully")
        } catch (e: IllegalArgumentException) {
            NormalController.LOG.warn("Invalid Field", e)
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
            if (XssDetector.containsXss(newValue)) {
                return HttpResponse.badRequest("Cross-site scripting detected")
            }

            val statement: Boolean = userService.updateSingleField(userID, fieldName, newValue)

            return if (statement) {
                HttpResponse.ok("Finished updating $fieldName field")
            } else {
                HttpResponse.badRequest("Failed to update $fieldName field: $newValue")
            }
        } catch (e: Exception) {
            NormalController.LOG.error("Error updating field [$fieldName] for user ID [$userID]", e)
            HttpResponse.serverError("Internal server error: ${e.message}")
        }
    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(DogWalkersController::class.java)
    }

}