package org.jungmha.routes.api.v1.user.account

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject
import kotlinx.coroutines.coroutineScope
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.constants.NormalUpdateField
import org.jungmha.database.record.EncryptedData
import org.jungmha.database.record.NormalInfo
import org.jungmha.security.securekey.ChaCha20

import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.jungmha.security.xss.XssDetector
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

// * Normal Controller

/**
 * คลาสนี้เป็น Controller สำหรับการจัดการข้อมูลบัญชีผู้ใช้ทั่วไป
 */

@Tag(
    name = "User normal",
    //description = "API ที่เกี่ยวข้องกับ Dog Walkers"
)
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
class NormalController @Inject constructor(
    private val userService: UserServiceImpl,
    private val token: Token,
    private val chacha: ChaCha20
) {

    /**
     * เมธอดสำหรับดึงข้อมูลส่วนตัวของผู้ใช้
     *
     * @param access ข้อมูล Token ที่ใช้ในการตรวจสอบสิทธิ์
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลส่วนตัว
     */
    @Operation(
        responses = [
            ApiResponse(
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = NormalInfo::class)
                    )
                ]
            )
        ]
    )
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
                LOG.warn("Invalid token or insufficient permission for user: $name")
                HttpResponse.badRequest("Invalid token or insufficient permission")
            }
        } catch (e: Exception) {
            LOG.error("Error processing getPersonalInfo", e)
            HttpResponse.serverError("Internal server error: ${e.message}")
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
        println(userInfo)

        val objectMapper = ObjectMapper().registerModule(KotlinModule()).registerModule(JavaTimeModule())
        val jsonString = objectMapper.writeValueAsString(userInfo)

        return if (userInfo != null) {
            val rawObject = jacksonObjectMapper().writeValueAsString(userInfo)
            //rawObject.processEncrypting(name)
            jsonString.processEncrypting(name)
        } else {
            LOG.warn("User info not found for user: $name")
            HttpResponse.notFound("User info not found")
        }
    }

    /**
     * เมธอดที่ใช้ในการประมวลผลข้อมูลที่ต้องการนำไป Encrypt
     *
     * @param name ชื่อผู้ใช้
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลที่ถูก Encrypt
     */
    private suspend fun String.processEncrypting(name: String): MutableHttpResponse<out Any?> {
        // นำข้อมูลมา Encrypt
        val shareKey = userService.findUser(name)?.sharedKey.toString()
        val encrypted = chacha.encrypt(
            this,
            shareKey
        )
        return HttpResponse.ok(EncryptedData(encrypted))
        //return HttpResponse.ok(this)
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
                LOG.warn("Invalid token or insufficient permission for user: $name")
                HttpResponse.badRequest("Invalid token or insufficient permission")
            }
        } catch (e: Exception) {
            LOG.error("Error processing editPersonalInfo", e)
            HttpResponse.serverError("Internal server error: ${e.message}")
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
        payload: EncryptedData
    ): MutableHttpResponse<out Any?> {
        return try {
            val userInfo = userService.findUser(name)
            val userID = userInfo?.userID!!
            val shareKey = userInfo.sharedKey

            // ถอดรหัสข้อมูล
            val decryptedData = chacha.decrypt(payload.content, shareKey)

            val updateQueue = buildUpdateQueue(decryptedData)


            // ใช้ for loop เพื่อดำเนินการอัปเดตในลำดับที่ถูกจัดลำดับ
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
            LOG.warn("Invalid Field", e)
            HttpResponse.badRequest("Invalid Field")
        }
    }


    private fun buildUpdateQueue(decryptedData: Map<String, Any?>): Queue<String> {
        // นำข้อมูลที่ต้องการอัปเดตมาจัดลำดับลงในคิว
        val updateQueue = ArrayDeque<String>()

        for (field in NormalUpdateField.entries) {
            if (decryptedData[field.fieldName] != null) {
                updateQueue.add(field.fieldName)
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
                return HttpResponse.badRequest("Cross-site scripting detected")
            }

            val statement: Boolean = userService.updateSingleField(userID, fieldName, newValue)

            return if (statement) {
                HttpResponse.ok("Finished updating $fieldName field")
            } else {
                HttpResponse.badRequest("Failed to update $fieldName field: $newValue")
            }
        } catch (e: Exception) {
            LOG.error("Error updating field [$fieldName] for user ID [$userID]", e)
            HttpResponse.serverError("Internal server error: ${e.message}")
        }
    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(NormalController::class.java)
    }

}
