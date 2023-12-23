package org.jungmha.routes.api.v1.home


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
import org.jungmha.domain.response.DogWalkerInfo
import java.util.*
import java.util.stream.Collectors


@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class HomeController @Inject constructor(
    private val service: DogsWalkersServiceImpl
){


    @Get(
        uri = "home/filter{?verify,location,name,pSmall,pMedium,pBig}",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getWogWalker(
        @QueryValue("verify") verify: Optional<String>,
        @QueryValue("name") name: Optional<String>,
        @QueryValue("location") location: Optional<String>,
        @QueryValue("pSmall") pSmall: Optional<Long>,
        @QueryValue("pMedium") pMedium: Optional<Long>,
        @QueryValue("pBig") pBig: Optional<Long>,
    ): List<DogWalkerInfo> {

        // ดึงข้อมูล DogWalker ทั้งหมด
        val rawData = service.dogWalkersAll()

        // กรองข้อมูลตามเงื่อนไข
        return rawData.stream().filter { data ->

            // ตรวจสอบค่า verify หากไม่ระบุหรือระบุถูกต้อง
            val verifyMatch = !verify.isPresent || data.detail?.verify.equals(verify.get(), ignoreCase = true)

            // ตรวจสอบค่า name หากไม่ระบุหรือระบุถูกต้อง
            val nameMatch = !name.isPresent || data.detail?.name.equals(name.get(), ignoreCase = true)

            // ตรวจสอบค่า location หากไม่ระบุหรือระบุถูกต้อง
            val locationMatch = !location.isPresent || data.detail?.location.equals(location.get(), ignoreCase = true)

            // ตรวจสอบค่า price ตามช่วงราคาที่ระบุ
            val pSmallMatch = (!pSmall.isPresent || data.detail?.price?.small!! <= pSmall.get())

            // กรองข้อมูลตามเงื่อนไข
            return@filter verifyMatch && nameMatch && locationMatch && pSmallMatch
        }.collect(Collectors.toList())
    }


}