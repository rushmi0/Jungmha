package org.jungmha.database.record

import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable
import jakarta.persistence.Embedded

@Introspected
@Serdeable.Serializable
data class PublicDogWalkerInfo(
    val walkerID: Int,
    val totalReview: Int,
    @Embedded
    val detail: WalkerDetail,
)
