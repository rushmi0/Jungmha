package org.jungmha.security.securekey

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.security.securekey.ECPublicKey.pointRecovery
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

        // คำนวณค่าจุดบนเส้นโค้งวงรีจาก private key โดยใช้เมธอด `generatePoint` ที่เขียนไว้ใน `ECPublicKey.kt`
        val curvePoint = multiplyPoint(
            privateKey,
            point
        )

        // เอาเฉพาะพิกัด x และแปลงเป็นเลขฐาน 16
        val coordX = curvePoint.x.toString(16)

        // ตรวจสอบว่าค่า coordX มีขนาดน้อยกว่า 64 หรือไม่
        if (coordX.length < 64) {
            // เพิ่ม 0 ไปหน้าสุดจนกว่าจะครบ 64 ตัวอักษร
            return "0".repeat(64 - coordX.length) + coordX
        }

        return coordX
    }

}
