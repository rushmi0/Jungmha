package org.jungmha.crypto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.jungmha.database.form.UserProfileForm
import org.jungmha.security.securekey.ChaCha20
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@MicronautTest
class ChaCha20Test @Inject constructor(
    private val chacha: ChaCha20
) {

    private val privateKey = "3e11810c67157bf584db16bbd85d9e9b339b4469e27390365195379cb2168a78"
    private val originalText = UserProfileForm(
        firstName = "สมหมาย",
        lastName = "ใจหมา",
        email = "sample1@gmail.com",
        phoneNumber = "0987654321",
        userType = "Normal"
    )

    @Test
    fun testEncryptAndDecrypt() {

        val objectMapper: ObjectMapper = jacksonObjectMapper()
        val jsonString: String = objectMapper.writeValueAsString(originalText)

        println("JSON String: $jsonString")

        // Convert JSON String back to Kotlin object
        val convertedObject: UserProfileForm = objectMapper.readValue(jsonString)
        println("Converted Object: $convertedObject")

        // Encrypt
        val encryptedText: String = chacha.encrypt(jsonString, privateKey)

        // Decrypt
        val decryptedText = chacha.decrypt(encryptedText, privateKey)

        // Verify
        assertEquals(originalText, decryptedText)
    }

}