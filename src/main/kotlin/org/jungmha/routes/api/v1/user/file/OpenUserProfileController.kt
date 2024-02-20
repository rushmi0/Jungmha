package org.jungmha.routes.api.v1.user.file

import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jungmha.database.statement.UserServiceImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.Path

// * Open File Controller

/**
 * คลาสนี้เป็น Controller สำหรับการเปิดไฟล์รูปภาพของผู้ใช้
 */
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
class OpenUserProfileController @Inject constructor(
    private val service: UserServiceImpl
) {

    /**
     * เมธอดสำหรับการเปิด URL รูปภาพของผู้ใช้
     *
     * @param name ชื่อผู้ใช้
     * @return HttpResponse ที่มีข้อมูลของไฟล์รูปภาพ
     */
    @Get(
        uri = "/user/{name}/image/{fingerprint}"
    )
    @Throws(IOException::class)
    suspend fun openImageURL(
        name: String,
        fingerprint: String
    ): HttpResponse<ByteArray> {
        try {
            LOG.info("Thread ${Thread.currentThread().name} executing openImageURL for user: $name")

            // ค้นหาข้อมูลผู้ใช้จากฐานข้อมูล
            val user = service.findUser(name)

            // ตรวจสอบว่ามีข้อมูลผู้ใช้และมีไฟล์รูปภาพหรือไม่
            if (user != null && user.imageProfile != "N/A" && user.imageProfile.isNotBlank()) {
                // อ่านข้อมูลไฟล์รูปภาพเป็น bytes
                val fileBytes = withContext(Dispatchers.IO) {
                    Files.readAllBytes(Path.of(user.imageProfile))
                }

                // ทางระบบทางสื่อมัลติมีเดียคำนวณ Content-Type ของไฟล์
                val contentType = URLConnection.guessContentTypeFromName(user.imageProfile)

                LOG.debug("Image found for user: $name, Content-Type: $contentType")

                // สร้าง HttpResponse และตั้งค่า Header ในการตอบสนอง
                return HttpResponse.ok(fileBytes).header("Content-type", contentType)
            }

            LOG.warn("No image found for user: $name")

            // กรณีไม่พบไฟล์รูปภาพ
            return HttpResponse.notFound()
        } catch (e: Exception) {
            // กรณีเกิดข้อผิดพลาดในระหว่างประมวลผล
            LOG.error("Error processing openImageURL for user: $name", e)
            return HttpResponse.serverError()
        }
    }


    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(OpenUserProfileController::class.java)
    }
}
