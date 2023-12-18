package org.jungmha.service

import io.micronaut.context.annotation.Bean
import java.sql.DriverManager

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.annotation.TransactionMode
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.database.statement.DogsWalkersServiceImpl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@MicronautTest(transactionMode = TransactionMode.SINGLE_TRANSACTION)
class DogsWalkersServiceImplTest {

    val jdbcUrl: String = "jdbc:postgresql://localhost:5432/postgres"
    val username: String = "postgres"
    val password: String = "sql@min"
    lateinit var dslContext: DSLContext

    @Test
    fun testDogWalkersAll() = runBlocking {
        DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
            dslContext = DSL.using(connection)
            val rawData = DogsWalkersServiceImpl(dslContext)
            val result = rawData.dogWalkersAll().toString()
            assertEquals(
                "[DogWalkerField(walkerID=1, userID=1, locationName=Park A, idCardNumber=c5234b2313bc9e7bdbf18810e12b41f636588c0eeef8ce4bc31de57c120a7d73, verification=false, priceSmall=50, priceMedium=60, priceBig=70), DogWalkerField(walkerID=2, userID=2, locationName=Park B, idCardNumber=889157fbd2ddf84740fc465765c51f5e3ada347676afce181b2db9c786e6417a, verification=false, priceSmall=45, priceMedium=55, priceBig=65), DogWalkerField(walkerID=3, userID=3, locationName=Park C, idCardNumber=1188e354a2f302b38ad68ed27c6ca3c89b559f7dd6b204b8caae0f13cc723720, verification=false, priceSmall=40, priceMedium=50, priceBig=60)]",
                result
            )
        }
    }

    @Test
    fun testInsert() = runBlocking {
        DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
            dslContext = DSL.using(connection)
            val rawData = DogsWalkersServiceImpl(dslContext)
            val result: Boolean = rawData.insert(
                DogWalkerForm(
                    4,
                    "Park C",
                    "c5234b2313bc9e7bdbf18810e12b41f636588c0eeef8ce4bc31de57c120a7d73",
                    40,
                    50,
                    60
                )
            )
            assertEquals(
                true,
                result
            )
        }
    }


}
