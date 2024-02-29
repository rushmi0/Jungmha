package org.jungmha.security.securekey

import io.micronaut.core.annotation.Introspected
import java.math.BigInteger

// * จุดบนเส้นโค้งวงรี มีพิกัด x และ y
@Introspected
data class PointField(
    val x: BigInteger,
    val y: BigInteger
)
