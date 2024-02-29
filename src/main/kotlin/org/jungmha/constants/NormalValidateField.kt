package org.jungmha.constants

import io.micronaut.core.annotation.Introspected

@Introspected
enum class NormalValidateField(override val fieldName: String) : EnumField {
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    USER_TYPE("userType")
}