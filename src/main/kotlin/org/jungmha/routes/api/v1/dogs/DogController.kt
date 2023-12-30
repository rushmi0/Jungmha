package org.jungmha.routes.api.v1.dogs

import io.micronaut.context.annotation.Bean
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import org.jungmha.database.field.DogField
import org.jungmha.database.statement.DogsServiceImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogController @Inject constructor(
    private val service: DogsServiceImpl
) {

    @Get(
        uri = "/dogs",
        produces = [MediaType.APPLICATION_JSON]
    )
    suspend fun getDogs(): List<DogField> {
        return try {
            service.dogsAll()
        } catch (e: Exception) {
            LOG.error("Error retrieving dogs", e)
            emptyList()
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogController::class.java)
    }

}
