package org.jungmha.constants

enum class NormalUpdateField(override val key: String) : EnumField {
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    USER_NAME("userName")
}