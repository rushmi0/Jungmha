package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkBookingsField
import org.jungmha.database.record.DogWalkBookings

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface DogWalkBookingsService {

    suspend fun bookingsAll(): List<DogWalkBookingsField>

    suspend fun insert(userID: Int, payload: DogWalkBookings): Boolean

    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}