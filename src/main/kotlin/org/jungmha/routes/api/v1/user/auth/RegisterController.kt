package org.jungmha.routes.api.v1.user.auth

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Patch
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
import org.jungmha.domain.response.EncryptedData
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.xss.XssDetector
import org.jungmha.utils.AccountDirectory
import org.slf4j.LoggerFactory
import jakarta.inject.Inject
import kotlinx.coroutines.coroutineScope
import org.jungmha.database.field.UserProfileField
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

    /**
     * สำหรับการลงทะเบียนผู้ใช้
     *
     * @param name ชื่อผู้ใช้
     * @param payload ข้อมูลที่ถูกเข้ารหัสแล้วที่จะถูกใช้ในการลงทะเบียน
     * @return HttpResponse แจ้งเตือนหรือคืนค่าสถานะของการลงทะเบียน
     */
    @Operation(
        summary = "สำหรับการลงทะเบียนผู้ใช้",
        description = "ทำการ Encrypt ชุดข้อมูลส่วนบุคคลด้วย AES ก่อนส่งไปที่ Server ด้วยรูปแบบข้อมูลนี้ \n \"content\" : \"string\"",
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
    @Patch(
        uri = "/auth/sign-up",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun signUp(
        @Header("UserName") name: String,
        @Header("Account-Type") type: String,
        @Body payload: EncryptedData
    ): MutableHttpResponse<out Any?>? {
        return try {
            LOG.info("Executing signUp")
            val response: MutableHttpResponse<out Any?> = if (type == "Normal") {
                coroutineScope {
                    processNormalRegistration(name, payload)
                }
            } else if (type == "DogWalkers") {
                TODO("Not yet implemented")
            } else {
                HttpResponse.badRequest("Invalid Header value for Account-Type: [$type]")
            }
            return response
        } catch (e: Exception) {
            LOG.error("Error creating the account: ${e.message}", e)
            HttpResponse.serverError("Failed to create the account: ${e.message}")
        }
    }


    private suspend fun processNormalRegistration(
        name: String,
        payload: EncryptedData
    ): MutableHttpResponse<out Any?> {
        val shareKey = userService.findUser(name)?.sharedKey.toString()
        val decryptedData: Map<String, Any> = aes.decrypt(payload.content, shareKey)
        val userData: UserProfileForm = mapDecryptedDataToUserProfileForm(decryptedData)

        if (containsXss(userData)) {
            return HttpResponse.badRequest("Cross-site scripting detected")
        }

        val statement: Boolean = userService.updateMultiField(name, userData)

        return if (statement) {
            val user: UserProfileField? = userService.findUser(name)
            val userId = user?.userID
            val token = token.buildTokenPair(name, 999999999999999999)

            if (userId != null) {
                AccountDirectory.createDirectory(userData.userType, userId)
            }

            HttpResponse.created(token)
        } else {
            LOG.error("Failed to create the account: Update operation failed")
            HttpResponse.serverError("Failed to create the account: Update operation failed")
        }
    }


    private suspend fun processDogWalkersRegistration(
        name: String,
        payload: EncryptedData
    ): MutableHttpResponse<out Any?> {
        TODO("Not yet implemented")
    }

    private fun mapDecryptedDataToUserProfileForm(decryptedData: Map<String, Any?>): UserProfileForm {
        return UserProfileForm(
            decryptedData["firstName"].toString(),
            decryptedData["lastName"].toString(),
            decryptedData["email"].toString(),
            decryptedData["phoneNumber"].toString(),
            decryptedData["userType"].toString()
        )
    }

    private fun containsXss(userData: UserProfileForm): Boolean {
        return XssDetector.containsXss(userData.firstName) ||
                XssDetector.containsXss(userData.lastName) ||
                XssDetector.containsXss(userData.email) ||
                XssDetector.containsXss(userData.phoneNumber) ||
                XssDetector.containsXss(userData.userType)
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(RegisterController::class.java)
    }

}
