package org.jungmha.database.record

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Serializable
data class DogWalkersInfo(
    val UserID: Int,
    val profileImage: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val accountType: String,
    val insights: Insights,
    val booking: List<BookingList>
)

@Introspected
@Serdeable.Serializable
data class Insights(
    val countUsed: Int,
    val countReview: Int,
    val totalReview: Int,
    val locationName: String,
    val idCardNumber: String,
    val verify: String,
    val price: PriceData
)