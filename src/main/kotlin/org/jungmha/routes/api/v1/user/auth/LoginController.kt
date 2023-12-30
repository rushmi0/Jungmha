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
import jakarta.inject.Inject
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.statement.SignatureServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.security.securekey.ECDSA
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// * Login Controller

/**
 * คลาสนี้เป็น Controller สำหรับการดำเนินการเข้าสู่ระบบ
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
     * สำหรับการดำเนินการเข้าสู่ระบบ
     *
     * @param sign ลายเซ็นที่ใช้ในการตรวจสอบความถูกต้องของข้อมูล
     * @param username ชื่อผู้ใช้
     * @return HttpResponse พร้อมกับข้อมูล Token หากการเข้าสู่ระบบสำเร็จ
     */
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

                userData != null -> {
                    val publickey = userData.authenKey
                    val uri = "/auth/sign-in/$username"

                    if (signService.checkSign(username, sign) && ECDSA.verify(uri, publickey, sign)) {
                        val authToken: TokenResponse = token.buildTokenPair(username, 50)
                        LOG.info("User [$username] Authentication successful")
                        HttpResponse.ok(authToken)
                    } else {
                        LOG.warn("Invalid Signature for user [$username]")
                        HttpResponse.badRequest("Invalid Signature")
                    }
                }

                else -> {
                    LOG.warn("User not found with username [$username]")
                    HttpResponse.badRequest("User not found")
                }

            }

            response
        } catch (e: Exception) {
            LOG.error("An unexpected error occurred during user authentication", e)
            HttpResponse.serverError("An unexpected error occurred")
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(LoginController::class.java)
    }

}

