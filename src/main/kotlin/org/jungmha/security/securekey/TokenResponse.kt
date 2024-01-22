package org.jungmha.security.securekey

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class TokenResponse(
    val serverPublicKey: ServerPublickey,
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