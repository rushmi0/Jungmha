package org.jungmha.database.field

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
@Introspected
data class DogField(
    val dogId: Int,
    val dogImage: String,
    val breedName: String,
    val size: String
)
