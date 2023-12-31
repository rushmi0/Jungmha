package org.jungmha.database.record

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Introspected
@Serdeable.Serializable
data class PublicDogWalkerInfo(
    val walkerID: Int,
    val detail: WalkerDetail,
)
