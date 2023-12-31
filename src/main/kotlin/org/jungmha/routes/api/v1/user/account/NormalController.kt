package org.jungmha.routes.api.v1.user.account

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
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
import org.jungmha.domain.response.EncryptedData
import org.jungmha.domain.response.NormalInfo
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class NormalController @Inject constructor(
    private val userService: UserServiceImpl,
    private val token: Token,
    private val aes: AES
) {

    @Get(
        "auth/user/normal",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPersonalInfo(
        @Header("Access-Token") access: String
    ): MutableHttpResponse<out Any?>? {
        return try {
            // ตรวจสอบความถูกต้องของโทเค็นและการอนุญาตของผู้ใช้
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

    private suspend fun processSearching(name: String): MutableHttpResponse<out Any?> {
        // เรียกดูข้อมูล ขอบัญชีผู้ใช้คนนั้นๆ
        val userInfo: NormalInfo? = userService.getUserInfo(name)

        return if (userInfo != null) {
            userInfo.processEncrypting(name)
        } else {
            LOG.warn("User info not found for user: $name")
            HttpResponse.notFound("User info not found")
        }
    }

    private suspend fun NormalInfo.processEncrypting(name: String): MutableHttpResponse<out Any?> {
        // นำข้อมูลมา Encrypt
        val shareKey = userService.findUser(name)?.sharedKey.toString()
        val encrypted = aes.encrypt(this.toString(), shareKey)
        return HttpResponse.ok(EncryptedData(encrypted))
    }


    // �� ──────────────────────────────────────────────────────────────────────────────────────── �� \\


    @Patch(
        "auth/user/normal",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun editPersonalInfo(@Header("Access-Token") access: String) {
        // ตรวจสอบความถูกต้องของ Token
        val userDetails: TokenObject = token.viewDetail(access)
        val permission: String = userDetails.permission

        // ตรวจสอบสิทธิ์การใช้งาน
        if (!token.verifyToken(access) || permission != "edit") {
            LOG.warn("Invalid token or insufficient permission during editPersonalInfo")
        } else {
            LOG.info("User ${userDetails.userName} is editing personal information")
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(NormalController::class.java)
    }

}
