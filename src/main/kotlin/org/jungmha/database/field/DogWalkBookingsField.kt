package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable

@Serdeable.Serializable
data class DogWalkBookingsField(
    val bookingID: Int,
    val walkerID: Int,
    val userID: Int,
    val dogID: Int,
    val status: String,
    val timeStart: String,
    val timeEnd: String,
    val duration: Int,
    val timeStamp: String
)
