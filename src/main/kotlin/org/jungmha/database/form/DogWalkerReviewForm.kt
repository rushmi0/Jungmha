package org.jungmha.database.form

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Deserializable
data class DogWalkerReviewForm(
    val rating: Int,
    val reviewText: String
)
