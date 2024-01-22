package org.jungmha.database.record

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable


@Introspected
@Serdeable.Serializable
data class PrivateDogWalkerInfo(
    val walkerID: Int,
    val detail: WalkerDetail,
    val counUsed: Int,
    val countReview: Int,
    val totalReview: Int,
    val contact: WalkerContact,
    val review: List<WalkerReview>
)

@Introspected
@Serdeable.Serializable
data class WalkerDetail(
    val name: String,
    val profileImage: String,
    val verify: Boolean,
    val location: String,
    val price: PriceData
)

@Introspected
@Serdeable.Serializable
data class PriceData(
    val small: Int,
    val medium: Int,
    val big: Int
)

@Introspected
@Serdeable.Serializable
data class WalkerContact(
    val email: String,
    val phoneNumber: String
)

@Introspected
@Serdeable.Serializable
data class WalkerReview(
    val userID: Int,
    val name: String,
    val profileImage: String,
    val rating: Int,
    val reviewText: String
)