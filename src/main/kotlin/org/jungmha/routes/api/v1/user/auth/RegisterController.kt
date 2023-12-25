package org.jungmha.routes.api.v1.user.auth

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
import org.jungmha.database.form.UserProfileForm
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.domain.response.EncryptedData
import org.jungmha.security.securekey.AES
import org.jungmha.security.securekey.Token
import org.jungmha.security.xss.XssDetector
import org.jungmha.utils.AccountDirectory
import org.slf4j.MDC
import org.slf4j.LoggerFactory
import jakarta.inject.Inject


// * RegisterController

/**
 * คลาสนี้เป็น Controller สำหรับดำเนินการลงทะเบียนผู้ใช้
 */
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class RegisterController @Inject constructor(
    private val service: UserServiceImpl,
    private val token: Token,
    private val aes: AES
) {

    /**
     * สำหรับการลงทะเบียนผู้ใช้
     *
     * @param name ชื่อผู้ใช้
     * @param payload ข้อมูลที่ถูกเข้ารหัสแล้วที่จะถูกใช้ในการลงทะเบียน
     * @return HttpResponse แจ้งเตือนหรือคืนค่าสถานะของการลงทะเบียน
     */
    @Post(
        uri = "/auth/sign-up",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun signUp(
        @Header("UserName") name: String,
        @Body payload: EncryptedData
    ): MutableHttpResponse<out Any?>? {
        return try {
            LOG.info("Thread ${Thread.currentThread().name} executing signUp")
            MDC.put("thread -> ", Thread.currentThread().name)

            // ดึงกุญแจใช้ในการเข้ารหัสจากฐานข้อมูล
            val shareKey = service.findUser(name)?.sharedKey.toString()

            // ถอดรหัสข้อมูล
            val decrypted = aes.decrypt(payload.content, shareKey)

            // สร้าง Object UserProfileForm จากข้อมูลที่ถอดรหัสได้
            val data = UserProfileForm(
                decrypted["firstName"].toString(),
                decrypted["lastName"].toString(),
                decrypted["email"].toString(),
                decrypted["phoneNumber"].toString(),
                decrypted["userType"].toString()
            )

            // ตรวจสอบความปลอดภัยของข้อมูลต่อ XSS
            if (
                XssDetector.containsXss(data.firstName) ||
                XssDetector.containsXss(data.lastName) ||
                XssDetector.containsXss(data.email) ||
                XssDetector.containsXss(data.phoneNumber) ||
                XssDetector.containsXss(data.userType)
            ) {
                HttpResponse.badRequest("Cross-site scripting detected")
            } else {
                // อัปเดตข้อมูลผู้ใช้
                val statement: Boolean = service.updateMultiField(
                    name,
                    data
                )

                if (statement) {
                    // ดึงข้อมูลผู้ใช้หลังจากการอัปเดต
                    val user = service.findUser(name)
                    val userId = user?.userID

                    // สร้าง Token และสร้างไดเร็กทอรีสำหรับผู้ใช้ใหม่ (ถ้ามี)
                    val token = token.buildTokenPair(name, 99)
                    if (userId != null) {
                        AccountDirectory.createDirectory(data.userType, userId)
                    }

                    // ส่งคำตอบสำหรับการลงทะเบียนเรียบร้อย พร้อมกับส่ง `Access Token`
                    HttpResponse.created(token)
                } else {
                    // การลงทะเบียนล้มเหลวเนื่องจากการอัปเดตข้อมูลไม่สำเร็จ
                    LOG.error("Failed to create the account: Update operation failed")
                    HttpResponse.serverError("Failed to create the account: Update operation failed")
                }
            }
        } catch (e: Exception) {
            LOG.error("Error creating the account: ${e.message}", e)
            HttpResponse.serverError("Failed to create the account: ${e.message}")
        } finally {
            MDC.clear()
        }
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(RegisterController::class.java)
    }

}
