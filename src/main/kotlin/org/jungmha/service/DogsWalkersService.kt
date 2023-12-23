package org.jungmha.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkerField
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.domain.response.DogWalkerInfo

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface DogsWalkersService {

    suspend fun dogWalkersAll() : List<DogWalkerInfo>

    suspend fun insert(payload: DogWalkerForm): Boolean

    suspend fun update(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}