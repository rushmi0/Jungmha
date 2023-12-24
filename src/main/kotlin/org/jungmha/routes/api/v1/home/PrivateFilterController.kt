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
import org.jungmha.domain.response.PrivateDogWalkerInfo
import org.jungmha.security.securekey.Token
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Collectors

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class PrivateFilterController @Inject constructor(
    private val service: DogsWalkersServiceImpl,
    private val token: Token
) {

    @Get(
        uri = "home/filter/auth{?verify,location,name,pSmall,pMedium,pBig,max}",
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

        val verifyToken = token.verifyToken(access)

        try {

            if (verifyToken) {
                // ดึงข้อมูล DogWalker ทั้งหมดจากฐานข้อมูล
                val rawData = service.privateDogWalkersAll()

                // กรองข้อมูลตามเงื่อนไข
                return rawData.stream().filter { data ->

                    // ตรวจสอบค่า verify หากไม่ระบุหรือระบุถูกต้อง
                    val verifyMatch = !verify.isPresent || data.detail.verify.equals(verify.get(), ignoreCase = true)

                    // ตรวจสอบค่า name หากไม่ระบุหรือระบุถูกต้อง
                    val nameMatch = !name.isPresent || data.detail.name.equals(name.get(), ignoreCase = true)

                    // ตรวจสอบค่า location หากไม่ระบุหรือระบุถูกต้อง
                    val locationMatch = !location.isPresent || data.detail.location.equals(location.get(), ignoreCase = true)

                    // ตรวจสอบค่า price ตามช่วงราคาที่ระบุ
                    val pSmallMatch = (!pSmall.isPresent || data.detail.price.small <= pSmall.get())
                    val pMediumMatch = (!pMedium.isPresent || data.detail.price.medium <= pMedium.get())
                    val pBigMatch = (!pBig.isPresent || data.detail.price.big <= pBig.get())

                    // กรองข้อมูลตามเงื่อนไข
                    return@filter verifyMatch && nameMatch && locationMatch && pSmallMatch && pMediumMatch && pBigMatch
                }.limit(max.orElse(Integer.MAX_VALUE).toLong()) // จำกัดจำนวนสูงสุดที่ต้องการแสดงผล
                    .collect(Collectors.toList())
            } else {
                return HttpResponse.badRequest("Invalid Token")
            }

        } catch (e: Exception) {
            LOG.error("Error during getPublicDogWalker operation: ${e.message}", e)
            // เพิ่มการแสดง path file ที่มีปัญหา
            LOG.error("Error file path: ${e.stackTrace.joinToString("\n")}")
            return HttpResponse.serverError("Internal Server Error: ${e.message}")
        }
    }



    companion object {
        private val LOG = LoggerFactory.getLogger(PrivateFilterController::class.java)
    }

}