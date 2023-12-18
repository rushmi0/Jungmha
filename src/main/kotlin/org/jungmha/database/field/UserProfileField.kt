package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class UserProfileField(
    val userID: Int,
    val imageProfile: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val authenKey: String,
    val createAt: String,
    val userType: String
)
