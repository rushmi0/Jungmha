package org.jungmha.routes.api.v1.user.auth

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.form.UserProfileForm
import org.jungmha.security.xss.XssDetector
import org.jungmha.utils.AccountDirectory
import org.slf4j.LoggerFactory
import jakarta.inject.Inject
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.security.securekey.Token


@Controller("api/v1")
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class NewAccountController @Inject constructor(
    private val service: UserServiceImpl,
    private val token: Token
) {

    @Post(
        uri = "/sign-up",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun signUp(@Body payload: UserProfileForm): MutableHttpResponse<out Any?>? {
        try {
            LOG.info("Executing signUp: {}", payload)

            if (payload.userName.isNotBlank() && payload.authenKey.isNotBlank()) {
                if (XssDetector.containsXss(payload.userName) ||
                    XssDetector.containsXss(payload.authenKey)
                ) {
                    return HttpResponse.badRequest("Cross-site scripting detected")
                } else {
                    val checkUserName: String = service.findUser(payload.userName)?.userName.toString()

                    if (checkUserName == payload.userName) {
                        return HttpResponse.badRequest("Invalid User Name: $checkUserName")
                    } else {
                        val userForm = UserProfileForm(
                            payload.imageProfile,
                            payload.userName,
                            payload.firstName,
                            payload.lastName,
                            payload.email,
                            payload.phoneNumber,
                            payload.authenKey,
                            payload.userType
                        )

                        val statement: Boolean = service.insert(userForm)

                        if (statement) {
                            val user = service.findUser(payload.userName)
                            val userId = user?.userID
                            val token = token.buildToken(payload.userName)
                            if (userId != null) {
                                AccountDirectory.createDirectory(payload.userType, userId)
                            }
                            return HttpResponse.created(token)
                        } else {
                            return HttpResponse.serverError("Failed to create the account")
                        }
                    }
                }
            } else {
                return HttpResponse.badRequest("Invalid input data")
            }
        } catch (e: Exception) {
            LOG.error("Error creating the account", e)
            return HttpResponse.serverError("Failed to create the account")
        }
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(NewAccountController::class.java)
    }


}

