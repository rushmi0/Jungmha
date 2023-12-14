package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class DogWalkerField(
    val walkerID: Int,
    val userID: Int,
    val locationName: String,
    val idCardNumber: Int,
    val verification: String,
    val priceSmall: String,
    val priceMedium: String,
    val priceBig: String
)
