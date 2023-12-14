package org.jungmha.domain

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import org.jooq.DSLContext

data class DogsRecord(
    val dog_id: Int,
    val dog_image: String,
    val breed_name: String,
    val size: String
)

data class Dog(
    val id: Long,
    val name: String,
    val breed: String
)

@Controller("/dogs")
class DogsController(
    private val dslContext: DSLContext
) {
//    @Get("/")
//    fun getAllDogs(): List<DogsRecord> {
//
//    }
}