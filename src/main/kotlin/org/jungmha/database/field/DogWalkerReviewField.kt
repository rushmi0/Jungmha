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
data class DogWalkerReviewField(
    val reviewID: Int,
    val walkerID: Int,
    val userID: Int,
    val rating: Int,
    val reviewText: String
)
