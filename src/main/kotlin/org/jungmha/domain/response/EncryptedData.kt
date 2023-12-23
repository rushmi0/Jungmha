package org.jungmha.domain.response

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Deserializable
@Serdeable.Serializable
data class EncryptedData(
    val content: String
)
