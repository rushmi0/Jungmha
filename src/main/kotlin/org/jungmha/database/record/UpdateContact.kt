package org.jungmha.database.record

import io.micronaut.core.annotation.Introspected

@Introspected
data class UpdateContact(
    val email: String,
    val phoneNumber: String
)