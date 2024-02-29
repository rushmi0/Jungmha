package org.jungmha.database.field

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.time.OffsetDateTime

@Serdeable.Serializable
@Introspected
data class SignatureField(
    val sigID: Int,
    val userID: Int,
    val signature: String,
    val nonce : String,
    val timestamp: OffsetDateTime
)
