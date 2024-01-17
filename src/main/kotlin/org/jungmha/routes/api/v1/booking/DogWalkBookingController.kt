package org.jungmha.routes.api.v1.booking

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.inject.Inject
import kotlinx.coroutines.coroutineScope
import org.jungmha.constants.DogWalkBookingsValidate
import org.jungmha.constants.NormalValidateField
import org.jungmha.database.statement.DogWalkBookingsServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.database.record.DogWalkBookings
import org.jungmha.database.record.EncryptedData
import org.jungmha.routes.api.v1.user.auth.RegisterController.Companion.validateDecryptedData
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.securekey.TokenObject
import org.jungmha.security.xss.XssDetector
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.ArrayDeque

// * Dog Walk Booking Controller

/**
 * คลาสนี้เป็น Controller สำหรับการจองบริการพาเดินสุนัขเดินเล่น
 */
//@SecurityRequirement(name = "Access-Token")
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogWalkBookingController @Inject constructor(
    private val bookingService: DogWalkBookingsServiceImpl,
    private val userService: UserServiceImpl,
    private val token: Token,
    private val aes: AES
) {

    /**
     * เมธอดสำหรับการทำการจองบริการพาเดินสุนัขเดินเล่น
     *
     * @param access ข้อมูล Token ที่ใช้ในการตรวจสอบสิทธิ์
     * @param payload ข้อมูลการจองบริการ
     * @return HttpResponse สำหรับผลลัพธ์ของการจอง
     */
    @Operation(
        responses = [
            ApiResponse(
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = DogWalkBookings::class)
                    )
                ]
            )
        ]
    )
    @Post(
        uri = "/auth/user/booking",
        consumes = [MediaType.APPLICATION_JSON]
    )
    suspend fun booking(
        @Header("Access-Token") access: String,
        @Body payload: EncryptedData
    ): MutableHttpResponse<out Any?>? {
        return try {
            // ตรวจสอบความถูกต้องของ Token
            val userDetails: TokenObject = token.viewDetail(access)
            val verify = token.verifyToken(access)
            val name: String = userDetails.userName
            val permission: String = userDetails.permission

            // ตรวจสอบความถูกต้องของ Token และสิทธิ์การใช้งาน
            return if (verify && permission == "edit") {
                coroutineScope {
                    processBooking(name, payload)
                }
            } else {
                LOG.warn("Invalid token or insufficient permission for user: $name")
                HttpResponse.badRequest("Invalid token or insufficient permission")
            }

        } catch (e: IllegalArgumentException) {
            LOG.error("Failed to Booking: ${e.message} \n> ${e.stackTrace}")
            HttpResponse.badRequest(e.message)
        } catch (e: Exception) {
            LOG.error("Failed to Booking \n${e.message}")
            HttpResponse.serverError("Failed to Booking: ${e.message}")
        }
    }




    private fun buildUpdateQueue(decryptedData: Map<String, Any?>): Queue<String> {
        val updateQueue = ArrayDeque<String>()
        for (record in DogWalkBookingsValidate.entries) {
            if (decryptedData[record.fieldName] != null) {
                updateQueue.add(record.fieldName)
            }
        }
        return updateQueue
    }



    /**
     * เมธอดที่ใช้ในการประมวลผลข้อมูลการจอง
     *
     * @param payload ข้อมูลการจองบริการ
     * @param user ชื่อผู้ใช้ที่ทำการจอง
     * @return HttpResponse สำหรับผลลัพธ์ของการจอง
     */
    private suspend fun processBooking(
        name: String,
        payload: EncryptedData,
    ): MutableHttpResponse<out Any?>? {
        try {
            val userInfo = userService.findUser(name)
                ?: return HttpResponse.badRequest("User not found")

            val userId: Int = userInfo.userID
            val shareKey = userInfo.sharedKey

            val decryptedData: Map<String, Any?> = aes.decrypt(payload.content, shareKey)

            val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val formatterTime = DateTimeFormatter.ofPattern("HH:mm")

            val bookings = DogWalkBookings(
                walkerID = decryptedData["walkerID"] as Int,
                //userID = decryptedData["userID"] as Int,
                dogID = decryptedData["dogID"] as Int,
                bookingDate = LocalDate.parse(decryptedData["bookingDate"] as String, formatterDate),
                timeStart = LocalTime.parse(decryptedData["timeStart"] as String, formatterTime),
                timeEnd = LocalTime.parse(decryptedData["timeEnd"] as String, formatterTime),
            )

            // ตรวจสอบค่า null และ XSS
            val validationResponse: MutableHttpResponse<out Any?> = validateDecryptedData(
                DogWalkBookingsValidate.entries.toTypedArray(),
                decryptedData
            )

            if (validationResponse.status != HttpStatus.OK) {
                return validationResponse
            }

            // ทำการบันทึกการจอง
            return if (bookingService.insert(userId, bookings)) {
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
