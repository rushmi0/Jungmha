package org.jungmha.routes.api.v1.home.filter

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.QueryValue
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.jungmha.database.record.PrivateDogWalkerInfo
import org.jungmha.database.record.PublicDogWalkerInfo
import org.jungmha.routes.api.v1.user.account.DogWalkersController
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Collectors

// * Private Filter Controller

/**
 * คลาสนี้เป็น Controller สำหรับการดึงข้อมูล Dog Walker จากฐานข้อมูลโดยให้บริการเฉพาะผู้ใช้ที่มีสิทธิ์ view-only
 */

@Tag(
    name = "Home filter",
    //description = "API ที่เกี่ยวข้องกับ"
)
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class PrivateFilterController @Inject constructor(
    private val service: DogsWalkersServiceImpl,
    private val token: Token
) {

    /**
     * เมธอดสำหรับการดึงข้อมูล Dog Walker จากฐานข้อมูล
     *
     * @param access ข้อมูล Token ที่ใช้ในการตรวจสอบสิทธิ์
     * @param name ชื่อของ Dog Walker (สามารถไม่ระบุ)
     * @param verify สถานะการตรวจสอบข้อมูล (สามารถไม่ระบุ)
     * @param location สถานที่ที่ Dog Walker ทำงาน (สามารถไม่ระบุ)
     * @param pSmall ราคาสำหรับการเดินสุนัขขนเล็ก (สามารถไม่ระบุ)
     * @param pMedium ราคาสำหรับการเดินสุนัขขนกลาง (สามารถไม่ระบุ)
     * @param pBig ราคาสำหรับการเดินสุนัขขนใหญ่ (สามารถไม่ระบุ)
     * @param max จำนวนข้อมูลสูงสุดที่ต้องการแสดงผล (สามารถไม่ระบุ, ค่าเริ่มต้นคือ Integer.MAX_VALUE)
     * @return HttpResponse สำหรับผลลัพธ์ของการดึงข้อมูล Dog Walker
     */
    @Operation(
        summary = "เมธอดสำหรับการดึงข้อมูล Dog Walker จากฐานข้อมูล",
        description = "เมธอดสำหรับการดึงข้อมูล Dog Walker จากฐานข้อมูล",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "HttpResponse สำหรับผลลัพธ์ของการดึงข้อมูล Dog Walker",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = PrivateDogWalkerInfo::class)
                        //schema = Schema(implementation = PublicDogWalkerInfo::class)
                    )
                ]
            )
        ]
    )
    @Get(
        uri = "/auth/home/filter{?verify,location,name,pSmall,pMedium,pBig,max}",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPrivateDogWalker(
        @Header("Access-Token") access: String,
        @QueryValue("name") name: Optional<String>,
        @QueryValue("verify") verify: Optional<String>,
        @QueryValue("location") location: Optional<String>,
        @QueryValue("pSmall") pSmall: Optional<Long>,
        @QueryValue("pMedium") pMedium: Optional<Long>,
        @QueryValue("pBig") pBig: Optional<Long>,
        @QueryValue("max") max: Optional<Int> = Optional.of(Integer.MAX_VALUE)
    ): List<PrivateDogWalkerInfo> {
        LOG.info("Current Class: ${Thread.currentThread().stackTrace[1].className}")
        LOG.info("Executing Method: ${Thread.currentThread().stackTrace[1].methodName}")
        LOG.info("Thread ${Thread.currentThread().name} [ID: ${Thread.currentThread().id}] in state ${Thread.currentThread().state}. Is Alive: ${Thread.currentThread().isAlive}")
        // ตรวจสอบความถูกต้องของ Token
        val userDetails: TokenObject = token.viewDetail(access)
        val verifyToken = token.verifyToken(access)
        val permission: String = userDetails.permission

        return when {

            verifyToken && permission == "view" -> {
                // ดึงข้อมูล Dog Walker ทั้งหมดจากฐานข้อมูล
                val rawData: List<PrivateDogWalkerInfo> = service.privateDogWalkersAll()

                // กรองข้อมูลตามเงื่อนไข
                rawData.stream().filter { data ->
                    val verifyMatch = !verify.isPresent || data.detail.verify.equals(verify.get())
                    val nameMatch = !name.isPresent || data.detail.name.equals(name.get(), ignoreCase = true)
                    val locationMatch = !location.isPresent || data.detail.location.equals(location.get(), ignoreCase = true)
                    val pSmallMatch = (!pSmall.isPresent || data.detail.price.small <= pSmall.get())
                    val pMediumMatch = (!pMedium.isPresent || data.detail.price.medium <= pMedium.get())
                    val pBigMatch = (!pBig.isPresent || data.detail.price.big <= pBig.get())

                    verifyMatch && nameMatch && locationMatch && pSmallMatch && pMediumMatch && pBigMatch

                }.limit(max.orElse(Integer.MAX_VALUE).toLong())
                    .collect(Collectors.toList())
            }

            else -> {
                LOG.warn("Invalid token or insufficient permission for user: ${userDetails.userName}")
                emptyList()
            }

        }

    }

    companion object {
        private val LOG = LoggerFactory.getLogger(PrivateFilterController::class.java)
    }

}
