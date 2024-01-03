package org.jungmha.routes.api.v1.user.account

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Patch
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.coroutineScope
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.constants.NormalUpdateField
import org.jungmha.constants.Waring.BAD_REQUEST_UPDATE_FAILED
import org.jungmha.constants.Waring.BAD_REQUEST_XSS_DETECTED
import org.jungmha.domain.response.EncryptedData
import org.jungmha.domain.response.NormalInfo
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.jungmha.security.xss.XssDetector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

import org.jungmha.constants.Waring.ERROR_PROCESSING
import org.jungmha.constants.Waring.INVALID_TOKEN
import org.jungmha.constants.Waring.INVALID_TOKEN_
import org.jungmha.constants.Waring.INTERNAL_SERVER_ERROR
import org.jungmha.constants.Waring.USER_INFO_NOT_FOUND
import org.jungmha.constants.Waring.USER_INFO_NOT_FOUND_
import org.jungmha.constants.Waring.OK_ALL_FIELDS_UPDATED
import org.jungmha.constants.Waring.INVALID_FIELD
import org.jungmha.constants.Waring.INVALID_FIELD_
import org.jungmha.constants.Waring.ERROR_UPDATING_FIELD
import org.jungmha.constants.Waring.OK_UPDATE_SUCCESSFUL

// * Normal Controller

