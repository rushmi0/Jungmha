package org.jungmha.routes.api.v1.user.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Put
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.jungmha.database.form.UserProfileForm
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.database.record.EncryptedData
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.xss.XssDetector
import org.jungmha.utils.AccountDirectory
import org.slf4j.LoggerFactory
import jakarta.inject.Inject
import org.jungmha.constants.EnumField
import org.jungmha.constants.NormalValidateField
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.jungmha.security.securekey.TokenResponse


// * RegisterController

/**
 * คลาสนี้เป็น Controller สำหรับดำเนินการลงทะเบียนผู้ใช้
 */
@SecurityRequirement(name = "Access-Token")
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class RegisterController @Inject constructor(
    private val userService: UserServiceImpl,
    private val walkersService: DogsWalkersServiceImpl,
    private val token: Token,
    private val aes: AES
) {

    private val objectMapper = jacksonObjectMapper()

    /**
     * สำหรับการลงทะเบียนผู้ใช้
     *
     * @param name ชื่อผู้ใช้
     * @param payload ข้อมูลที่ถูกเข้ารหัสแล้วที่จะถูกใช้ในการลงทะเบียน
     * @return HttpResponse แจ้งเตือนหรือคืนค่าสถานะของการลงทะเบียน
     */
    @Operation(
        summary = "สำหรับการลงทะเบียนผู้ใช้",
        description = "ทำการ Encrypt ชุดข้อมูลส่วนบุคคลด้วย AES ก่อนส่งไปที่ Server",
        requestBody = RequestBody(
            required = true,
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserProfileForm::class),
                )
            ]
        ),
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "HttpResponse แจ้งเตือนหรือคืนค่าสถานะของการลงทะเบียน",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = TokenResponse::class)
                    )
                ]
            )
        ]
    )
    @Put(
        uri = "/auth/sign-up",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun signUp(
        @Header("UserName") name: String,
        @Body payload: EncryptedData
    ): MutableHttpResponse<out Any?>? {
        return try {
            LOG.info("Executing signUp")
            val response: MutableHttpResponse<out Any?> = processRegistration(name, payload)
            return response
        } catch (e: Exception) {
            LOG.error("Error creating the account: ${e.message}", e)
            HttpResponse.serverError("Failed to create the account: ${e.message}")
        }
    }

    /**
     * เมธอดที่ใช้ในการประมวลผลข้อมูลของการลงทะเบียน
     *
     * @param name ชื่อผู้ใช้
     * @param payload ข้อมูลที่ถูกเข้ารหัสแล้วที่จะถูกใช้ในการลงทะเบียน
     * @return HttpResponse สำหรับผลลัพธ์ของข้อมูลที่ถูก Encrypt
     */
    private suspend fun processRegistration(
        name: String,
        payload: EncryptedData
    ): MutableHttpResponse<out Any?> {
        val userInfo = userService.findUser(name)
            ?: return HttpResponse.badRequest("User not found")
        val shareKey = userInfo.sharedKey

        val decryptedData: Map<String, Any?> = aes.decrypt(payload.content, shareKey)

        // ตรวจสอบค่า null และ XSS
        val validationResponse: MutableHttpResponse<out Any?> = validateDecryptedData(
            NormalValidateField.entries.toTypedArray(),
            decryptedData
        )

        if (validationResponse.status != HttpStatus.OK) {
            return validationResponse
        }

        // ใช้ ObjectMapper เพื่อแปลง decryptedData เป็น UserProfileForm (Kotlin Object)
        val userData = objectMapper.convertValue(decryptedData, UserProfileForm::class.java)

        val statement: Boolean = userService.updateMultiField(name, userData)
        return if (statement) {
            val userId = userInfo.userID
            val token = token.buildTokenPair(
                name,
                30
            )

            val type = userData.userType
            if (type == "Normal") {
                AccountDirectory.createDirectory(type, userId)
            } else if (type == "DogWalkers") {
                walkersService.insert(userId)
                AccountDirectory.createDirectory(type, userId)
            }

            HttpResponse.created(token)
        } else {
            LOG.error("Failed to create the account: Update operation failed")
            HttpResponse.serverError("Failed to create the account: Update operation failed")
        }
    }


    companion object {

        /**
         * เมธอดที่ใช้ในการตรวจสอบความถูกต้องของข้อมูลที่ถูก Decrypt
         *
         * @param fields ฟิลด์ที่ต้องการตรวจสอบ
         * @param decryptedData ข้อมูลที่ถูก Decrypt
         * @return HttpResponse สำหรับผลลัพธ์ของการตรวจสอบ
         */
        fun validateDecryptedData(
            fields: Array<out EnumField>,
            decryptedData: Map<String, Any?>
        ): MutableHttpResponse<out Any?> {
            for (field in fields) {
                val value = decryptedData[field.fieldName]
                if (value == null || value.toString().isBlank()) {
                    LOG.warn("field '${field.fieldName}' is null or empty")
                    return HttpResponse.badRequest("$field cannot be null or empty")
                }
                if (XssDetector.containsXss(value.toString())) {
                    LOG.warn("XSS detected in $field")
                    return HttpResponse.badRequest("Cross-site scripting detected in $field")
                }
            }
            return HttpResponse.ok()
        }

        private val LOG = LoggerFactory.getLogger(RegisterController::class.java)
    }

}
