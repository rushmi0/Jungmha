package org.jungmha.database.form

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Deserializable
data class DogWalkerForm(
    val userID: Int,
    val locationName: String,
    val countUsed: Int,
    val idCardNumber: String,
    val priceSmall: Int,
    val priceMedium: Int,
    val priceBig: Int
)