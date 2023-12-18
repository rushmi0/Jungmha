package org.jungmha.security.securekey

import java.math.BigInteger

data class ParametersField(
    val A: BigInteger,
    val B: BigInteger,
    val P: BigInteger,
    val N: BigInteger,
    val G: PointField,
    val H: BigInteger
)
