package org.jungmha.constants

enum class NormalValidateField(override val key: String) : EnumField {
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    USER_TYPE("userType")
}