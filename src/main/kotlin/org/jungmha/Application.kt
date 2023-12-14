package org.jungmha

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.info.*


@OpenAPIDefinition(
    info = Info(
            title = "jungmha",
            version = "0.1"
    )
)
object Api {
}


object Application {

//    private val LOG = LoggerFactory.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application.javaClass)
    }

}