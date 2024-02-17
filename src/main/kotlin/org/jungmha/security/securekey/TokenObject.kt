package org.jungmha.security.securekey

import io.micronaut.core.annotation.Introspected
import java.math.BigInteger
@Introspected
data class TokenObject(
    val userName: String,
    val permission: String,
    val exp: BigInteger,
    val iat: BigInteger,
    val signature: String,
)
