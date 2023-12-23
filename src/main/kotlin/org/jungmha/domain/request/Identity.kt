package org.jungmha.domain.request

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Deserializable
data class Identity(
    val userName: String,
    val authenKey: String
)