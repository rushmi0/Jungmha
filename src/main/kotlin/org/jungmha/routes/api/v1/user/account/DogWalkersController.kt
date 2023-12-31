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
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.domain.response.DogWalkersInfo
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogWalkersController @Inject constructor(
    private val walkerService: DogsWalkersServiceImpl,
    private val userService: UserServiceImpl,
    private val token: Token,
    private val aes: AES
) {


    @Get(
        uri = "auth/user/dogwalkers/{name}",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPersonalInfo(
        //@Header("Access-Token") access: String
        name: String
    ): MutableHttpResponse<out Any?>? {
        return try {
            // ตรวจสอบความถูกต้องของ Token และการอนุญาตของผู้ใช้
//            val userDetails: TokenObject = token.viewDetail(access)
//            val verify = token.verifyToken(access)
//            val permission: String = userDetails.permission
//            val name = userDetails.userName

            // ตรวจสอบความถูกต้องของ Token และสิทธิ์การใช้งาน
            // verify && permission == "view"
            return if (true) {
                coroutineScope {
                    processSearching(name)
                }
            } else {
                NormalController.LOG.warn("Invalid token or insufficient permission for user: $name")
                HttpResponse.badRequest("Invalid token or insufficient permission")
            }
        } catch (e: Exception) {
            NormalController.LOG.error("Error processing getPersonalInfo", e)
            HttpResponse.serverError("Internal server error: ${e.message}")
        }
    }

    private suspend fun processSearching(name: String): MutableHttpResponse<out Any?> {
        // เรียกดูข้อมูลบัญชีผู้ใช้คนนั้นๆ
        val userInfo: DogWalkersInfo? = walkerService.getDogWalkersInfo(name)

        return if (userInfo != null) {
            userInfo.processEncrypting(name)
        } else {
            NormalController.LOG.warn("User info not found for user: $name")
            HttpResponse.notFound("User info not found")
        }
    }

    private suspend fun DogWalkersInfo.processEncrypting(name: String): MutableHttpResponse<out Any?> {
        // นำข้อมูลมา Encrypt
        //val shareKey = userService.findUser(name)?.sharedKey.toString()
        //val encrypted = aes.encrypt(this.toString(), shareKey)
        //return HttpResponse.ok(EncryptedData(encrypted))
        return HttpResponse.ok(this)
    }



    // �� ──────────────────────────────────────────────────────────────────────────────────────── �� \\


    @Patch(
        uri = "auth/user/dogwalkers",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun editPersonalInfo(
        @Header("Access-Token") access: String
    ) {

        // ตรวจสอบความถูกต้องของ Token
        val userDetails: TokenObject = token.viewDetail(access)
        val permission: String = userDetails.permission

        // ตรวจสอบสิทธิ์การใช้งาน
        if (!token.verifyToken(access) || permission != "edit") {
            LOG.warn("Invalid token")
        }

    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(DogWalkersController::class.java)
    }

}