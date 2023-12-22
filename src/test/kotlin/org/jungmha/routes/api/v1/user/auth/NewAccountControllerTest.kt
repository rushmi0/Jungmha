package org.jungmha.routes.api.v1.user.auth


import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.jungmha.database.form.UserProfileForm
import org.jungmha.database.statement.UserServiceImpl
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.MDC

@MicronautTest
class NewAccountControllerTest {

    @Inject
    lateinit var userService: UserServiceImpl // You might need to inject the service for testing

    @Inject
    lateinit var httpClient: HttpClient

    @Inject
    lateinit var server: EmbeddedServer

    @AfterEach
    fun cleanup() {
        MDC.clear()
    }

    @Test
    fun testSignUpEndpoint() {
        val payload = UserProfileForm(
            "img.png",
            "Watcharapol",
            "Phongwilai",
            "ph.watcharapol_st@gmail.com",
            "1234567653",
            "Normal"
        )

        val request = HttpRequest.POST(
            "/api/v1/user/sign-up",
            payload
        )
            .contentType(MediaType.APPLICATION_JSON_TYPE)

        val response = httpClient.toBlocking().exchange(request, String::class.java)

        assertEquals(HttpStatus.CREATED, response.status)
        assertEquals("Account created successfully", response.body())
    }
}