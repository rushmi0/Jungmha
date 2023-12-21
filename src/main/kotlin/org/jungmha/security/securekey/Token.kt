package org.jungmha.security.securekey

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import java.math.BigInteger
import java.util.*

data class JWTObject(
    val userName: String,
    val exp: BigInteger,
    val iat: BigInteger,
    val signature: String,
)

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class Token{


    fun buildToken(username: String): String? {
        val currentTimeMillis = System.currentTimeMillis()
        val exp = currentTimeMillis + 2_592_000

        val rawObject = JWTObject(
            username,
            exp.toBigInteger(),
            currentTimeMillis.toBigInteger(),
            ""
        )

        val message = jacksonObjectMapper().writeValueAsString(rawObject)
        val signature = ECDSA.sign(
            BigInteger("B885C70F190320D90AAECDBED18E4CB556D52AA9D8CD3E4040EC1582A960C43B", 16),
            message
        )

        val tokenObject = JWTObject(
            username,
            exp.toBigInteger(),
            currentTimeMillis.toBigInteger(),
            signature
        )

        val data = jacksonObjectMapper().writeValueAsString(tokenObject)
        val encode = data.toByteArray(charset("UTF-8"))
        return Base64.getEncoder().encodeToString(encode)
    }


    // eyJ1c2VyTmFtZSI6IlJ1c2htaTAiLCJleHAiOjE3MDMxODA4MTgxNTYsImlhdCI6MTcwMzE3ODIyNjE1Niwic2lnbmF0dXJlIjoiMzA0NTAyMjEwMGU2ZWZkNDBjZGI3YWQ2NGNmY2QxMDVhNjM2Y2JlYzQwOTZmMWI5OGYzMjQwMTFmMDYyZTAxYmM3YjM1YjZlZWYwMjIwMjI2OTNlMTZjZjUwZGNlYmNjYjdjNDdlNzVhMGJmYjNmYzA1NjgwYmYwYTgyNzFkOGI1NmNhNmU1NGJjN2QxMiJ9
    fun verifyToken(token: String): JWTObject {
        val decode = Base64.getDecoder().decode(token)
        val jsonMap = jacksonObjectMapper().readValue<Map<String, Any>>(decode)
        val data = jacksonObjectMapper().writeValueAsString(jsonMap)
        return jacksonObjectMapper().readValue(data)
    }
}

