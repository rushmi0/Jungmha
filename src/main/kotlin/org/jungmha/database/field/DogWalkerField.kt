package org.jungmha.database.field

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
@Introspected
data class DogWalkerField(
    val walkerID: Int,
    val userID: Int,
    //val locationName: String,
    val idCardNumber: String,
    val verification: Boolean,
    val priceSmall: Int,
    val priceMedium: Int,
    val priceBig: Int
)
