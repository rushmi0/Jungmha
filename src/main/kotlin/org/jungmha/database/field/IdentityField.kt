package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class IdentityField(
    val rootID: Int,
    val userName: String,
    val authenKey: String,
    val sharedKey: String,
)
