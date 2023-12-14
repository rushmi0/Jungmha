package org.jungmha.database.form

import io.micronaut.serde.annotation.Serdeable.Deserializable

@Deserializable
data class UserProfileForm(
    val imageProfile: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val authenKey: String,
    val userType: String
)
