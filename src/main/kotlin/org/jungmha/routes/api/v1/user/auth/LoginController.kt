package org.jungmha.routes.api.v1.user.auth

import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
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
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.SignatureForm
import org.jungmha.database.statement.SignatureServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.security.securekey.ECDSA
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigInteger
import jakarta.inject.Inject

/**
 * คลาส LoginController
 *
 * Controller สำหรับการจัดการเกี่ยวกับการเข้าสู่ระบบ (Sign-In) ของผู้ใช้
 * ซึ่งรับผิดชอบในการตรวจสอบลายเซ็นเจอร์ (Signature) และการสร้าง Token สำหรับผู้ใช้ที่เข้าสู่ระบบสำเร็จ
 */

@Tag(
    name = "User auth",
   //description = "API ที่เกี่ยวข้องกับ Dog Walkers"
)
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
class LoginController @Inject constructor(
    private val signService: SignatureServiceImpl,
    private val userService: UserServiceImpl,
    private val token: Token,
) {

    /**
     * เมธอด signIn
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
                        schema = Schema(implementation = TokenResponse::class)
                    )
                ]
            )
        ]
    )
    @Get(
        uri = "/auth/sign-in/{username}/{unixTime}",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun signIn(
        @Header("Signature") sign: String,
        username: String,
        unixTime: String
    ): MutableHttpResponse<out Any?>? {

        val userData: UserProfileField? = userService.findUser(username)

        return try {
            val response = when {
                userData != null -> handleUserAuthentication(sign, unixTime, username, userData)
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
     * เมธอด handleUserAuthentication
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
        unixTime: String,
        username: String,
        userData: UserProfileField
    ): MutableHttpResponse<out Any?> {
        val publickey = userData.authenKey
        val uri = "/auth/sign-in/$username/$unixTime"

        val currentUnixTime = System.currentTimeMillis()
        val isTimeValid: Boolean = (currentUnixTime.toBigInteger() - BigInteger(unixTime)) <= BigInteger("60000")

        return if (signService.checkSign(username, sign) && ECDSA.verify(uri, publickey, sign) && isTimeValid) {
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

fun main() {

    // /auth/sign-in/HelloWorld3 : d93caff530f5e87058f7ce2df45b932a8f413f3000e1bdcb5cd3359bbd863148
    val uri = "/auth/sign-in/HelloWorld3"


    val unixTime = System.currentTimeMillis()
    println(unixTime)
}
