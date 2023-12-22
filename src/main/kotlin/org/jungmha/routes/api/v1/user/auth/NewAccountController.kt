//package org.jungmha.routes.api.v1.user.auth
//
//import io.micronaut.context.annotation.Bean
//import io.micronaut.http.HttpResponse
//import io.micronaut.http.MediaType
//import io.micronaut.http.MutableHttpResponse
//import io.micronaut.http.annotation.Body
//import io.micronaut.http.annotation.Controller
//import io.micronaut.http.annotation.Post
//import io.micronaut.runtime.http.scope.RequestScope
//import io.micronaut.scheduling.TaskExecutors
//import io.micronaut.scheduling.annotation.ExecuteOn
//import org.jungmha.database.form.UserProfileForm
//import org.jungmha.security.xss.XssDetector
//import org.jungmha.utils.AccountDirectory
//import org.slf4j.LoggerFactory
//import jakarta.inject.Inject
//import org.jungmha.database.statement.ServerKeyServiceImpl
//import org.jungmha.database.statement.UserServiceImpl
//import org.jungmha.security.securekey.ECPublicKey.compressed
//import org.jungmha.security.securekey.ECPublicKey.toPublicKey
//import org.jungmha.security.securekey.Token
//import org.slf4j.MDC
//import java.math.BigInteger
//
//
//@Controller("api/v1")
//@Bean
//@RequestScope
//@ExecuteOn(TaskExecutors.IO)
//class SignUpNewAccount @Inject constructor(
//    private val server: ServerKeyServiceImpl,
//    private val service: UserServiceImpl,
//    private val token: Token
//) {
//
//    @Post(
//        uri = "/auth/sign-up",
//        consumes = [MediaType.APPLICATION_JSON],
//        produces = [MediaType.APPLICATION_JSON]
//    )
//    suspend fun signUp(
//        @Body payload: UserProfileForm,
//    ): MutableHttpResponse<out Any?>? {
//        try {
//            LOG.info("Thread ${Thread.currentThread().name} executing signUp")
//            MDC.put("thread -> ", Thread.currentThread().name)
//
//            val serverPrivateKey = BigInteger(
//                server.getServerKey(1)?.privateKey,
//                16
//            )
//            val publicKey: String = serverPrivateKey.toPublicKey().compressed()
//
//            if (payload.userName.isNotBlank()) {
//                if (XssDetector.containsXss(payload.userName)
//                ) {
//                    return HttpResponse.badRequest("Cross-site scripting detected")
//                } else {
//
//                    if () {
//                        val userForm = UserProfileForm(
//                            payload.imageProfile,
//                            payload.firstName,
//                            payload.lastName,
//                            payload.email,
//                            payload.phoneNumber,
//                            payload.userType
//                        )
//
//                        LOG.debug("Received JSON payload: {}", payload)
//
//                        val statement: Boolean = service.insert(userForm)
//
//                        if (statement) {
//                            val user = service.findUser(payload.userName)
//                            val userId = user?.userID
//                            val token = token.buildTokenPair(payload.userName)
//                            if (userId != null) {
//                                AccountDirectory.createDirectory(payload.userType, userId)
//                            }
//                            return HttpResponse.created(token)
//                        } else {
//                            return HttpResponse.serverError("Failed to create the account")
//                        }
//                    }
//                }
//            } else {
//                return HttpResponse.badRequest("Invalid input data")
//            }
//        } catch (e: Exception) {
//            LOG.error("Error creating the account: ${e.message} $e")
//            return HttpResponse.serverError("Failed to create the account")
//        } finally {
//            MDC.clear()
//        }
//    }
//
//
//    companion object {
//        private val LOG = LoggerFactory.getLogger(SignUpNewAccount::class.java)
//    }
//
//
//}