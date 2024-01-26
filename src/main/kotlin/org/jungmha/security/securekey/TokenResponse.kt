package org.jungmha.security.securekey

import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Embedded

@Serdeable.Serializable
data class TokenResponse(
    @Embedded
    val serverPublicKey: ServerPublickey,
    @Embedded
    val token: ApiResponseToken
)

@Serdeable.Serializable
data class ApiResponseToken(
    val edit: String,
    val view: String
)

@Serdeable.Serializable
data class ServerPublickey(
    val publicKey: String
)