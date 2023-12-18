package org.jungmha.database.form

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Deserializable
data class DogWalkBookingsForm(
    val walkerID: Int,
    val userID: Int,
    val dogID: Int,
    val status: String,
    val timeStart: String,
    val timeEnd: String,
    val duration: Int,
    val total: Int,
    val timeStamp: String
)
