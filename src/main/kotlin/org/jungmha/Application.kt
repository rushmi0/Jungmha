package org.jungmha

import io.micronaut.runtime.Micronaut.run
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.*
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme


@SecurityScheme(
    type = SecuritySchemeType.APIKEY,
    name = "Access-Token",
    `in` = SecuritySchemeIn.HEADER
)
@OpenAPIDefinition(
    info = Info(
        title = "Jungmha API Document",
        version = "0.1",
        description = "เอกสารนี้เป็นคู่มือการใช้งาน API",
        termsOfService = "https://github.com/rushmi0/Jungmha",
        license = License(
            name = "MIT License",
            url = "https://github.com/rushmi0/Jungmha/blob/backend/LICENSE"
        ),
        contact = Contact(
            name = "Rushmi0",
            url = "https://snort.social/nprofile1qqswfvkxfu8yu49tkdx4vfxdqs8qtmx80uxyvlxydckvf4d7nz478cccayvnq",
        )
    ),
    security = [
        SecurityRequirement(
            name = "Access-Token",
            scopes = ["view", "edit"]
        )
    ]
)
object Api {

}

fun main(args: Array<String>) {
    run(*args)
}
