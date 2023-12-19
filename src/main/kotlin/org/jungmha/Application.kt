package org.jungmha

import io.micronaut.runtime.Micronaut.run
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.info.*

@OpenAPIDefinition(
    info = Info(
        title = "Jungmha",
        version = "0.1"
    )
)
object Api {

}

fun main(args: Array<String>) {
    run(*args)
}
