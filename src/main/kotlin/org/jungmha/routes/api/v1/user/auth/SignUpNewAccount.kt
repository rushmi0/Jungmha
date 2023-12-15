package org.jungmha.routes.api.v1.user.auth

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post

import org.jungmha.database.form.UserProfileForm
import org.jungmha.database.service.UserService
import org.jungmha.security.xss.XssDetector
import org.jungmha.utils.AccountDirectory
import org.slf4j.LoggerFactory

@Controller("api/v1")
class SignUpNewAccount(
    private val service: UserService
) {

    private val LOG = LoggerFactory.getLogger(SignUpNewAccount::class.java)

    @Post(
        uri = "/user/sign-up",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    fun SignUp(
        @Header("AccountType") accountType: String?,
        @Body payload: UserProfileForm
    ): MutableHttpResponse<out Any>? {

        val userName = payload.userName
        val publicKey = payload.authenKey

        try {
            // 'Normal', 'DogWalkers'
            when {

                accountType == "Normal" || accountType == "DogWalkers" -> {

                    if (userName.isNotBlank() && publicKey.isNotBlank()) {
                        // ตรวจสอบ XSS
                        if (XssDetector.containsXss(userName) ||
                            XssDetector.containsXss(publicKey)
                        ) {
                            return HttpResponse.badRequest("ตรวจพบการเขียน Cross-site scripting")
                        } else {

                            val checkUserName = service.findUserName(userName)?.userName

                            if (checkUserName == userName && accountType == "Normal") {
                                return HttpResponse.badRequest("User Name: $checkUserName ใช้งานไม่ได้")
                            } else {
                                val statement: Boolean = service.insert(
                                    UserProfileForm(
                                        payload.imageProfile,
                                        payload.userName,
                                        payload.firstName,
                                        payload.lastName,
                                        payload.email,
                                        payload.phoneNumber,
                                        payload.authenKey,
                                        payload.userType
                                    )
                                )

                                if (statement) {
                                    val user = service.findUserName(userName)
                                    val id = user!!.userID
                                    AccountDirectory.createDirectory("Normal", id)
                                }

                            }

                            if (checkUserName == userName && accountType == "DogWalkers") {
                                return HttpResponse.badRequest("User Name: $checkUserName ใช้งานไม่ได้")
                            } else {
                                val statement: Boolean = service.insert(
                                    UserProfileForm(
                                        payload.imageProfile,
                                        payload.userName,
                                        payload.firstName,
                                        payload.lastName,
                                        payload.email,
                                        payload.phoneNumber,
                                        payload.authenKey,
                                        payload.userType
                                    )
                                )

                                if (statement) {
                                    val user = service.findUserName(userName)
                                    val id = user!!.userID
                                    AccountDirectory.createDirectory("DogWalkers", id)
                                }
                            }

                        }
                    }

                }


            }
        } catch (e: Exception) {
            LOG.error("เกิดข้อผิดพลาดในการสร้างบัญชี: ", e)
        }

        return HttpResponse.created("สร้างบัญชีสำเร็จ")
    }


}