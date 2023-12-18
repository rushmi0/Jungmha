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
        println("c5234b2313bc9e7bdbf18810e12b41f636588c0eeef8ce4bc31de57c120a7d73".length)
        Micronaut.run(Application.javaClass)
    }

}