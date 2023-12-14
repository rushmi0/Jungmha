package org.jungmha.database.form

import io.micronaut.serde.annotation.Serdeable.Deserializable

@Deserializable
data class DogWalkerReviewForm(
    val rating: Int,
    val reviewText: String
)
