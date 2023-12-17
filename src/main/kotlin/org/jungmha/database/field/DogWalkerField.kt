package org.jungmha.database.field

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.serde.annotation.Serdeable

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Serdeable.Serializable
data class DogWalkerField(
    val walkerID: Int,
    val userID: Int,
    val locationName: String,
    val idCardNumber: Long,
    val verification: String,
    val priceSmall: Int,
    val priceMedium: Int,
    val priceBig: Int
)
