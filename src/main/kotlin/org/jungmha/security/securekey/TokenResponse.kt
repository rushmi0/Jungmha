package org.jungmha.security.securekey

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class TokenResponse(
    val token: List<ApiResponseToken>
)

@Serdeable.Serializable
data class ApiResponseToken(
    val edit: String,
    val view: String
)
