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

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag

import org.jungmha.constants.DogWalkerUpdateField
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.database.record.DogWalkersInfo
import org.jungmha.database.record.EncryptedData
import org.jungmha.database.record.UpdateContact
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.jungmha.security.xss.XssDetector

import jakarta.inject.Inject
import kotlinx.coroutines.coroutineScope

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

// * Dog Walkers Controller

/**
 * คลาสนี้เป็น Controller สำหรับดำเนินการที่เกี่ยวข้องกับ Dog Walkers
 */
@Tag(
    name = "Dog Walkers",
    description = "API ที่เกี่ยวข้องกับ Dog Walkers"
)
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
     * สำหรับการดึงข้อมูล Dog Walkers ที่เกี่ยวข้องกับผู้ใช้
     *
     * @param access Access-Token สำหรับตรวจสอบความถูกต้องและสิทธิ์การใช้งาน
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูล Dog Walkers หรือแจ้งเตือนหากไม่สามารถดึงข้อมูลได้
     */
    @Operation(
        responses = [
            ApiResponse(
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = DogWalkersInfo::class)
                    )
                ]
            )
        ]
    )
    @Get(
        uri = "auth/user/dogwalkers",
        consumes = [MediaType.APPLICATION_JSON],
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
            val userName = userDetails.userName

            // ตรวจสอบความถูกต้องของ Token และสิทธิ์การใช้งาน
            return when {

                verify && permission == "view" -> {
                    coroutineScope {
                        processSearching(userName)
                    }
                }

                else -> {
                    LOG.warn("Invalid token or insufficient permission for user: $userName")
                    HttpResponse.badRequest("Invalid token or insufficient permission")
                }

            }
        } catch (e: Exception) {
            LOG.error("Error processing: ${e.message}")
            HttpResponse.serverError("Internal server error: ${e.message}")
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
            LOG.warn("User info not found for user: $name")
            HttpResponse.notFound("User info not found")
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
        return HttpResponse.ok(EncryptedData(encrypted))
        //return HttpResponse.ok(this)
    }

    // �� ──────────────────────────────────────────────────────────────────────────────────────── �� \\


    /**
     * สำหรับแก้ไขข้อมูลส่วนตัวของ Dog Walkers
     *
     * @param access Access-Token สำหรับตรวจสอบความถูกต้องและสิทธิ์การใช้งาน
     * @param payload ข้อมูลที่ถูก Encrypt ที่จะถูกใช้ในการแก้ไขข้อมูล
     * @return HttpResponse สำหรับผลลัพธ์ของการแก้ไขข้อมูลหรือแจ้งเตือนหากไม่สามารถดำเนินการได้
     */
    @RequestBody(
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = UpdateContact::class)
            )
        ]
    )
    @Operation(
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Fields updated successfully"
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid token or insufficient permission"
            ),
            ApiResponse(
                responseCode = "5xx",
                description = "Internal server error"
            )
        ]
    )
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
                    LOG.warn("Invalid token or insufficient permission for user: $name")
                    HttpResponse.badRequest("Invalid token or insufficient permission")
                }
            }
        } catch (e: Exception) {
            LOG.error("Error processing: ${e.message}")
            HttpResponse.serverError("Internal server error: ${e.message}")
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
        try {
            // ค้นหาข้อมูลผู้ใช้
            val userInfo = userService.findUser(name)
                ?: return HttpResponse.badRequest("User not found")

            // ดึงข้อมูลผู้ใช้
            val userID = userInfo.userID
            val shareKey = userInfo.sharedKey

            // Decrypt ข้อมูล
            val decryptedData: Map<String, Any> = aes.decrypt(payload.content, shareKey)

            /**
             * โดยในที่นี้เราใช้ queue ในการจัดเก็บฟิลด์ที่ต้องการแก้ไขของ Dog Walkers
             * ตัวอย่างเช่น ถ้ามีการแก้ไข email, phone number, และ location
             * ตัว queue จะถูกสร้างเพื่อเก็บลำดับที่ต้องแก้ไขเช่น ["email", "phoneNumber", "location" ... ]
             */

            // สร้างคิวของฟิลด์ที่ต้องการแก้ไข
            val updateQueue: Queue<String> = buildUpdateQueue(decryptedData)

            // วนลูปตามคิวและดำเนินการแก้ไขฟิลด์
            for (field in updateQueue) {
                val newValue = decryptedData[field]?.toString() ?: continue

                // ตรวจสอบความปลอดภัยของข้อมูลที่จะนำมาแก้ไข
                if (XssDetector.containsXss(newValue)) {
                    return HttpResponse.badRequest("Cross-site scripting detected")
                }

                // ดำเนินการแก้ไขฟิลด์
                val result = processFieldUpdate(userID, field, newValue)

                // ตรวจสอบผลลัพธ์และคืนค่าหากไม่สำเร็จ
                if (result.status != null && result.status != HttpStatus.OK) {
                    return result
                }
            }

            return HttpResponse.ok("Fields updated successfully")
        } catch (e: IllegalArgumentException) {
            LOG.warn("Invalid Field ${e.message}")
            return HttpResponse.badRequest("Invalid Field")
        }
    }

    /**
     * เมธอดที่ใช้ในการสร้างคิวของฟิลด์ที่ต้องการแก้ไข
     *
     * @param decryptedData ข้อมูลที่ถูก Decrypt
     * @return คิวของฟิลด์ที่ต้องการแก้ไข
     */
    private fun buildUpdateQueue(decryptedData: Map<String, Any?>): Queue<String> {

        /**
         * โดยในที่นี้เราใช้ enum DogWalkerUpdateField เพื่อกำหนดฟิลด์ที่สามารถแก้ไขได้
         * และตรวจสอบว่าฟิลด์ดังกล่าวมีค่าในข้อมูลที่ถูก Decrypt หรือไม่
         * ถ้ามีค่า ก็จะถูกเพิ่มลงในคิวเพื่อให้สามารถดำเนินการแก้ไขตามลำดับได้
         */

        val updateQueue = ArrayDeque<String>()
        for (field in DogWalkerUpdateField.entries) {
            if (decryptedData[field.fieldName] != null) {
                updateQueue.add(field.fieldName)
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
            LOG.error("Error updating field [$fieldName] for user ID [$userID] ${e.message}")
            HttpResponse.serverError("Internal server error: ${e.message}")
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
            return HttpResponse.badRequest("Cross-site scripting detected")
        }

        val statement: Boolean = userService.updateSingleField(userID, fieldName, newValue)

        return if (statement) {
            HttpResponse.ok("Finished updating $fieldName field")
        } else {
            HttpResponse.badRequest("Failed to update $fieldName field: $newValue")
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
            return HttpResponse.badRequest("Cross-site scripting detected")
        }

        val id = walkerService.getSingleDogWalkersInfo(userID)?.userID!!
        val statement: Boolean = walkerService.updateSingleField(id, fieldName, newValue)

        return if (statement) {
            HttpResponse.ok("Finished updating $fieldName field")
        } else {
            HttpResponse.badRequest("Failed to update $fieldName field: $newValue")
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogWalkersController::class.java)
    }

}