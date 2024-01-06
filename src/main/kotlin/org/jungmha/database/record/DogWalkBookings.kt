package org.jungmha.database.record

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate
import java.time.LocalTime

@Introspected
@Serdeable.Deserializable
data class DogWalkBookings(
    val walkerID: Int,
    val dogID: Int,
    val bookingDate: LocalDate,
    val timeStart: LocalTime,
    val timeEnd: LocalTime,
)
