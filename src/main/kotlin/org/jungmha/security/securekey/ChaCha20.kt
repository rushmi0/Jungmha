package org.jungmha.security.securekey

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.engines.ChaCha7539Engine
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.jungmha.utils.ShiftTo.HexToByteArray
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64

@Bean
@Introspected
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class ChaCha20 {


    private val nonce: ByteArray = ByteArray(12)

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    init {
        SecureRandom().nextBytes(nonce)
    }

    @Throws(java.lang.Exception::class)
    fun encrypt(plainText: String, sharedKey: String): String {
        val cipher = ChaCha7539Engine()
        val keyParam: CipherParameters = KeyParameter(sharedKey.HexToByteArray())
        val parameters: CipherParameters = ParametersWithIV(keyParam, nonce)

        cipher.init(true, parameters)

        val input = plainText.toByteArray(StandardCharsets.UTF_8)
        val output = ByteArray(input.size)
        cipher.processBytes(input, 0, input.size, output, 0)

        val nonceBase64 = Base64.getEncoder().encodeToString(nonce)

        return Base64.getEncoder().encodeToString(output) + "?iv=" + nonceBase64
    }

    @Throws(java.lang.Exception::class)
    fun decrypt(cipherText: String, sharedKey: String): Map<String, Any> {

        val (encryptedString, nonceBase64) = cipherText.split("?iv=")
        val nonceDecoded = Base64.getDecoder().decode(nonceBase64)

        // ตรวจสอบ Shared Key ว่ามีขนาด 32 bytes (128 bits) หรือไม่
        if (sharedKey.HexToByteArray().size != 32) {
            throw IllegalArgumentException("Invalid shared key size. It should be 32 characters (128 bits).")
        }

        val cipher = ChaCha7539Engine()
        val keyParam: CipherParameters = KeyParameter(sharedKey.HexToByteArray())
        val parameters: CipherParameters = ParametersWithIV(keyParam, nonceDecoded)

        cipher.init(false, parameters)

        val cipherTextBytes = Base64.getDecoder().decode(encryptedString)
        val output = ByteArray(cipherTextBytes.size)
        cipher.processBytes(cipherTextBytes, 0, cipherTextBytes.size, output, 0)

        val decryptedString =  String(output, StandardCharsets.UTF_8)
        return objectMapper.readValue<Map<String, Any>>(decryptedString)
    }

}

fun main() {

    val privateKey = "3e11810c67157bf584db16bbd85d9e9b339b4469e27390365195379cb2168a78"
    val chaCha20EncryptDecrypt = ChaCha20()

    val originalText = "Hello, ChaCha20!"
    val encryptedText = chaCha20EncryptDecrypt.encrypt(originalText, privateKey)

    val decryptedText = chaCha20EncryptDecrypt.decrypt(
        encryptedText,
        privateKey
    )

    println("Original Text: $originalText")
    println("Encrypted Text: $encryptedText")
    println("Decrypted Text: $decryptedText")
}
