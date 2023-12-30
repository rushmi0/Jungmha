package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable
import java.time.OffsetDateTime

@Serdeable.Serializable
data class UserProfileField(
    val userID: Int,
    val authenKey: String,
    val sharedKey: String,
    val imageProfile: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val createAt: OffsetDateTime,
    val userType: String
)
