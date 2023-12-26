package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.domain.response.PrivateDogWalkerInfo
import org.jungmha.domain.response.PublicDogWalkerInfo

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface DogsWalkersService {

    suspend fun publicDogWalkersAll() : List<PublicDogWalkerInfo>

    suspend fun privateDogWalkersAll(): List<PrivateDogWalkerInfo>

    suspend fun insert(payload: DogWalkerForm): Boolean

    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}