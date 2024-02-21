package org.jungmha.database.form

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Deserializable
data class UserProfileForm(
    val firstName: String,
    val lastName: String,
    val locationName: String,
    val email: String,
    val phoneNumber: String,
    val userType: String
)
