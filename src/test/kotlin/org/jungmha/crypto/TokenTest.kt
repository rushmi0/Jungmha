package org.jungmha.crypto

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.jungmha.security.securekey.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class TokenTest @Inject constructor(
    private val token: Token
) {


    @Test
    fun testToken() {
        val tk = "eyJ1c2VyTmFtZSI6IkF1cmEiLCJwZXJtaXNzaW9uIjoidmlldy1vbmx5IiwiZXhwIjoxNzAzNDA5MzU3OTU3LCJpYXQiOjE3MDM0MDgwNjE5NTcsInNpZ25hdHVyZSI6IjMwNDQwMjIwM2U5YmI1MTI4M2I2YTAxZDc3ZGVjZjA5NmUwN2ZiM2Y2OTMyNDljZWJkZDNmYTY2MjMwYjE2MWJkNzg5ZTc0YjAyMjAxNTcwYjVmYmUwNTAxN2VhYmJiZDlhZWI1N2Q0MjMzMDllZDZkYTJiM2M5N2U5ZGI5OWNlNWMzNGYzYjZjZGUyIn0="
        val verify = token.verifyToken(tk)
        Assertions.assertTrue(verify)
    }

}