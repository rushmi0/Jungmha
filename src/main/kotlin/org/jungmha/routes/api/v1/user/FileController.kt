package org.jungmha.routes.api.v1.user

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

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class FileController @Inject constructor(
    taskDispatcher: CoroutineDispatcher?,
    private val service: UserServiceImpl,
    private val token: Token
) {

    private val dispatcher: CoroutineDispatcher = taskDispatcher ?: Dispatchers.IO
    private val pathDirectory = "src/main/resources/images/account"

    @Post(
        uri = "/auth/user/upload",
        consumes = [MediaType.MULTIPART_FORM_DATA]
    )
    @Throws(IOException::class)
    suspend fun uploadFile(
        @Header("Authorization") access: String,
        @Part file: CompletedFileUpload
    ): MutableHttpResponse<String> {

        val verifyToken = token.verifyToken(access)
        val user = token.viewDetail(access).userName

        return try {
            if (verifyToken) {
                val user = service.findUser(user) ?: throw IllegalArgumentException("User not found")

                val userId = user.userID
                val typeAccount = user.userType

                val targetDirectory = File("$pathDirectory/$typeAccount/usr_$userId/profileImage/")
                val targetFile = File(targetDirectory, file.filename)

                if (!targetDirectory.exists()) {
                    targetDirectory.mkdirs()
                }

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



//    @Throws(IOException::class)
//    suspend fun openFile() {
//
//    }
//
//
//    @Throws(IOException::class)
//    suspend fun openImage() {
//
//    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(FileController::class.java)
    }



}