package org.jungmha.domain.request

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import java.time.LocalTime

@Introspected
@Serdeable.Deserializable
data class DogWalkBookings(
    val walkerID: Int,
    val dogID: Int,
    val timeStart: LocalTime,
    val timeEnd: LocalTime,
)
