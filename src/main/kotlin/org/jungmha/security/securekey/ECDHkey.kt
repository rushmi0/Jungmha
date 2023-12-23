package org.jungmha.security.securekey

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.security.securekey.ECPublicKey.compressed
import org.jungmha.security.securekey.ECPublicKey.pointRecovery
import org.jungmha.security.securekey.ECPublicKey.toPublicKey
import org.jungmha.security.securekey.EllipticCurve.multiplyPoint
import java.math.BigInteger

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class ECDHkey {

    /**
     * `ECDH Key` มีชื่อเรียกเฉพาะคือ `ECDH Shared Secret` หรือ `Elliptic Curve Diffie-Hellman Shared Secret`
     * `ECDH Shared Secret` คือคีย์ลับที่สร้างขึ้นระหว่างสองฝ่ายโดยใช้อัลกอริทึม ECDH (Elliptic Curve Diffie-Hellman)
     * โดยใช้คีย์สาธารณะและคีย์ส่วนตัวของฝ่ายแต่ละฝ่าย นำมาคำนวณกัน จะได้คีย์ลับที่เป็นค่าเดียวกัน ซึ่งจะนำไปใช้เป็นคีย์สำหรับการเข้ารหัสและถอดรหัสข้อมูล โดยใช้วิธีการเข้ารหัสแบบสมมาตร (Symmetric Encryption)
     * */

    // ใช้สำหรับสร้าง Shared Key ระหว่าง 2 ฝ่าย เรียกว่า ECDH (Elliptic Curve Diffie-Hellman)
    fun sharedSecret(
        // Private Key ของตัวเอง
        privateKey: BigInteger,
        // Public Key ของฝ่ายตรงข้าม
        publicKey: String
    ): String {

        // แปลง public key ให้อยู่ในรูปของ PointField นั้นก็คือ (x, y) ซึ่งเป็นพิกัดบนเส้นโค้งวงรี
        val point: PointField = publicKey.pointRecovery()
            ?: throw IllegalArgumentException("Invalid or unsupported public key format")

        // คำนวณค่าจุดบนเส้นโค้งวงรีจาก private key โดยใช้เมธอด `generatePoint` ที่เขียนไว้ใน `ECPublicKey.kt`
        val curvePoint = multiplyPoint(
            privateKey,
            point
        )

        // เอาเฉพาะพิกัด x และแปลงเป็นเลขฐาน 16 ก่อนคืนค่ากลับไป
        return curvePoint.x.toString(16)
    }

}

fun main() {

    val privateKey = BigInteger("b1bd351d555e1781134d0b406e58145277a67696d3ad2511c98e4627dafcf5b2", 16)
    val publicKey = privateKey.toPublicKey().compressed()

    println(publicKey)

    val ecdh = ECDHkey()

    val sharedKey = ecdh.sharedSecret(
        privateKey,
        "02f06f2580404d439896f002a6b77cbbc518ace934345c69b831a234a6dfbe54c8"
    )
    println(sharedKey)

}