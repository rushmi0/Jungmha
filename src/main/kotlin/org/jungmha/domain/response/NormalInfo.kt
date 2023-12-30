package org.jungmha.domain.response

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

@Introspected
@Serdeable.Serializable
data class NormalInfo(
    val UserID: Int,
    val profileImage: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val booking: List<TxBooking>
)

@Introspected
@Serdeable.Serializable
data class TxBooking(
    val bookingID: Int,
    val userName: String,
    val breedName: String,
    val size: String,
    val status: String,
    val bookingDate: LocalDate,
    val timeStart: LocalTime,
    val timeEnd: LocalTime,
    val duration: LocalTime,
    val total: Int,
    val timeStamp: OffsetDateTime
)