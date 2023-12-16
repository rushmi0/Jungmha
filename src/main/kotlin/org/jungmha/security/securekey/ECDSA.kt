package org.jungmha.security.securekey

import org.jungmha.security.securekey.ECPublicKey.pointRecovery
import org.jungmha.security.securekey.EllipticCurve.addPoint
import org.jungmha.security.securekey.EllipticCurve.modinv
import org.jungmha.security.securekey.EllipticCurve.multiplyPoint
import java.math.BigInteger
import java.security.SecureRandom

/*
* สร้างลายเซ็นและตรวจสอบ ECDSA
* */

object ECDSA {


    /*
* https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki
*/

    // * Parameters secp256k1
    private val curveDomain: Secp256K1.CurveParams = Secp256K1.getCurveParams()
    private val N: BigInteger = curveDomain.N


    fun sign() {

    }

    fun verify() {

    }



    // �� ──────────────────────────────────────────────────────────────────────────────────────── �� \\


    // * สร้างลายเซ็น โดยรับค่า private key และ message ที่ต้องการลงลายเซ็น และคืนค่าเป็นคู่ของ BigInteger (r, s)

    fun signECDSA(
        privateKey: BigInteger,
        message: BigInteger
    ): Pair<BigInteger, BigInteger> {
        val m = message
        //val k = BigInteger("42854675228720239947134362876390869888553449708741430898694136287991817016610")

        val k = BigInteger(256, SecureRandom())

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

    fun verifyECDSA(
        publicKeyPoint: String,
        message: BigInteger,
        signature: Pair<BigInteger, BigInteger>
    ): Boolean {
        val (r, s) = signature

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
    fun toDERencode(signature: Pair<BigInteger, BigInteger>): String {

        val (r, s) = signature

        val rb = r.toByteArray()
        val sb = s.toByteArray()

        val der_r = byteArrayOf(0x02.toByte()) + rb.size.toByte() + rb
        val der_s = byteArrayOf(0x02.toByte()) + sb.size.toByte() + sb

        val der_sig = byteArrayOf(0x30.toByte()) + (der_r.size + der_s.size).toByte() + der_r + der_s

        return der_sig.joinToString("") { "%02x".format(it) }
    }

    fun derRecovered(derSignature: String): Pair<BigInteger, BigInteger>? {
        try {
            val derBytes = derSignature.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            if (derBytes.size < 8 || derBytes[0] != 0x30.toByte() || derBytes[2] != 0x02.toByte()) {
                return null
            }

            val lenR = derBytes[3].toInt()
            val lenS = derBytes[lenR + 5].toInt()

            val rBytes = derBytes.copyOfRange(4, 4 + lenR)
            val sBytes = derBytes.copyOfRange(6 + lenR, 6 + lenR + lenS)

            val r = BigInteger(1, rBytes)
            val s = BigInteger(1, sBytes)

            return Pair(r, s)
        } catch (e: Exception) {
            println("ไม่สามารถถอดรหัสลายเซ็น: ${e.message}")
            return null
        }
    }


}