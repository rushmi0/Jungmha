package org.jungmha.security.securekey


import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import java.math.BigInteger

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)object Secp256K1 {

    // * Secp256K1 curve parameters:
    private val A = BigInteger.ZERO
    private val B = BigInteger.valueOf(7)
    private val P = BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16)
    private val N = BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16)
    private val G = PointField(
        BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16),
        BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16)
    )

    data class CurveParams(
        val A: BigInteger,
        val B: BigInteger,
        val P: BigInteger,
        val N: BigInteger,
        val G: PointField
    )

    fun getCurveParams(): CurveParams {
        return CurveParams(A, B, P, N, G)
    }

}