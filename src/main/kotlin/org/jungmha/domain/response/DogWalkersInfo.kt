package org.jungmha.domain.response

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
    val booking: List<TxBooking>
)

@Introspected
@Serdeable.Serializable
data class Insights(
    val countUsed: Int,
    val countReview: Int,
    val totalReview: Int,
    val location: String,
    val idCard: String,
    val verify: String,
    val price: PriceData
)