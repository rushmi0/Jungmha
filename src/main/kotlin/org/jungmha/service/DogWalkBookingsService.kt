package org.jungmha.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkerField
import org.jungmha.database.form.DogWalkerForm

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface DogWalkBookingsService {

    suspend fun bookingsAll(): List<DogWalkerField>

    suspend fun insert(payload: DogWalkerForm): Boolean

    suspend fun update(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}