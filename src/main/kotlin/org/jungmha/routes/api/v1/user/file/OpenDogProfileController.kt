package org.jungmha.routes.api.v1.user.file

import io.micronaut.context.annotation.Bean
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import org.jungmha.database.statement.DogsServiceImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.Path

// * Open Dog Profile Controller

/**
 * คลาสนี้เป็น Controller สำหรับการแสดงภาพโปรไฟล์ของสุนัข
 */
@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class OpenDogProfileController @Inject constructor(
    private val service: DogsServiceImpl
) {

    /**
     * สำหรับการแสดงภาพโปรไฟล์ของสุนัข
     *
     * @param name ชื่อของสุนัข
     * @return HttpResponse พร้อมกับข้อมูลภาพ
     */
    @Get(uri = "/dog/{name}/image/{fingerprint}/{file}")
    @Throws(IOException::class)
    suspend fun openImageDogsURL(
        name: String,
        fingerprint: String,
        file: String
    ): HttpResponse<ByteArray> {
        return try {
            LOG.info("Thread ${Thread.currentThread().name} executing openImageDogsURL for dog: $name")

            val dogs = service.findDog(name)

            if (dogs != null && dogs.dogImage != "N/A" && dogs.dogImage.isNotBlank()) {
                val fileBytes = Files.readAllBytes(Path.of(dogs.dogImage))

                val contentType = URLConnection.guessContentTypeFromName(dogs.dogImage)

                LOG.debug("Image found for dog: $name, Content-Type: $contentType")

                return HttpResponse.ok(fileBytes).header("Content-type", contentType)
            }

            LOG.warn("No image found for dog: $name")
            HttpResponse.notFound()
        } catch (e: Exception) {
            LOG.error("Error processing openImageDogsURL for dog: $name", e)
            HttpResponse.serverError()
        }
    }

    // Logger สำหรับการแสดงภาพโปรไฟล์ของสุนัข
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(OpenDogProfileController::class.java)
    }
}
