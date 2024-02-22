package org.jungmha.crypto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

import org.jungmha.database.form.UserProfileForm
import org.jungmha.security.securekey.ChaCha20
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ChaCha20Test {

    private val chacha = ChaCha20()
    private val privateKey = "0000000000000000000000000000000000000000000000000000000000000001"

    private val originalObject = UserProfileForm(
        firstName = "สมหมาย",
        lastName = "ใจหมา",
        email = "sample1@gmail.com",
        locationName = "เขตวัฒนา",
        phoneNumber = "0987654321",
        userType = "Normal"
    )

    private val objectMapper: ObjectMapper = jacksonObjectMapper()
    private val jsonString: String = objectMapper.writeValueAsString(originalObject)

    @Test
    fun testEncryptAndDecrypt() {

        // Encrypt
        val encryptedText: String = chacha.encrypt(jsonString, privateKey)

        // Decrypt
        val decryptedText = chacha.decrypt(encryptedText, privateKey)
        val decryptedUserProfileForm = objectMapper.convertValue(decryptedText, UserProfileForm::class.java)

        // Verify
        assertEquals(originalObject, decryptedUserProfileForm)
    }

}