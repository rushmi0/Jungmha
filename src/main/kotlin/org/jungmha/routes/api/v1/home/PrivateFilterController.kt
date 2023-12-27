package org.jungmha.routes.api.v1.home

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.QueryValue
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Collectors

// * Private Filter Controller

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class PrivateFilterController @Inject constructor(
    private val service: DogsWalkersServiceImpl,
    private val token: Token
) {

    @Get(
        uri = "/auth/home/filter{?verify,location,name,pSmall,pMedium,pBig,max}",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPrivateDogWalker(
        @Header("Authorization") access: String,
        @QueryValue("name") name: Optional<String>,
        @QueryValue("verify") verify: Optional<String>,
        @QueryValue("location") location: Optional<String>,
        @QueryValue("pSmall") pSmall: Optional<Long>,
        @QueryValue("pMedium") pMedium: Optional<Long>,
        @QueryValue("pBig") pBig: Optional<Long>,
        @QueryValue("max") max: Optional<Int> = Optional.of(Integer.MAX_VALUE)
    ): Any? {

        try {
            // ตรวจสอบความถูกต้องของ Token
            val userDetails: TokenObject = token.viewDetail(access)
            val permission: String = userDetails.permission

            // ตรวจสอบสิทธิ์การใช้งาน
            if (!token.verifyToken(access) || permission != "view-only") {
                LOG.warn("Invalid token for getting Private Dog Walker")
                return HttpResponse.badRequest("Invalid token")
            }

            // ดึงข้อมูล DogWalker ทั้งหมดจากฐานข้อมูล
            val rawData = service.privateDogWalkersAll()

            // กรองข้อมูลตามเงื่อนไข
            val filteredData = rawData.stream()
                .filter { data ->
                    val verifyMatch = !verify.isPresent || data.detail.verify.equals(verify.get(), ignoreCase = true)
                    val nameMatch = !name.isPresent || data.detail.name.equals(name.get(), ignoreCase = true)
                    val locationMatch = !location.isPresent || data.detail.location.equals(location.get(), ignoreCase = true)
                    val pSmallMatch = !pSmall.isPresent || data.detail.price.small <= pSmall.get()
                    val pMediumMatch = !pMedium.isPresent || data.detail.price.medium <= pMedium.get()
                    val pBigMatch = !pBig.isPresent || data.detail.price.big <= pBig.get()

                    verifyMatch && nameMatch && locationMatch && pSmallMatch && pMediumMatch && pBigMatch
                }
                .limit(max.orElse(Integer.MAX_VALUE).toLong())
                .collect(Collectors.toList())

            return HttpResponse.ok(filteredData)
        } catch (e: Exception) {
            LOG.error("Error during getPrivateDogWalker operation: ${e.message}", e)
            LOG.error("Error file path: ${e.stackTrace.joinToString("\n")}")
            return HttpResponse.serverError("Internal Server Error: ${e.message}")
        }
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(PrivateFilterController::class.java)
    }

}
