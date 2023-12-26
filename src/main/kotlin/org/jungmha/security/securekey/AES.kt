package org.jungmha.security.securekey

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.utils.ShiftTo.HexToByteArray
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class AES {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun encrypt(data: String, sharedKey: String): String {
        val jsonString = objectMapper.writeValueAsString(data)

        val iv = ByteArray(16)
        val random = SecureRandom()
        random.nextBytes(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(sharedKey.HexToByteArray(), "AES"), IvParameterSpec(iv))

        val encryptedData = cipher.doFinal(jsonString.toByteArray(Charsets.UTF_8))

        val ivBase64 = Base64.getEncoder().encodeToString(iv)
        return Base64.getEncoder().encodeToString(encryptedData) + "?iv=" + ivBase64
    }

    fun decrypt(encryptedData: String, sharedKey: String): Map<String, Any> {
        try {
            val (encryptedString, ivBase64) = encryptedData.split("?iv=")
            val ivDecoded = Base64.getDecoder().decode(ivBase64)

            // ตรวจสอบ Shared Key ว่ามีขนาด 32 bytes (128 bits) หรือไม่
            if (sharedKey.HexToByteArray().size != 32) {
                throw IllegalArgumentException("Invalid shared key size. It should be 32 characters (128 bits).")
            }

            val decipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            decipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(sharedKey.HexToByteArray(), "AES"), IvParameterSpec(ivDecoded))

            val decryptedBytes = decipher.doFinal(Base64.getDecoder().decode(encryptedString))
            val decryptedString = decryptedBytes.toString(Charsets.UTF_8)

            val decryptedMap = objectMapper.readValue<Map<String, Any>>(decryptedString)
            return decryptedMap
        } catch (ex: Exception) {
            // จัดการข้อผิดพลาดที่เกิดขึ้น
            ex.printStackTrace() // หรือให้ทำการล็อกหรือรายงานข้อผิดพลาดตามความเหมาะสม
            //throw RuntimeException("Failed to decrypt the data. Reason: ${ex.message}")
            throw RuntimeException("Failed to decrypt the data. Reason: ${ex.localizedMessage}")
        }
    }

}


fun main() {

    val aes = AES()

    val encryptedData = "OpDX/jCYm1GiJC9UgH2QAEQWv8xeupr3nzfugwghegwz7lJyncZD2ZwIihznd3W6OqGr/KPpt1K11y6dHRudxZ6lr/rViut3gsPrER3iXOZVN4MqlK9yZEEeELFwPLsdqwDNAs01EE8qlJePITxhwb+ajqBOuPdkcdiC5naLGo5mFmKEEkRy4pQYrfSMNkFX?iv=pYGE7zJUMjwxsQ07g/0spg=="
    val sharedKey = "3e11810c67157bf584db16bbd85d9e9b339b4469e27390365195379cb2168a78"

    val result = aes.decrypt(
        encryptedData,
        sharedKey
    )

    println(result)

}