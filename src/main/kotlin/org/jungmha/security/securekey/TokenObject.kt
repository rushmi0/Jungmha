package org.jungmha.security.securekey

import java.math.BigInteger

data class TokenObject(
    val userName: String,
    val permission: String,
    val exp: BigInteger,
    val iat: BigInteger,
    val signature: String,
)
