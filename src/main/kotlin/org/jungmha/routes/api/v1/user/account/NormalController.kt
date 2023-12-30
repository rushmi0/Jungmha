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
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.domain.response.NormalInfo
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class NormalController @Inject constructor(
    private val  userService: UserServiceImpl,
    private val token: Token
) {


    @Get(
        uri = "auth/user/normal",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPersonalInfo(
        //@Header("Access-Token") access: String
    ): MutableHttpResponse<NormalInfo> {

        // ตรวจสอบความถูกต้องของ Token
        /*val userDetails: TokenObject = token.viewDetail(access)
        val permission: String = userDetails.permission

        // ตรวจสอบสิทธิ์การใช้งาน
        if (!token.verifyToken(access) || permission != "view") {
            LOG.warn("Invalid token")
        }*/


        val user: NormalInfo? = userService.getUserInfo("user1")
        return HttpResponse.ok(user)

    }


    @Patch(
        uri = "auth/user/normal",
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
        val LOG: Logger = LoggerFactory.getLogger(NormalController::class.java)
    }

}