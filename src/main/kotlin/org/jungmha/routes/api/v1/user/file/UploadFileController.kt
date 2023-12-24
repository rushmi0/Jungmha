package org.jungmha.routes.api.v1.user.file

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.Part
import io.micronaut.http.annotation.Post
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.security.securekey.Token
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

// * Upload File Controller

/**
 * คลาสนี้เป็น Controller สำหรับการอัปโหลดไฟล์ของผู้ใช้
 */
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class UploadFileController @Inject constructor(
    taskDispatcher: CoroutineDispatcher?,
    private val service: UserServiceImpl,
    private val token: Token
) {

    // ตัวแปร dispatcher สำหรับใช้งาน Coroutine ใน IO context
    private val dispatcher: CoroutineDispatcher = taskDispatcher ?: Dispatchers.IO
    private val pathDirectory = "src/main/resources/images/account"

    /**
     * เมธอดสำหรับการอัปโหลดไฟล์
     *
     * @param access ข้อมูล Token สำหรับตรวจสอบสิทธิ์การอัปโหลด
     * @param file ไฟล์ที่จะอัปโหลด
     * @return HttpResponse แสดงผลลัพธ์ของการอัปโหลด
     */
    @Post(
        uri = "/auth/user/upload",
        consumes = [MediaType.MULTIPART_FORM_DATA]
    )
    @Throws(IOException::class)
    suspend fun uploadFile(
        @Header("Authorization") access: String,
        @Part file: CompletedFileUpload
    ): MutableHttpResponse<String> {

        // ตรวจสอบ Token ว่าถูกต้องหรือไม่
        val verify = token.verifyToken(access)

        // ดึงข้อมูล UserName จาก Token
        val user = token.viewDetail(access).userName
        val permission = token.viewDetail(access).permission

        return try {
            if (verify && permission == "full-control") {
                // ค้นหาข้อมูลผู้ใช้จากฐานข้อมูล
                val user = service.findUser(user) ?: throw IllegalArgumentException("User not found")

                // ดึงข้อมูล UserID และประเภทของผู้ใช้
                val userId = user.userID
                val typeAccount = user.userType

                // กำหนดตำแหน่งที่จะบันทึกไฟล์
                val targetDirectory = File("$pathDirectory/$typeAccount/usr_$userId/profileImage/")
                val targetFile = File(targetDirectory, file.filename)

                // ตรวจสอบว่ามี Directory หรือยัง ถ้าไม่มีให้สร้าง
                if (!targetDirectory.exists()) {
                    targetDirectory.mkdirs()
                }

                // อัปโหลดไฟล์
                withContext(dispatcher) {
                    Files.copy(
                        file.inputStream,
                        targetFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }

                LOG.info("File uploaded successfully: ${targetFile.absolutePath}")
                HttpResponse.ok("File uploaded successfully")
            } else {
                LOG.warn("Invalid token for file upload")
                HttpResponse.badRequest("Invalid token")
            }
        } catch (e: Exception) {
            LOG.error("Failed to upload file", e)
            HttpResponse.serverError("Failed to upload file: ${e.message}")
        }
    }

    companion object {
        // Logger สำหรับการอัปโหลดไฟล์
        val LOG: Logger = LoggerFactory.getLogger(UploadFileController::class.java)
    }

}
