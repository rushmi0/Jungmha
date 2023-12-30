package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogField
import org.jungmha.database.form.DogForm

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface DogsService {

    suspend fun findDog(dogName: String): DogField?

    suspend fun dogsAll(): List<DogField>

    suspend fun insert(payload: DogForm): Boolean

    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}