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
import java.math.BigInteger

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class RegisterController @Inject constructor(
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

        val serverPrivateKey = BigInteger(
            server.getServerKey(1)?.privateKey,
            16
        )
        val publicKey: String = serverPrivateKey.toPublicKey().compressed()

        val clientPublicKey: String = payload.authenKey
        val name: String = payload.userName

        val checkUserName = service.findUser(payload.userName)?.userName
        if (checkUserName == payload.userName) {
            return HttpResponse.badRequest("Invalid User Name: $checkUserName")
        } else {
            val sharedKey = ecdh.sharedSecret(
                serverPrivateKey,
                clientPublicKey
            )
            // ทำสิ่งที่คุณต้องการกับ sharedKey


            //val statement: Boolean = service.insert()


            return HttpResponse.created(publicKey)
        }
    }
}
