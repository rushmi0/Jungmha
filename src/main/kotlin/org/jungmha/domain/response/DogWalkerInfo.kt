package org.jungmha.domain.response

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Serializable
data class DogWalkerInfo(
    val walkerID: Int,
    val detail: WalkerDetail?,
    val contact: WalkerContact
)

@Introspected
@Serdeable.Serializable
data class WalkerDetail(
    val name: String,
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