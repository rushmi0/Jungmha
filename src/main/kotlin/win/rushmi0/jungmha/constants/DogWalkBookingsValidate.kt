package win.rushmi0.jungmha.constants

import io.micronaut.core.annotation.Introspected

@Introspected
enum class DogWalkBookingsValidate(override val fieldName: String) : win.rushmi0.jungmha.constants.EnumField {
    WALKER_ID("walkerID"),
    DOG_ID("dogID"),
    BOOKING_DATE("bookingDate"),
    TIME_START("timeStart"),
    TIME_END("timeEnd")
}
