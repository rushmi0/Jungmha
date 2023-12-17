package org.jungmha.security.securekey

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import java.math.BigInteger

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
data class ParametersField(
    val A: BigInteger,
    val B: BigInteger,
    val P: BigInteger,
    val N: BigInteger,
    val G: PointField,
    val H: BigInteger
)
