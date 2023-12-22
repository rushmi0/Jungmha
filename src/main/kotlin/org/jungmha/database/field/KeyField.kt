package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class KeyField(
    val keyID: Int,
    val privateKey: String,
    val tag: String
)
