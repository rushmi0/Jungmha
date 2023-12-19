package org.jungmha.service


import java.sql.DriverManager

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.annotation.TransactionMode
import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
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
                "[DogWalkerField(walkerID=1, userID=1, locationName=Park A, idCardNumber=c5234b2313bc9e7bdbf18810e12b41f636588c0eeef8ce4bc31de57c120a7d73, verification=true, priceSmall=50, priceMedium=60, priceBig=70), DogWalkerField(walkerID=2, userID=2, locationName=Park B, idCardNumber=889157fbd2ddf84740fc465765c51f5e3ada347676afce181b2db9c786e6417a, verification=true, priceSmall=45, priceMedium=55, priceBig=65)]",
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


    @Test
    fun testUpdate() = runBlocking {
        DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
            dslContext = DSL.using(connection)
            val dogWalkersService = DogsWalkersServiceImpl(dslContext)

            val idToUpdate = 4
            val fieldName = "locationName"
            val newValue = "New Park"

            val result = dogWalkersService.update(idToUpdate, fieldName, newValue)
            assertEquals(true, result)
        }
    }


    @Test
    fun testDelete() = runBlocking {
        DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
            dslContext = DSL.using(connection)
            val dogWalkersService = DogsWalkersServiceImpl(dslContext)

            val idToDelete = 4

            val result = dogWalkersService.delete(idToDelete)
            assertEquals(true, result)
        }
    }


}
