package org.jungmha.database.record

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Embedded
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

@Introspected
@Serdeable.Serializable
data class NormalInfo(
    val userID: Int,
    val profileImage: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val accountType: String,
    @Embedded
    val booking: List<BookingList>?
)

@Introspected
@Serdeable.Serializable
data class BookingList(
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
    val timeStamp: OffsetDateTime,
    val serviceStatus: String
)