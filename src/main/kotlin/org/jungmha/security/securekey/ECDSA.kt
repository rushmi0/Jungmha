package org.jungmha.security.securekey

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.security.securekey.ECPublicKey.pointRecovery
import org.jungmha.security.securekey.EllipticCurve.addPoint
import org.jungmha.security.securekey.EllipticCurve.modinv
import org.jungmha.security.securekey.EllipticCurve.multiplyPoint

import java.math.BigInteger
import java.security.SecureRandom

/*
* สร้างลายเซ็นและตรวจสอบ ECDSA
* */

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
object ECDSA {


    /*
* https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki
*/

    // * Parameters secp256k1
    private val curveDomain: Secp256K1.CurveParams = Secp256K1.getCurveParams()
    private val N: BigInteger = curveDomain.N


    fun sign(
        privateKey: String,
        message: String
    ): String {
        val signaturePoint: Pair<BigInteger, BigInteger> = signECDSA(
            BigInteger(privateKey, 16),
            BigInteger(message, 16)
        )
        return toDERFormat(signaturePoint)
    }

    fun verify(
        publicKeyPoint: String,
        message: String,
        signature: String
    ): Boolean {
        val signaturePoint = derRecovered(signature)
        return signaturePoint?.let {
            verifyECDSA(publicKeyPoint, BigInteger(message, 16), it)
        } ?: false
    }



    // �� ──────────────────────────────────────────────────────────────────────────────────────── �� \\


    // * สร้างลายเซ็น โดยรับค่า private key และ message ที่ต้องการลงลายเซ็น และคืนค่าเป็นคู่ของ BigInteger (r, s)

    private fun signECDSA(
        privateKey: BigInteger,
        message: BigInteger
    ): Pair<BigInteger, BigInteger> {
        val m = message
        //val k = BigInteger("42854675228720239947134362876390869888553449708741430898694136287991817016610")

        val k = BigInteger(N.bitLength(), SecureRandom()).mod(N - BigInteger.ONE).add(BigInteger.ONE)

        val point: PointField = multiplyPoint(k)

        val kInv: BigInteger = modinv(k, N)

        val r: BigInteger = point.x % N

        var s: BigInteger = (m + r * privateKey) * kInv % N
        // var s: BigInteger = ((m + r * privateKey) * kInv) % N

        // * https://github.com/bitcoin/bips/blob/master/bip-0146.mediawiki
        if (s > N.shiftRight(1)) {
            s = N - s
        }

        return Pair(r, s)
    }

    private fun verifyECDSA(
        publicKeyPoint: String,
        message: BigInteger,
        signature: PointField?
    ): Boolean {
        val (r, s) = signature ?: return false

        val w: BigInteger = modinv(s, N)
        val u1: BigInteger = (message * w) % N
        val u2: BigInteger = (r * w) % N

        val point1: PointField = multiplyPoint(u1)
        val point2: PointField = multiplyPoint(
            u2,
            publicKeyPoint.pointRecovery()
        )

        val point: PointField = addPoint(point1, point2)

        val x: BigInteger = point.x % N

        return x == r
    }


    // �� ──────────────────────────────────────────────────────────────────────────────────────── �� \\


    // * https://github.com/bitcoin/bips/blob/master/bip-0066.mediawiki
    // เมธอดสำหรับแปลงลายเซ็นให้อยู่ในรูปของ DER format
    // โดยรับคู่ของ BigInteger ที่แทนลายเซ็น (r, s) เป็น input
    private fun toDERFormat(signature: Pair<BigInteger, BigInteger>): String {
        // แยกค่า r และ s จากคู่ของ BigInteger
        val (r, s) = signature

        // แปลงค่า r และ s ให้อยู่ในรูปของ bytes
        val rb = r.toByteArray()
        val sb = s.toByteArray()

        // สร้าง bytes สำหรับเก็บค่า r ในรูปแบบ DER
        val der_r = byteArrayOf(0x02.toByte()) + rb.size.toByte() + rb

        // สร้าง bytes สำหรับเก็บค่า s ในรูปแบบ DER
        val der_s = byteArrayOf(0x02.toByte()) + sb.size.toByte() + sb

        // สร้าง bytes สำหรับเก็บลายเซ็นในรูปแบบ DER ที่รวมค่า r และ s
        val der_sig = byteArrayOf(0x30.toByte()) + (der_r.size + der_s.size).toByte() + der_r + der_s

        // แปลง bytes ในรูปของ DER ให้อยู่ในรูปของ hexadecimal string
        return der_sig.joinToString("") { "%02x".format(it) }
    }



    // เมธอดสำหรับถอดรหัสลายเซ็นในรูปของ DER
    // และคืนค่าเป็นคู่ของ BigInteger (r, s)
    private fun derRecovered(derSignature: String): PointField? {
        try {
            // แปลงรหัสลายเซ็นในรูปของ DER จากฐาน 16 เป็น bytes
            val derBytes = derSignature.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

            // ตรวจสอบความถูกต้องของรูปแบบ DER
            if (derBytes.size < 8 || derBytes[0] != 0x30.toByte() || derBytes[2] != 0x02.toByte()) {
                println("รูปแบบ DER ไม่ถูกต้อง")
                return null
            }

            // คำนวณความยาวของ r และ s
            val lenR = derBytes[3].toInt()
            val lenS = derBytes[lenR + 5].toInt()

            // ดึง bytes ที่เกี่ยวข้องกับ r และ s
            val rBytes = derBytes.copyOfRange(4, 4 + lenR)
            val sBytes = derBytes.copyOfRange(6 + lenR, 6 + lenR + lenS)

            // แปลง bytes เป็น BigInteger โดยไม่เอาเครื่องหมายลบ (positive)
            val r = BigInteger(1, rBytes)
            val s = BigInteger(1, sBytes)

            return PointField(r, s)
        } catch (e: Exception) {
            println("ไม่สามารถถอดรหัสลายเซ็น: ${e.message}")
            return null
        }
    }


}
