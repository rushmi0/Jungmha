package org.jungmha.routes.api.v1.user.auth

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject
import org.jungmha.database.form.UserProfileForm
import org.jungmha.database.service.UserService
import org.jungmha.security.xss.XssDetector
import org.jungmha.utils.AccountDirectory
import org.slf4j.LoggerFactory

@Controller("api/v1")
class SignUpNewAccount @Inject constructor(
    private val service: UserService
) {

    private val LOG = LoggerFactory.getLogger(SignUpNewAccount::class.java)

    @Post(
        uri = "/user/sign-up",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun signUp(
        @Header("AccountType") accountType: String?,
        @Body payload: UserProfileForm
    ): MutableHttpResponse<out Any>? {
        try {
            when (accountType) {
                "Normal", "DogWalkers" -> {
                    if (payload.userName.isNotBlank() && payload.authenKey.isNotBlank()) {
                        if (XssDetector.containsXss(payload.userName) ||
                            XssDetector.containsXss(payload.authenKey)
                        ) {
                            return HttpResponse.badRequest("Cross-site scripting detected")
                        } else {
                            val checkUserName = service.findUserName(payload.userName)?.userName

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
                                    val user = service.findUserName(payload.userName)
                                    val id: Int? = user?.userID
                                    if (id != null) {
                                        AccountDirectory.createDirectory(accountType, id)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            LOG.error("There was an error creating the account: ", e)
        }
        return HttpResponse.created("สร้างบัญชีสำเร็จ")
    }
}
