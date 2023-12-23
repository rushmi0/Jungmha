package org.jungmha.routes.api.v1.user.auth

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import org.jungmha.database.form.IdentityForm
import org.jungmha.database.statement.ServerKeyServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.security.securekey.ECDHkey
import org.jungmha.security.securekey.ECPublicKey.compressed
import org.jungmha.security.securekey.ECPublicKey.toPublicKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigInteger

// * OpenChannelController

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class OpenChannelController @Inject constructor(
    private val server: ServerKeyServiceImpl,
    private val service: UserServiceImpl,
    private val ecdh: ECDHkey
) {

    @Post(
        uri = "/auth/open-channel",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun openChannel(
        @Body payload: IdentityForm
    ): MutableHttpResponse<out Any?>? {

        try {
            // ขั้นตอนเริ่มต้น
            val serverPrivateKey = BigInteger(
                server.getServerKey(1)?.privateKey,
                16
            )
            val publicKey: String = serverPrivateKey.toPublicKey().compressed()

            val clientPublicKey: String = payload.authenKey
            val name: String = payload.userName

            // ตรวจสอบ User Name
            val checkUserName = service.findUser(payload.userName)?.userName
            if (checkUserName == payload.userName) {
                LOG.warn("Invalid User Name: $checkUserName")
                return HttpResponse.badRequest("Invalid User Name: $checkUserName")
            }

            // สร้าง share secret key
            val sharedKey = ecdh.sharedSecret(
                serverPrivateKey,
                clientPublicKey
            )

            // การสร้าง IdentityForm
            val id = IdentityForm(
                name,
                clientPublicKey,
                sharedKey
            )

            // การทำงานกับ UserService
            val statement: Boolean = service.insert(id)
            if (statement) {
                LOG.info("Create channel successful for New User")
                return HttpResponse.created(publicKey)
            } else {
                LOG.error("Failed to create a channel for the account")
                return HttpResponse.serverError("Failed to create a channel for the account")
            }
        } catch (e: Exception) {
            LOG.error("Error during open channel operation", e)
            return HttpResponse.serverError("Unexpected error during open channel operation")
        }
    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(OpenChannelController::class.java)
    }

}
