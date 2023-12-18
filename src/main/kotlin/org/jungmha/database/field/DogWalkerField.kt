package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class DogWalkerField(
    val walkerID: Int,
    val userID: Int,
    val locationName: String,
    val idCardNumber: String,
    val verification: String,
    val priceSmall: Int,
    val priceMedium: Int,
    val priceBig: Int
)
