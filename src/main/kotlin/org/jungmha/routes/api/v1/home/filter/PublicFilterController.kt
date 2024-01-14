package org.jungmha.routes.api.v1.home.filter


import io.micronaut.context.annotation.Bean
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.jungmha.database.record.PublicDogWalkerInfo
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Collectors

// * Public Filter Controller

/**
 * คลาสนี้เป็น Controller สำหรับการกรองข้อมูล Public Dog Walker
 */
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class PublicFilterController @Inject constructor(
    private val service: DogsWalkersServiceImpl
) {

    /**
     * เมธอดสำหรับการดึงข้อมูล Public Dog Walker ตามเงื่อนไขที่ระบุ
     *
     * @param name ชื่อของ Dog Walker (ถ้ามี)
     * @param verify สถานะการตรวจสอบ (ถ้ามี)
     * @param location สถานที่ที่ Dog Walker ทำงาน (ถ้ามี)
     * @param pSmall ราคาต่ำสุดสำหรับขนาดเล็ก (ถ้ามี)
     * @param pMedium ราคาต่ำสุดสำหรับขนาดกลาง (ถ้ามี)
     * @param pBig ราคาต่ำสุดสำหรับขนาดใหญ่ (ถ้ามี)
     * @param max จำนวนข้อมูลสูงสุดที่ต้องการแสดงผล (ถ้าไม่ระบุให้ใช้ค่า Integer.MAX_VALUE)
     * @return ข้อมูลสำหรับผู้ที่ไม่มีบัญชีในระบบ ตามเงื่อนไขที่ระบุ
     */
    @Get(
        uri = "home/filter{?verify,location,name,pSmall,pMedium,pBig,max}",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getPublicDogWalker(
        @QueryValue("name") name: Optional<String>,
        @QueryValue("verify") verify: Optional<String>,
        @QueryValue("location") location: Optional<String>,
        @QueryValue("pSmall") pSmall: Optional<Long>,
        @QueryValue("pMedium") pMedium: Optional<Long>,
        @QueryValue("pBig") pBig: Optional<Long>,
        @QueryValue("max") max: Optional<Int> = Optional.of(Integer.MAX_VALUE)
    ): List<PublicDogWalkerInfo> {
        LOG.info("Current Class: ${Thread.currentThread().stackTrace[1].className}")
        LOG.info("Executing Method: ${Thread.currentThread().stackTrace[1].methodName}")
        LOG.info("Thread ${Thread.currentThread().name} [ID: ${Thread.currentThread().id}] in state ${Thread.currentThread().state}. Is Alive: ${Thread.currentThread().isAlive}")
        return try {
            val rawData = service.publicDogWalkersAll()

             rawData.stream().filter { data ->
                val verifyMatch = !verify.isPresent || data.detail.verify.equals(verify.get(), ignoreCase = true)
                val nameMatch = !name.isPresent || data.detail.name.equals(name.get(), ignoreCase = true)
                val locationMatch = !location.isPresent || data.detail.location.equals(location.get(), ignoreCase = true)
                val pSmallMatch = (!pSmall.isPresent || data.detail.price.small <= pSmall.get())
                val pMediumMatch = (!pMedium.isPresent || data.detail.price.medium <= pMedium.get())
                val pBigMatch = (!pBig.isPresent || data.detail.price.big <= pBig.get())

                verifyMatch && nameMatch && locationMatch && pSmallMatch && pMediumMatch && pBigMatch
            }.limit(max.orElse(Integer.MAX_VALUE).toLong())
                .collect(Collectors.toList())

        } catch (e: Exception) {
            LOG.error("Error during getPublicDogWalker operation: ${e.message}", e)
            LOG.error("Error file path: ${e.stackTrace.joinToString("\n")}")
            emptyList()
        }
    }

    companion object {
        // Logger สำหรับการดึงข้อมูล Public Dog Walker
        private val LOG = LoggerFactory.getLogger(PublicFilterController::class.java)
    }

}
