package org.jungmha.routes.api.v1.response

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class TokenResponse(
    val token: List<ApiResponseToken>
)

@Serdeable.Serializable
data class ApiResponseToken(
    val fullControl: String,
    val viewOnly: String
)
