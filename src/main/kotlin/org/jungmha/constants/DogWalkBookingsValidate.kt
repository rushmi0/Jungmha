package org.jungmha.constants

import io.micronaut.core.annotation.Introspected

@Introspected
enum class DogWalkBookingsValidate(override val fieldName: String) : EnumField {
    WALKER_ID("walkerID"),
    DOG_ID("dogID"),
    BOOKING_DATE("bookingDate"),
    TIME_START("timeStart"),
    TIME_END("timeEnd")
}
