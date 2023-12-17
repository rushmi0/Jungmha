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
data class DogField(
    val dogId: Int,
    val dogImage: String,
    val breedName: String,
    val size: String
)
