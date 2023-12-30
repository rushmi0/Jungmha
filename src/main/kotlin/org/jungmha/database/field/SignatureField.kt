package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable
import java.time.OffsetDateTime

@Serdeable.Serializable
data class SignatureField(
    val sigID: Int,
    val userID: Int,
    val signature: String,
    val nonce : String,
    val timestamp: OffsetDateTime
)
