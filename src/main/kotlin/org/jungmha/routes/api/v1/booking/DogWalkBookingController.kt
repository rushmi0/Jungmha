package org.jungmha.routes.api.v1.booking

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.inject.Inject
import org.jungmha.database.statement.DogWalkBookingsServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.domain.request.DogWalkBookings
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.slf4j.LoggerFactory

// * Dog Walk Booking Controller

/**
 * คลาสนี้เป็น Controller สำหรับการจองบริการพาเดินสุนัขเดินเล่น
 */
@SecurityRequirement(name = "Access-Token")
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogWalkBookingController @Inject constructor(
    private val bookingService: DogWalkBookingsServiceImpl,
    private val userService: UserServiceImpl,
    private val token: Token
) {

    /**
     * เมธอดสำหรับการทำการจองบริการพาเดินสุนัขเดินเล่น
     *
     * @param access ข้อมูล Token ที่ใช้ในการตรวจสอบสิทธิ์
     * @param payload ข้อมูลการจองบริการ
     * @return HttpResponse สำหรับผลลัพธ์ของการจอง
     */
    @Post(
        uri = "/auth/user/booking",
        consumes = [MediaType.APPLICATION_JSON]
    )
    suspend fun booking(
        @Header("Access-Token") access: String,
        @Body payload: DogWalkBookings
    ): MutableHttpResponse<String> {
        return try {
            // ตรวจสอบความถูกต้องของ Token
            val userDetails: TokenObject = token.viewDetail(access)
            val user: String = userDetails.userName
            val permission: String = userDetails.permission

            // ตรวจสอบสิทธิ์การใช้งาน
            if (!token.verifyToken(access) || permission != "edit") {
                LOG.warn("Invalid token for Booking")
                return HttpResponse.badRequest("Invalid token")
            }

            processBooking(payload, user)
        } catch (e: IllegalArgumentException) {
            LOG.error("Failed to Booking: ${e.message}")
            return HttpResponse.badRequest(e.message)
        } catch (e: Exception) {
            LOG.error("Failed to Booking", e)
            return HttpResponse.serverError("Failed to Booking: ${e.message}")
        }
    }

    /**
     * เมธอดที่ใช้ในการประมวลผลข้อมูลการจอง
     *
     * @param payload ข้อมูลการจองบริการ
     * @param user ชื่อผู้ใช้ที่ทำการจอง
     * @return HttpResponse สำหรับผลลัพธ์ของการจอง
     */
    private suspend fun processBooking(payload: DogWalkBookings, user: String): MutableHttpResponse<String> {
        try {
            // ค้นหา UserID จากชื่อผู้ใช้
            val userId: Int = userService.findUser(user)?.userID
                ?: throw IllegalArgumentException("User not found")

            // ทำการบันทึกการจอง
            return if (bookingService.insert(userId, payload)) {
                HttpResponse.ok("Booking successfully")
            } else {
                HttpResponse.serverError("Failed to Booking field")
            }

        } catch (e: IllegalArgumentException) {
            LOG.error("Failed to Booking: ${e.message}")
            return HttpResponse.badRequest(e.message)
        } catch (e: Exception) {
            LOG.error("Failed to Booking", e)
            return HttpResponse.serverError("Failed to Booking: ${e.message}")
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DogWalkBookingController::class.java)
    }

}
