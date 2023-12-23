package org.jungmha.routes.api.v1.user.auth

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.form.UserProfileForm
import org.jungmha.database.statement.ServerKeyServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.domain.response.EncryptedData
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.utils.AccountDirectory
import org.slf4j.MDC
import org.slf4j.LoggerFactory
import jakarta.inject.Inject

// * RegisterController

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class RegisterController @Inject constructor(
    private val server: ServerKeyServiceImpl,
    private val service: UserServiceImpl,
    private val token: Token,
    private val aes: AES
) {

    @Post(
        uri = "/auth/sign-up",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun signUp(
        @Header("UserName") name: String,
        @Body payload: EncryptedData
    ): MutableHttpResponse<out Any?>? {

        try {
            LOG.info("Thread ${Thread.currentThread().name} executing signUp")
            MDC.put("thread -> ", Thread.currentThread().name)

            val shareKey = service.findUser(name)?.sharedKey.toString()
            val decrypted = aes.decrypt(payload.content, shareKey)

            val data = UserProfileForm(
                decrypted["firstName"].toString(),
                decrypted["lastName"].toString(),
                decrypted["email"].toString(),
                decrypted["phoneNumber"].toString(),
                decrypted["userType"].toString()
            )

            val statement: Boolean = service.updateMultiField(
                name,
                data
            )

            if (statement) {
                val user = service.findUser(name)
                val userId = user?.userID
                val token = token.buildTokenPair(name)
                if (userId != null) {
                    AccountDirectory.createDirectory(data.userType, userId)
                }
                return HttpResponse.created(token)
            } else {
                LOG.error("Failed to create the account: Update operation failed")
                return HttpResponse.serverError("Failed to create the account: Update operation failed")
            }
        } catch (e: Exception) {
            LOG.error("Error creating the account: ${e.message}", e)
            return HttpResponse.serverError("Failed to create the account: ${e.message}")
        } finally {
            MDC.clear()
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(RegisterController::class.java)
    }

}
