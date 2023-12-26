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
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.statement.UserServiceImpl
import org.jungmha.security.securekey.Token
import org.jungmha.utils.AccountDirectory.deleteFilesInPathAndCheckExistence
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
        return try {
            // ตรวจสอบความถูกต้องของโทเค็นและการอนุญาตของผู้ใช้
            val userDetails = token.viewDetail(access)
            val verify = token.verifyToken(access)
            val user = userDetails.userName
            val permission = userDetails.permission

            // ตรวจสอบความถูกต้องของ Token และสิทธิ์การใช้งาน
            val response = if (verify && permission == "full-control") {
                processFileUpload(user, file)
            } else {
                LOG.warn("Invalid token for file upload")
                HttpResponse.badRequest("Invalid token")
            }

            response
        } catch (e: Exception) {
            LOG.error("Failed to upload file", e)
            HttpResponse.serverError("Failed to upload file: ${e.message}")
        }

    }

    /**
     * เมธอดสำหรับการประมวลผลการอัปโหลดไฟล์
     *
     * @param user ชื่อผู้ใช้
     * @param file ไฟล์ที่จะอัปโหลด
     * @return HttpResponse แสดงผลลัพธ์ของการอัปโหลด
     */
    private suspend fun processFileUpload(user: String, file: CompletedFileUpload): MutableHttpResponse<String> {
        // ค้นหาข้อมูลผู้ใช้จากฐานข้อมูล
        val userProfile: UserProfileField = service.findUser(user) ?: throw IllegalArgumentException("User not found")
        val userId: Int = userProfile.userID
        val typeAccount: String = userProfile.userType
        val profile = userProfile.imageProfile

        // กำหนดตำแหน่งที่จะบันทึกไฟล์
        val targetDirectory = File("$pathDirectory/$typeAccount/usr_$userId/profileImage")
        val targetFile = File("$targetDirectory/${file.filename}")

        // ตรวจสอบว่ามี Directory หรือยัง ถ้าไม่มีให้สร้าง
        if (!targetDirectory.exists()) {
            targetDirectory.mkdirs()
        }

        // กำหนดการตอบสนองขึ้นอยู่กับสถานะของไฟล์โปรไฟล์ผู้ใช้
        val response = if (profile == "N/A") {
            // กรณีที่ยังไม่มีไฟล์โปรไฟล์
            withContext(dispatcher) {
                Files.copy(
                    file.inputStream,
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            }

            // อัปเดตข้อมูลไฟล์โปรไฟล์ในฐานข้อมูล
            updateProfileImage(userId, targetFile)
        } else {
            // กรณีที่มีไฟล์โปรไฟล์อยู่แล้ว
            val check: Boolean = deleteFilesInPathAndCheckExistence(profile)
            if (check) {
                // ลบไฟล์โปรไฟล์เดิมและทำการอัปโหลดไฟล์ใหม่
                withContext(dispatcher) {
                    Files.copy(
                        file.inputStream,
                        targetFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }

                // อัปเดตข้อมูลไฟล์โปรไฟล์ในฐานข้อมูล
                updateProfileImage(userId, targetFile)
            } else {
                // กรณีที่เกิดข้อผิดพลาดในการลบไฟล์เดิม
                HttpResponse.ok("Profile image exists, no need to upload")
            }
        }

        return response
    }

    /**
     * เมธอดสำหรับอัปเดตข้อมูลไฟล์โปรไฟล์ในฐานข้อมูล
     *
     * @param userId รหัสผู้ใช้
     * @param targetFile ไฟล์ที่อัปโหลด
     * @return HttpResponse แสดงผลลัพธ์ของการอัปเดต
     */
    private suspend fun updateProfileImage(userId: Int, targetFile: File): MutableHttpResponse<String> {
        // ทำการอัปเดตข้อมูลไฟล์โปรไฟล์ในฐานข้อมูล
        val statement: Boolean = service.updateSingleField(
            userId,
            "imageProfile",
            targetFile.absolutePath
        )

        // ตอบสนองตามสถานะของการอัปเดต
        return if (statement) {
            LOG.info("File uploaded successfully: ${targetFile.absolutePath}")
            HttpResponse.ok("File uploaded successfully")
        } else {
            LOG.error("Failed to update user profile image field")
            HttpResponse.serverError("Failed to update user profile image field")
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(UploadFileController::class.java)
    }
}
