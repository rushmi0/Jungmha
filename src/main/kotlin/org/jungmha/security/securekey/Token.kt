package org.jungmha.security.securekey

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import org.jungmha.database.statement.ServerKeyServiceImpl
import org.jungmha.security.securekey.ECPublicKey.compressed
import org.jungmha.security.securekey.ECPublicKey.toPublicKey
import java.math.BigInteger
import java.util.*

data class TokenObject(
    val userName: String,
    val permission: String, // full-control, view-only
    val exp: BigInteger,
    val iat: BigInteger,
    val signature: String,
)

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class Token {


    private val privateKey =  BigInteger("b1bd351d555e1781134d0b406e58145277a67696d3ad2511c98e4627dafcf5b2", 16)
    private val publicKey = privateKey.toPublicKey().compressed()

    private fun buildToken(permission: String, username: String, time: Long? = null): String? {
        val currentTimeMillis = System.currentTimeMillis().toBigInteger()
        val timePoint = time ?: 5
        val exp = currentTimeMillis + 86_400.toBigInteger() * timePoint.toBigInteger()

        val rawObject = TokenObject(
            username,
            permission,
            exp,
            currentTimeMillis,
            ""
        )
        val message = jacksonObjectMapper().writeValueAsString(rawObject)
        val signature = ECDSA.sign(privateKey, message)

        return encodeToken(rawObject.copy(signature = signature))
    }

    private fun encodeToken(tokenObject: TokenObject): String {
        val data = jacksonObjectMapper().writeValueAsString(tokenObject)
        val encode = data.toByteArray(Charsets.UTF_8)
        return Base64.getEncoder().encodeToString(encode)
    }

    fun buildTokenPair(username: String, time: Long? = null): TokenResponse {
        val fullControlToken = buildToken("full-control", username, time)!!
        val viewOnlyToken = buildToken("view-only", username, time)!!

        return TokenResponse(
            listOf(
                ApiResponseToken(
                    fullControlToken,
                    viewOnlyToken
                )
            )
        )
    }

    fun verifyToken(token: String): Boolean {
        try {
            val decodedBytes = Base64.getDecoder().decode(token)
            val jsonMap = jacksonObjectMapper().readValue<Map<String, Any>>(decodedBytes)

            val userName = jsonMap["userName"] as String
            val permission = jsonMap["permission"] as String
            val exp = jsonMap["exp"].toString().toBigInteger()
            val iat = jsonMap["iat"].toString().toBigInteger()
            val signature = jsonMap["signature"] as String

            val currentTimeMillis = System.currentTimeMillis().toBigInteger()

            if (isTokenExpired(currentTimeMillis, iat, exp)) {
                return false
            }

            val raw = TokenObject(
                userName,
                permission,
                exp,
                iat,
                ""
            )
            val message = jacksonObjectMapper().writeValueAsString(raw)

            return ECDSA.verify(message, publicKey, signature)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun isTokenExpired(currentTimeMillis: BigInteger, iat: BigInteger, exp: BigInteger): Boolean {
        return currentTimeMillis < iat || currentTimeMillis > exp
    }
}