package org.jungmha.database.field

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
@Introspected
data class IdentityField(
    val rootID: Int,
    val userName: String,
    val authenKey: String,
    val sharedKey: String,
)
