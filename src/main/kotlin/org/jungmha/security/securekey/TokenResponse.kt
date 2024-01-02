package org.jungmha.security.securekey

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class TokenResponse(
    val server_public_key: String,
    val token: ApiResponseToken
)

@Serdeable.Serializable
data class ApiResponseToken(
    val edit: String,
    val view: String
)
