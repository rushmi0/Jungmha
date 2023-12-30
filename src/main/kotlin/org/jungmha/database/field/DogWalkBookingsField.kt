package org.jungmha.database.field

import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

@Serdeable.Serializable
data class DogWalkBookingsField(
    val bookingID: Int,
    val walkerID: Int,
    val userID: Int,
    val dogID: Int,
    val status: String,
    val bookingDate: LocalDate,
    val timeStart: LocalTime,
    val timeEnd: LocalTime,
    val duration: LocalTime,
    val total: Int,
    val timeStamp: OffsetDateTime
)
