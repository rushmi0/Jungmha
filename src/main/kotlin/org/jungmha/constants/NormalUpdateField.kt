package org.jungmha.constants

import io.micronaut.core.annotation.Introspected

@Introspected
enum class NormalUpdateField(override val fieldName: String) : EnumField {
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    USER_NAME("userName")
}