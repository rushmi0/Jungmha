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
data class DogWalkBookingsField(
    val bookingID: Int,
    val walkerID: Int,
    val userID: Int,
    val dogID: Int,
    val status: String,
    val timeStart: String,
    val timeEnd: String,
    val duration: Int,
    val timeStamp: String
)
