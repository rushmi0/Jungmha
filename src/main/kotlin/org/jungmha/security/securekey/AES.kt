import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jungmha.utils.ShiftTo.HexToByteArray
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object AESEncryption {

    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun encrypt(data: User, sharedKey: String): String {
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

        val (encryptedString, ivBase64) = encryptedData.split("?iv=")
        val ivDecoded = Base64.getDecoder().decode(ivBase64)

        val decipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        decipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(sharedKey.HexToByteArray(), "AES"), IvParameterSpec(ivDecoded))

        val decryptedString = decipher.doFinal(Base64.getDecoder().decode(encryptedString)).toString(Charsets.UTF_8)

        return objectMapper.readValue(decryptedString)
    }

}

data class User(
    val name: String,
    val age: Int
)

fun main() {
    val sharedKey = "8fda492e3522673b0b0561526e4b1b96b3bdf81484ca5a1db4f30125fc04be54"
    val data = User(
        "Mai na",
        344
    )

    println(data)

    // Encrypt
    val dataToSend = AESEncryption.encrypt(data, sharedKey)
    println("Encrypted data: $dataToSend")

    // Decrypt
    val decryptedData = AESEncryption.decrypt(dataToSend, sharedKey)
    println("Decrypted data: $decryptedData")

    // Access only the property
    val name = decryptedData["name"]
    println("Name: $name")

    val age = decryptedData["age"]
    println("Age: $age")
}
