package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class DogField(
    val dogId: Int,
    val dogImage: String,
    val breedName: String,
    val size: String
)
