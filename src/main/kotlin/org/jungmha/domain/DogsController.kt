package org.jungmha.domain

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.serde.annotation.Serdeable
import jakarta.inject.Inject
import nu.studer.sample.tables.Dogs.DOGS
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.jooq.impl.DSL
import java.sql.DriverManager

@Controller("/dogs")
class DogsController(
    @Inject
    private val dslContext: DSLContext
) {
    @Get("/", produces = [MediaType.APPLICATION_JSON])
    fun getAllDogs(): List<DogDetail> { // แก้ที่นี่เป็น List<DogDetail>
        // ดึงข้อมูลจากตาราง DOGS
        val result: Result<Record> = dslContext.select().from(DOGS).fetch()

        // แปลงข้อมูลใน result เป็น List<DogDetail>
        return result.map { record ->
            DogDetail(
                record[DOGS.DOG_ID],
                record[DOGS.DOG_IMAGE],
                record[DOGS.BREED_NAME],
                record[DOGS.SIZE]
            )
        }
    }
}

@Serdeable.Serializable
data class DogDetail(
    val dogId: Int,
    val dogImage: String,
    val breedName: String,
    val size: String
)

fun main() {
    // กำหนด connection string และข้อมูลการเข้าถึงฐานข้อมูล
    val jdbcUrl = "jdbc:postgresql://localhost:5432/postgres"
    val username = "postgres"
    val password = "sql@min"

    // เชื่อมต่อฐานข้อมูล
    DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
        // สร้าง DSLContext จาก connection
        val dslContext = DSL.using(connection)

        // เรียกใช้ getAllDogs() จาก DogsController
        val dogsController = DogsController(dslContext)
        val result: List<DogDetail> = dogsController.getAllDogs()

        // แสดงผลลัพธ์ทั้งหมดทาง console
        println(result)
    }
}
