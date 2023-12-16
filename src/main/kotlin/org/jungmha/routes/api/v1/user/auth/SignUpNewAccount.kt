package org.jungmha.routes.api.v1.user.auth

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject
import org.jungmha.database.form.DogWalkerForm
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
        @Body payloadNormal: UserProfileForm,
        @Body payloadDogWalkers: DogWalkerForm
    ): MutableHttpResponse<out Any>? {
        try {
            when (accountType) {
                "Normal", "DogWalkers" -> {
                    if (payloadNormal.userName.isNotBlank() && payloadNormal.authenKey.isNotBlank()) {
                        if (XssDetector.containsXss(payloadNormal.userName) ||
                            XssDetector.containsXss(payloadNormal.authenKey)
                        ) {
                            return HttpResponse.badRequest("Cross-site scripting detected")
                        } else {
                            val checkUserName = service.findUserName(payloadNormal.userName)?.userName
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
                                    val user = service.findUserName(payloadNormal.userName)
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
