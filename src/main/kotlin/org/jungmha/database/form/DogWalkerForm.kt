package org.jungmha.database.form

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Deserializable
data class DogWalkerForm(
    val locationName: String,
    val idCardNumber: Int,
    val priceSmall: String,
    val priceMedium: String,
    val priceBig: String
)