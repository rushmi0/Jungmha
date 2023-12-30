package org.jungmha.service

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.jungmha.database.form.UserProfileForm
import org.jungmha.database.statement.UserServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.sql.DriverManager

@MicronautTest
class UserServiceImplTest {

    val jdbcUrl: String = "jdbc:postgresql://localhost:5432/postgres"
    val username: String = "postgres"
    val password: String = "sql@min"
    lateinit var dslContext: DSLContext

    @Test
    fun testFindUser() = runBlocking {
        DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
            dslContext = DSL.using(connection)
            val rawData = UserServiceImpl(dslContext, Dispatchers.IO)
            val result = rawData.findUser( "'1' = '1'; drop table if exists userprofiles;")?.userName
            Assertions.assertNull(result)
        }
    }

    @Test
    fun testUpdateMultiField() = runBlocking {
        DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
            dslContext = DSL.using(connection)
            val rawData = UserServiceImpl(dslContext,  Dispatchers.IO)
            val newData = UserProfileForm(
                "Watcharapol",
                "Phongwilai",
                "ph.watcharapol_st@tni.ac.th",
                "0991857733",
                "Normal"
            )
            val result = rawData.updateMultiField(
                "user1",
                newData
            )

            Assertions.assertEquals(true, result)
        }
    }


//    @Test
//    fun testGetUserInfo() = runBlocking {
//        DriverManager.getConnection(
//            "jdbc:postgresql://localhost:5432/postgres",
//            "postgres",
//            "sql@min").use { connection ->
//            dslContext = DSL.using(connection)
//            val rawData = UserServiceImpl(dslContext, Dispatchers.IO)
//            val result = rawData.getUserInfo("user2")
//            println(result)
//        }
//    }

}