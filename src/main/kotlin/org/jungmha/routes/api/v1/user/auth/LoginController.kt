package org.jungmha.routes.api.v1.user.auth

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.inject.Inject
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.SignatureForm
import org.jungmha.database.statement.SignatureServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.security.securekey.ECDSA
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * **คลาส LoginController**
 *
 * Controller สำหรับการจัดการเกี่ยวกับการเข้าสู่ระบบ (Sign-In) ของผู้ใช้
 * ซึ่งรับผิดชอบในการตรวจสอบลายเซ็นเจอร์ (Signature) และการสร้าง Token สำหรับผู้ใช้ที่เข้าสู่ระบบสำเร็จ
 */

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class LoginController @Inject constructor(
    private val signService: SignatureServiceImpl,
    private val userService: UserServiceImpl,
    private val token: Token,
) {

    /**
     * **เมธอด signIn**
     *
     * ใช้สำหรับการเข้าสู่ระบบผู้ใช้ โดยตรวจสอบลายเซ็นเจอร์และสร้าง Token สำหรับผู้ใช้ที่เข้าสู่ระบบสำเร็จ
     *
     * @param sign ลายเซ็นเจอร์ที่ส่งมาจากผู้ใช้
     * @param username ชื่อผู้ใช้
     * @return MutableHttpResponse ที่เป็นผลลัพธ์ของการเข้าสู่ระบบ
     */
    @Operation(
        responses = [
            ApiResponse(
                content = [
                    Content(
                        mediaType = "application/json",
                        //schema = Schema(implementation = NormalInfo::class)
                    )
                ]
            )
        ]
    )
    @Get(
        uri = "/auth/sign-in/{username}",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun signIn(
        @Header("Signature") sign: String,
        username: String
    ): MutableHttpResponse<out Any?>? {

        val userData: UserProfileField? = userService.findUser(username)

        return try {
            val response = when {
                userData != null -> handleUserAuthentication(sign, username, userData)
                else -> {
                    LOG.warn("User not found with username [$username]")
                    return HttpResponse.badRequest("User not found")
                }
            }
            response
        } catch (e: Exception) {
            LOG.error("An unexpected error occurred during user authentication: ${e.message}")
            return HttpResponse.serverError("An unexpected error occurred")
        }
    }

    /**
     * **เมธอด handleUserAuthentication**
     *
     * ใช้สำหรับการตรวจสอบลายเซ็นเจอร์และสร้าง Token สำหรับผู้ใช้ที่เข้าสู่ระบบสำเร็จ
     *
     * @param sign ลายเซ็นเจอร์ที่ส่งมาจากผู้ใช้
     * @param username ชื่อผู้ใช้
     * @param userData ข้อมูลผู้ใช้
     * @return MutableHttpResponse ที่เป็นผลลัพธ์ของการตรวจสอบลายเซ็นและการเข้าสู่ระบบ
     */
    private suspend fun handleUserAuthentication(
        sign: String,
        username: String,
        userData: UserProfileField
    ): MutableHttpResponse<out Any?> {
        val publickey = userData.authenKey
        val uri = "/auth/sign-in/$username"

        return if (signService.checkSign(username, sign) && ECDSA.verify(uri, publickey, sign)) {
            val authToken: TokenResponse = token.buildTokenPair(username, 50)
            signService.insert(SignatureForm(userData.userID, sign))
            LOG.info("User [$username] Authentication successful")
            HttpResponse.ok(authToken)
        } else {
            LOG.warn("Invalid Signature for user [$username]")
            HttpResponse.badRequest("Invalid Signature")
        }
    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(LoginController::class.java)
    }

}
