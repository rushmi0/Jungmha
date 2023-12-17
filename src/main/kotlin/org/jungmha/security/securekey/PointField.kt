package org.jungmha.security.securekey

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import java.math.BigInteger

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
// * จุดบนเส้นโค้งวงรี มีพิกัด x และ y
data class PointField(
    val x: BigInteger,
    val y: BigInteger
)
