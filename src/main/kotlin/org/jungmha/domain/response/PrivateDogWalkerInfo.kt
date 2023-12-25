package org.jungmha.domain.response

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable


//PrivateDogWalkerInfo
@Introspected
@Serdeable.Serializable
data class PrivateDogWalkerInfo(
    val walkerID: Int,
    val detail: WalkerDetail,
    val contact: WalkerContact,
    val review: WalkerReview
)

@Introspected
@Serdeable.Serializable
data class WalkerDetail(
    val name: String,
    val profileImage: String,
    val verify: String,
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