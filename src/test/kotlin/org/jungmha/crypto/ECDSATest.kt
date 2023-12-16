package org.jungmha.crypto

import org.jungmha.security.securekey.PointField
import org.jungmha.utils.ShiftTo.ByteArrayToHex
import org.jungmha.utils.ShiftTo.SHA256
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigInteger

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ECDSATest {

    lateinit var nonce: ByteArray
    lateinit var message: ByteArray

    lateinit var privateKey: BigInteger
    lateinit var publicKeyXHex: String
    lateinit var publicKeyPoint: PointField
    lateinit var publicKeyDynamic: PointField
    lateinit var publicKeyCompressed: String
    lateinit var publicKeyUncompressed: String

    @BeforeEach
    fun setupDefaultValues() {

        privateKey = BigInteger("3f5245b0e22c36ad08850c55065562f711e4740b37a91577f7ccfece0c8ef437", 16)

        publicKeyCompressed = "0201fc369a3265381bbc57a7914bae9cb0f8d80faa4430e90a7c5c11a41a36ce93"
        message = "I am a fish".SHA256()

    }

    @Test
    fun yourTestMethod() {
        println(message.ByteArrayToHex())
    }
}
