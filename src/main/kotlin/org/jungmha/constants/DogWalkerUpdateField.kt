package org.jungmha.constants

enum class DogWalkerUpdateField(override val fieldName: String) : EnumField {
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    COUNT_USED("countUsed"),
    ID_CARD_NUMBER("idCardNumber"),
    LOCATION("location"),
    SMALL_PRICE("small"),
    MEDIUM_PRICE("medium"),
    BIG_PRICE("big")
}