package win.rushmi0.jungmha.constants

import io.micronaut.core.annotation.Introspected

@Introspected
enum class NormalUpdateField(override val fieldName: String) : win.rushmi0.jungmha.constants.EnumField {
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    USER_NAME("userName")
}