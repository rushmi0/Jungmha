package org.jungmha.constants

enum class DogWalkerUpdateField(override val key: String) : EnumField  {
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    ID_CARD_NUMBER("idCardNumber"),
    LOCATION("location"),
    SMALL_PRICE("small"),
    MEDIUM_PRICE("medium"),
    BIG_PRICE("big")
}