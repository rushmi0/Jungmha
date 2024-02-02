package org.jungmha.security.securekey

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import org.jungmha.security.securekey.ECPublicKey.compressed
import org.jungmha.security.securekey.ECPublicKey.toPublicKey
import java.math.BigInteger
import java.util.*


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class Token @Inject constructor(
    @Value("\${org.jungmha.security.securekey.secret}") private val secretKey: String,
){


    private val privateKey =  BigInteger(secretKey, 16)
    private val publicKey = privateKey.toPublicKey().compressed()

    private fun buildToken(permission: String, username: String, time: Long? = null): String? {
        val currentTimeMillis = System.currentTimeMillis().toBigInteger()
        val timePoint = time ?: 15
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
        val editToken = buildToken("edit", username, time)!!
        val viewToken = buildToken("view", username, time)!!

        return TokenResponse(
            ServerPublickey(
                publicKey
            ),
            ApiResponseToken(
                editToken,
                viewToken
            )
        )
    }

    fun viewDetail(token: String): TokenObject {
        val decodedBytes = Base64.getDecoder().decode(token)
        val jsonMap = jacksonObjectMapper().readValue<Map<String, Any>>(decodedBytes)

        val userName = jsonMap["userName"] as String
        val permission = jsonMap["permission"] as String
        val exp = jsonMap["exp"].toString().toBigInteger()
        val iat = jsonMap["iat"].toString().toBigInteger()
        val signature = jsonMap["signature"] as String

        return TokenObject(
            userName,
            permission,
            exp,
            iat,
            signature
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
