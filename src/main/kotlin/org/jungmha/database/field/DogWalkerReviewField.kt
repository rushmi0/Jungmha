package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class DogWalkerReviewField(
    val reviewID: Int,
    val walkerID: Int,
    val userID: Int,
    val rating: Int,
    val reviewText: String
)
