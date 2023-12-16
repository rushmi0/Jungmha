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
import org.slf4j.MDC

@Controller("api/v1")
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class SignUpNewAccount @Inject constructor(
    private val service: UserServiceImpl,
) {

    private val LOG = LoggerFactory.getLogger(SignUpNewAccount::class.java)

    @Post(
        uri = "/user/sign-up",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun signUp(
        @Body payloadNormal: UserProfileForm,
    ): MutableHttpResponse<out Any>? {
        try {
            // Set thread information in the MDC
            MDC.put("thread", Thread.currentThread().name)

            if (payloadNormal.userName.isNotBlank() && payloadNormal.authenKey.isNotBlank()) {
                if (XssDetector.containsXss(payloadNormal.userName) ||
                    XssDetector.containsXss(payloadNormal.authenKey)
                ) {
                    return HttpResponse.badRequest("Cross-site scripting detected")
                } else {
                    val checkUserName = service.findUser(payloadNormal.userName)?.userName
                    if (checkUserName == payloadNormal.userName) {
                        return HttpResponse.badRequest("Invalid User Name: $checkUserName")
                    } else {
                        val userForm = UserProfileForm(
                            payloadNormal.imageProfile,
                            payloadNormal.userName,
                            payloadNormal.firstName,
                            payloadNormal.lastName,
                            payloadNormal.email,
                            payloadNormal.phoneNumber,
                            payloadNormal.authenKey,
                            payloadNormal.userType
                        )

                        val statement: Boolean = service.insert(userForm)

                        if (statement) {
                            val user = service.findUser(payloadNormal.userName)
                            val userId = user?.userID
                            if (userId != null) {
                                AccountDirectory.createDirectory(payloadNormal.userType, userId)
                            }
                            return HttpResponse.created("Account created successfully")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            LOG.error("There was an error creating the account: ", e)
        } finally {
            MDC.clear()
        }
        return HttpResponse.serverError("Failed to create the account")
    }
}
