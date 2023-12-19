package org.jungmha.database.form

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalTime
import java.time.OffsetDateTime

@Introspected
@Serdeable.Deserializable
data class DogWalkBookingsForm(
    val walkerID: Int,
    val userID: Int,
    val dogID: Int,
    val status: String,
    val timeStart: LocalTime,
    val timeEnd: LocalTime,
    val duration: LocalTime,
    val total: Int,
    val timeStamp: OffsetDateTime
)