/**
 * คลาสนี้เป็น Controller สำหรับการจัดการข้อมูลบัญชีผู้ใช้ทั่วไป
 */
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class NormalController @Inject constructor(
    private val userService: UserServiceImpl,
    private val token: Token,
    private val aes: AES
) {

    /**
     * เมธอดสำหรับดึงข้อมูลส่วนตัวของผู้ใช้
     *
     * @param access ข้อมูล Token ที่ใช้ในการตรวจสอบสิทธิ์
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลส่วนตัว
     */
    @Get(
        "auth/user/normal",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPersonalInfo(
        @Header("Access-Token") access: String
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
                LOG.warn(INVALID_TOKEN.format(name))
                HttpResponse.badRequest(INVALID_TOKEN_)
            }
        } catch (e: Exception) {
            LOG.error(ERROR_PROCESSING.format(e))
            HttpResponse.serverError(INTERNAL_SERVER_ERROR.format(e.message))
        }
    }

    /**
     * เมธอดที่ใช้ในการประมวลผลข้อมูลการค้นหาของผู้ใช้
     *
     * @param name ชื่อผู้ใช้
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลส่วนตัว
     */
    private suspend fun processSearching(name: String): MutableHttpResponse<out Any?> {
        // เรียกดูข้อมูลบัญชีผู้ใช้คนนั้นๆ
        val userInfo: NormalInfo? = userService.getUserInfo(name)

        return if (userInfo != null) {
            userInfo.processEncrypting(name)
        } else {
            LOG.warn(USER_INFO_NOT_FOUND.format(name))
            HttpResponse.notFound(USER_INFO_NOT_FOUND_)
        }
    }

    /**
     * เมธอดที่ใช้ในการประมวลผลข้อมูลที่ต้องการนำไป Encrypt
     *
     * @param name ชื่อผู้ใช้
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลที่ถูก Encrypt
     */
    private suspend fun NormalInfo.processEncrypting(name: String): MutableHttpResponse<out Any?> {
        // นำข้อมูลมา Encrypt
        val shareKey = userService.findUser(name)?.sharedKey.toString()
        val encrypted = aes.encrypt(this.toString(), shareKey)
        return HttpResponse.ok(EncryptedData(encrypted))
    }


    // �� ──────────────────────────────────────────────────────────────────────────────────────── �� \\


    /**
     * เมธอดสำหรับแก้ไขข้อมูลส่วนตัวของผู้ใช้
     *
     * @param access ข้อมูล Token ที่ใช้ในการตรวจสอบสิทธิ์
     * @param payload ข้อมูลที่ถูก Encrypt ที่ต้องการนำมาแก้ไข
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลที่ถูกแก้ไข
     */
    @Patch(
        "auth/user/normal",
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
                LOG.warn(INVALID_TOKEN.format(name))
                HttpResponse.badRequest(INVALID_TOKEN_)
            }
        } catch (e: Exception) {
            LOG.error(ERROR_PROCESSING.format(e))
            HttpResponse.serverError(INTERNAL_SERVER_ERROR.format(e.message))
        }
    }

    /**
     * เมธอดที่ใช้ในการประมวลผลข้อมูลที่ถูก Encrypt และทำการแก้ไข
     *
     * @param name ชื่อผู้ใช้
     * @param payload ข้อมูลที่ถูก Encrypt ที่ต้องการนำมาแก้ไข
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลที่ถูกแก้ไข
     */
    private suspend fun processDecrypting(
        name: String,
        payload: EncryptedData)
    : MutableHttpResponse<out Any?> {
        return try {
            val userInfo = userService.findUser(name)
            val userID = userInfo?.userID!!
            val shareKey = userInfo.sharedKey

            // ถอดรหัสข้อมูล
            val decryptedData = aes.decrypt(payload.content, shareKey)

            val updateQueue = buildUpdateQueue(decryptedData)


            // ใช้ for loop เพื่อดำเนินการอัปเดตในลำดับที่ถูกจัดลำดับ
            for (field in updateQueue) {
                val newValue = decryptedData[field]?.toString() ?: continue
                if (XssDetector.containsXss(newValue)) {
                    return HttpResponse.badRequest(BAD_REQUEST_XSS_DETECTED)
                }

                val result = processFieldUpdate(userID, field, newValue)
                if (result.status != HttpStatus.OK) {
                    return result
                }
            }

            return HttpResponse.ok(OK_ALL_FIELDS_UPDATED)
        } catch (e: IllegalArgumentException) {
            LOG.warn(INVALID_FIELD.format(e))
            HttpResponse.badRequest(INVALID_FIELD_)
        }
    }


    private fun buildUpdateQueue(decryptedData: Map<String, Any?>): Queue<String> {
        // นำข้อมูลที่ต้องการอัปเดตมาจัดลำดับลงในคิว
        val updateQueue = ArrayDeque<String>()

        for (field in NormalUpdateField.entries) {
            if (decryptedData[field.key] != null) {
                updateQueue.add(field.key)
            }
        }

        return updateQueue
    }


    /**
     * เมธอดที่ใช้ในการประมวลผลข้อมูลที่ถูกแก้ไขและทำการบันทึกลงในฐานข้อมูล
     *
     * @param userID ไอดีของผู้ใช้
     * @param fieldName ชื่อของฟิลด์ที่ต้องการแก้ไข
     * @param newValue ข้อมูลที่ต้องการนำมาแก้ไข
     * @return HttpResponse สำหรับผลลัพธ์ของการแก้ไข
     */
    private suspend fun processFieldUpdate(
        userID: Int,
        fieldName: String,
        newValue: String
    ): MutableHttpResponse<out Any?> {
        return try {
            if (XssDetector.containsXss(newValue)) {
                return HttpResponse.badRequest(BAD_REQUEST_XSS_DETECTED)
            }

            val statement: Boolean = userService.updateSingleField(userID, fieldName, newValue)

            return if (statement) {
                HttpResponse.ok(OK_UPDATE_SUCCESSFUL.format(fieldName))
            } else {
                HttpResponse.badRequest(BAD_REQUEST_UPDATE_FAILED.format(fieldName, newValue))
            }
        } catch (e: Exception) {
            LOG.error(ERROR_UPDATING_FIELD.format(fieldName,userID,e))
            HttpResponse.serverError(INTERNAL_SERVER_ERROR.format(e.message))
        }
    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(NormalController::class.java)
    }

}
