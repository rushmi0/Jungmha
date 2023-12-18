package org.jungmha

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.info.*
import org.slf4j.LoggerFactory


@OpenAPIDefinition(
    info = Info(
        title = "jungmha",
        version = "0.1"
    )
)
object Api {

}

object Application {
    private val LOG = LoggerFactory.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        LOG.info("Thread ${Thread.currentThread().name} executing main in Application")
        Micronaut.run(Application.javaClass)
    }
}
