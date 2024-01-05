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
import org.jungmha.constants.Warning
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

// * Dog Walkers Controller

/**
 * คลาสนี้เป็น Controller สำหรับดำเนินการที่เกี่ยวข้องกับ Dog Walkers
 */
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

    /**
     * สำหรับการดึงข้อมูล Dog Walkers จาก ID
     *
     * @param id ไอดีของ Dog Walkers
     * @return ข้อมูล Dog Walkers หากพบ หรือ HttpResponse แจ้งเตือนหากไม่พบ
     */
    @Get(
        uri = "auth/user/dogwalkers/id/{id}",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getSingleDogWalkersInfo(id: Int): Any? {
        LOG.info("Current Class: ${Thread.currentThread().stackTrace[1].className}")
        LOG.info("Executing Method: ${Thread.currentThread().stackTrace[1].methodName}")
        LOG.info("Thread ${Thread.currentThread().name} [ID: ${Thread.currentThread().id}] in state ${Thread.currentThread().state}. Is Alive: ${Thread.currentThread().isAlive}")
        return walkerService.getSingleDogWalkersInfo(id) ?: HttpResponse.badRequest("Not found")
    }

    /**
     * สำหรับการดึงข้อมูล Dog Walkers ที่เกี่ยวข้องกับผู้ใช้
     *
     * @param access Access-Token สำหรับตรวจสอบความถูกต้องและสิทธิ์การใช้งาน
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูล Dog Walkers หรือแจ้งเตือนหากไม่สามารถดึงข้อมูลได้
     */
    @Get(
        uri = "auth/user/dogwalkers/{userName}",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPersonalInfo(
        //@Header("Access-Token") access: String
        userName: String
    ): MutableHttpResponse<out Any?>? {
        return try {
            // ตรวจสอบความถูกต้องของ Token และการอนุญาตของผู้ใช้
//            val userDetails: TokenObject = token.viewDetail(access)
//            val verify = token.verifyToken(access)
//            val permission: String = userDetails.permission
//            val userName = userDetails.userName

            // ตรวจสอบความถูกต้องของ Token และสิทธิ์การใช้งาน
            return when {
                true//verify && permission == "view"
                -> {
                    coroutineScope {
                        processSearching(userName)
                    }
                }
                else -> {
                    LOG.warn(Warning.INVALID_TOKEN.message.format(userName))
                    HttpResponse.badRequest(Warning.INVALID_TOKEN_)
                }
            }
        } catch (e: Exception) {
            LOG.error(Warning.ERROR_PROCESSING.message.format(e))
            HttpResponse.serverError(Warning.INTERNAL_SERVER_ERROR.message.format(e.message))
        }
    }

    /**
     * เมธอดที่ใช้ในการประมวลผลข้อมูล Dog Walkers
     *
     * @param name ชื่อผู้ใช้
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลที่ถูก Encrypt หรือแจ้งเตือนหากไม่พบข้อมูล
     */
    private suspend fun processSearching(name: String): MutableHttpResponse<out Any?> {
        // เรียกดูข้อมูลบัญชีผู้ใช้คนนั้นๆ
        val userInfo: DogWalkersInfo? = walkerService.getDogWalkersInfo(name)

        return if (userInfo != null) {
            userInfo.processEncrypting(name)
        } else {
            LOG.warn(Warning.USER_INFO_NOT_FOUND.message.format(name))
            HttpResponse.notFound(Warning.USER_INFO_NOT_FOUND_)
        }
    }

    /**
     * เมธอดที่ใช้ในการ Encrypt ข้อมูล Dog Walkers
     *
     * @param name ชื่อผู้ใช้
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลที่ถูก Encrypt
     */
    private suspend fun DogWalkersInfo.processEncrypting(name: String): MutableHttpResponse<out Any?> {
        // นำข้อมูลมา Encrypt
        val shareKey = userService.findUser(name)?.sharedKey.toString()
        val encrypted = aes.encrypt(this.toString(), shareKey)
        //return HttpResponse.ok(EncryptedData(encrypted))
        return HttpResponse.ok(this)
    }

    // �� ──────────────────────────────────────────────────────────────────────────────────────── �� \\


    /**
     * สำหรับแก้ไขข้อมูลส่วนตัวของ Dog Walkers
     *
     * @param access Access-Token สำหรับตรวจสอบความถูกต้องและสิทธิ์การใช้งาน
     * @param payload ข้อมูลที่ถูก Encrypt ที่จะถูกใช้ในการแก้ไขข้อมูล
     * @return HttpResponse สำหรับผลลัพธ์ของการแก้ไขข้อมูลหรือแจ้งเตือนหากไม่สามารถดำเนินการได้
     */
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
            return when {
                verify && permission == "edit" -> {
                    coroutineScope {
                        processDecrypting(name, payload)
                    }
                }
                else -> {
                    LOG.warn(Warning.INVALID_TOKEN.message.format(name))
                    HttpResponse.badRequest(Warning.INVALID_TOKEN_)
                }
            }
        } catch (e: Exception) {
            LOG.error(Warning.ERROR_PROCESSING.message.format(e))
            HttpResponse.serverError(Warning.INTERNAL_SERVER_ERROR.message.format(e.message))
        }
    }

    /**
     * เมธอดที่ใช้ในการประมวลผลการแก้ไขข้อมูล Dog Walkers
     *
     * @param name ชื่อผู้ใช้
     * @param payload ข้อมูลที่ถูก Encrypt ที่จะถูกใช้ในการแก้ไขข้อมูล
     * @return HttpResponse สำหรับผลลัพธ์ของการแก้ไขข้อมูลหรือแจ้งเตือนหากไม่สามารถดำเนินการได้
     */
    private suspend fun processDecrypting(
        name: String,
        payload: EncryptedData
    ): MutableHttpResponse<out Any?> {
        return try {
            val userInfo = userService.findUser(name) ?: return HttpResponse.badRequest(Warning.BAD_REQUEST_USER_NOT_FOUND)
            val userID = userInfo.userID
            val shareKey = userInfo.sharedKey

            val decryptedData: Map<String, Any> = aes.decrypt(payload.content, shareKey)
            val updateQueue: Queue<String> = buildUpdateQueue(decryptedData)

            for (field in updateQueue) {
                val newValue = decryptedData[field]?.toString() ?: continue

                if (XssDetector.containsXss(newValue)) {
                    return HttpResponse.badRequest(Warning.BAD_REQUEST_XSS_DETECTED)
                }

                val result = processFieldUpdate(userID, field, newValue)

                // ตรวจสอบว่า result เป็น MutableHttpResponse และมีค่า status และไม่เท่ากับ null
                if (result.status != null && result.status != HttpStatus.OK) {
                    return result
                }
            }

            return HttpResponse.ok(Warning.OK_ALL_FIELDS_UPDATED)
        } catch (e: IllegalArgumentException) {
            LOG.warn(Warning.INVALID_FIELD.message.format(e))
            return HttpResponse.badRequest(Warning.INVALID_FIELD_)
        }
    }

    /**
     * เมธอดที่ใช้ในการสร้างคิวของฟิลด์ที่ต้องการแก้ไข
     *
     * @param decryptedData ข้อมูลที่ถูก Decrypt
     * @return คิวของฟิลด์ที่ต้องการแก้ไข
     */
    private fun buildUpdateQueue(decryptedData: Map<String, Any?>): Queue<String> {
        val updateQueue = ArrayDeque<String>()
        for (field in DogWalkerUpdateField.entries) {
            if (decryptedData[field.key] != null) {
                updateQueue.add(field.key)
            }
        }
        return updateQueue
    }

    /**
     * เมธอดที่ใช้ในการแก้ไขฟิลด์แต่ละตัว
     *
     * @param userID ไอดีของผู้ใช้
     * @param fieldName ชื่อฟิลด์ที่ต้องการแก้ไข
     * @param newValue ค่าใหม่ที่ใช้ในการแก้ไข
     * @return HttpResponse สำหรับผลลัพธ์ของการแก้ไขข้อมูลหรือแจ้งเตือนหากไม่สามารถดำเนินการได้
     */
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
            LOG.error(Warning.ERROR_UPDATING_FIELD.message.format(fieldName, userID, e.message))
            return HttpResponse.serverError(Warning.INTERNAL_SERVER_ERROR.message.format(e.message))
        }
    }

    /**
     * เมธอดที่ใช้ในการแก้ไขฟิลด์ email หรือ phoneNumber
     *
     * @param userID ไอดีของผู้ใช้
     * @param fieldName ชื่อฟิลด์ที่ต้องการแก้ไข
     * @param newValue ค่าใหม่ที่ใช้ในการแก้ไข
     * @return HttpResponse สำหรับผลลัพธ์ของการแก้ไขข้อมูลหรือแจ้งเตือนหากไม่สามารถดำเนินการได้
     */
    private suspend fun processFieldUpdateForEmailOrPhoneNumber(
        userID: Int,
        fieldName: String,
        newValue: String
    ): MutableHttpResponse<out Any?> {
        if (XssDetector.containsXss(newValue)) {
            return HttpResponse.badRequest(Warning.BAD_REQUEST_XSS_DETECTED)
        }

        val statement: Boolean = userService.updateSingleField(userID, fieldName, newValue)

        return if (statement) {
            HttpResponse.ok(Warning.OK_UPDATE_SUCCESSFUL.message.format(fieldName))
        } else {
            HttpResponse.badRequest(Warning.BAD_REQUEST_UPDATE_FAILED.message.format(fieldName, newValue))
        }
    }

    /**
     * เมธอดที่ใช้ในการแก้ไขฟิลด์ที่ไม่ใช่ email และ phoneNumber
     *
     * @param userID ไอดีของผู้ใช้
     * @param fieldName ชื่อฟิลด์ที่ต้องการแก้ไข
     * @param newValue ค่าใหม่ที่ใช้ในการแก้ไข
     * @return HttpResponse สำหรับผลลัพธ์ของการแก้ไขข้อมูลหรือแจ้งเตือนหากไม่สามารถดำเนินการได้
     */
    private suspend fun processFieldUpdateForOtherFields(
        userID: Int,
        fieldName: String,
        newValue: String
    ): MutableHttpResponse<out Any?> {
        if (XssDetector.containsXss(newValue)) {
            return HttpResponse.badRequest(Warning.BAD_REQUEST_XSS_DETECTED)
        }

        val id = walkerService.getSingleDogWalkersInfo(userID)?.userID!!
        val statement: Boolean = walkerService.updateSingleField(id, fieldName, newValue)

        return if (statement) {
            HttpResponse.ok(Warning.OK_UPDATE_SUCCESSFUL.message.format(fieldName))
        } else {
            HttpResponse.badRequest(Warning.BAD_REQUEST_UPDATE_FAILED.message.format(fieldName, newValue))
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogWalkersController::class.java)
    }

}