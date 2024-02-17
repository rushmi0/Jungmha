package org.jungmha.database.field

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
@Introspected
data class DogWalkerReviewField(
    val reviewID: Int,
    val walkerID: Int,
    val userID: Int,
    val rating: Int,
    val reviewText: String
)
