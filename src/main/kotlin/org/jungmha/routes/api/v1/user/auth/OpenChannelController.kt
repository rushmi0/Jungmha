package org.jungmha.routes.api.v1.user.auth

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import org.jungmha.database.form.IdentityForm
import org.jungmha.database.statement.ServerKeyServiceImpl
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.domain.request.Identity
import org.jungmha.security.securekey.ECDHkey
import org.jungmha.security.securekey.ECPublicKey.compressed
import org.jungmha.security.securekey.ECPublicKey.toPublicKey
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigInteger

// * Open Channel Controller

/**
 * คลาสนี้เป็น Controller สำหรับการเปิดช่องสื่อสาร (Open Channel)
 */
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class OpenChannelController @Inject constructor(
    private val server: ServerKeyServiceImpl,
    private val service: UserServiceImpl,
    private val ecdh: ECDHkey
) {

    /**
     * สำหรับการเปิดช่องสื่อสาร (Open Channel)
     *
     * @param payload ข้อมูลที่ใช้ในการกำหนดตัวตนของผู้ใช้
     * @return HttpResponse แจ้งเตือนหรือคืนค่าสถานะของการเปิดช่องสื่อสาร
     */
    @Post(
        uri = "/auth/open-channel",
        consumes = [MediaType.APPLICATION_JSON],
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun openChannel(
        @Body payload: Identity
    ): MutableHttpResponse<out Any?>? {

        try {

            // ดึง `Private Key` ของ Server 
            val serverPrivateKey = BigInteger(
                server.getServerKey(1)?.privateKey,
                16
            )

            // แปลง `Public Key` ของ Server เป็นรูปแบบที่มีขนาดเล็ก
            val publicKey: String = serverPrivateKey.toPublicKey().compressed()

            // ดึง `Public Key` ของผู้ใช้จากข้อมูล payload
            val clientPublicKey: String = payload.authenKey

            // ตรวจสอบว่ามีชื่อผู้ใช้นี้อยู่ฐานข้อมูลแล้วหรือไม่
            val checkUserName = service.findUser(payload.userName)?.userName
            if (checkUserName == payload.userName) {
                LOG.warn("Invalid User Name: $checkUserName")
                return HttpResponse.badRequest("Invalid User Name: $checkUserName")
            }

            // คำนวณ Shared Key จาก `Private Key` ของ Server และ `Public Key` ของผู้ใช้
            val sharedKey = ecdh.sharedSecret(
                serverPrivateKey,
                clientPublicKey
            )

            // สร้าง Object IdentityForm เพื่อบันทึกข้อมูลตัวตนของผู้ใช้
            val id = IdentityForm(
                payload.userName,
                clientPublicKey,
                sharedKey
            )

            // บันทึกข้อมูลลงในฐานข้อมูล
            val statement: Boolean = service.insert(id)
            if (statement) {
                LOG.info("Create channel successful for New User")
                return HttpResponse.created(publicKey)
            } else {
                LOG.error("Failed to create a channel for the account")
                return HttpResponse.serverError("Failed to create a channel for the account")
            }
        } catch (e: Exception) {
            LOG.error("Error during open channel operation", e)
            return HttpResponse.serverError("Unexpected error during open channel operation")
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(OpenChannelController::class.java)
    }

}
