package org.jungmha.security.securekey

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.starkbank.ellipticcurve.Curve
import com.starkbank.ellipticcurve.Ecdsa
import com.starkbank.ellipticcurve.PrivateKey
import org.jungmha.utils.ShiftTo.ByteArrayToHex
import org.jungmha.utils.ShiftTo.SHA256
import java.math.BigInteger
import java.util.*

data class JWTObject(
    val userName: String,
    val exp: BigInteger,
    val iat: BigInteger,
    val signature: String,
)

class Token {

    private val privateKey = PrivateKey(
        Curve.secp256k1,
        BigInteger("a66679d60de1659086f2138bd275e1d0ef53f143ca814442eb97b94ca9668a20", 16)
    )

    fun buildToken(username: String): String? {
        val currentTimeMillis = System.currentTimeMillis()
        val exp = currentTimeMillis + 2_592_000

        val message = JWTObject(
            username,
            exp.toBigInteger(),
            currentTimeMillis.toBigInteger(),
            ""
        )

        val dataHash = jacksonObjectMapper().writeValueAsString(message).SHA256().ByteArrayToHex()
        val signature = Ecdsa.sign(dataHash, privateKey).toDer().bytes.ByteArrayToHex()

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

    fun verifyToken(token: String): JWTObject {
        val decode = Base64.getDecoder().decode(token)
        val jsonMap = jacksonObjectMapper().readValue<Map<String, Any>>(decode)
        val data = jacksonObjectMapper().writeValueAsString(jsonMap)
        return jacksonObjectMapper().readValue(data)
    }
}

fun main() {
    val userName = "root"
    val authenKey = "028fda492e3522673b0b0561526e4b1b96b3bdf81484ca5a1db4f30125fc04be54"

    val token = Token().buildToken(userName)!!
    println(token)

    val verify = Token().verifyToken(token)
    println(verify)

    val data = JWTObject(
        verify.userName,
        verify.exp,
        verify.iat,
        verify.signature
    )

    println(data)
}
