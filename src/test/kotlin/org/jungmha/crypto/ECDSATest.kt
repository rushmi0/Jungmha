package org.jungmha.crypto

import org.jungmha.security.securekey.ECDSA
import org.jungmha.security.securekey.ECPublicKey.compressed
import org.jungmha.security.securekey.ECPublicKey.toPublicKey
import org.jungmha.utils.ShiftTo.SHA256
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigInteger
import java.security.SecureRandom

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ECDSATest {

    lateinit var nonce: ByteArray
    lateinit var message: ByteArray

    lateinit var privateKey: BigInteger
    lateinit var publicKeyCompressed: String

    @BeforeEach
    fun setupDefaultValues() {
        privateKey = BigInteger(256, SecureRandom())
        publicKeyCompressed = privateKey.toPublicKey().compressed()
        message = "I am a fish".SHA256()

    }

    @Test
    fun testSignAndVerify() {

        val message = "Hello, ECDSA!"

        // Sign the message
        val signature = ECDSA.sign(privateKey, message)
        val result = ECDSA.verify(message, publicKeyCompressed, signature)

        assertTrue(result)
    }
}
