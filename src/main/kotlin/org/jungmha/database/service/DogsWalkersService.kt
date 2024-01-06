package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkerField
import org.jungmha.database.record.DogWalkersInfo
import org.jungmha.database.record.PrivateDogWalkerInfo
import org.jungmha.database.record.PublicDogWalkerInfo

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface DogsWalkersService {

    suspend fun getSingleDogWalkersInfo(id: Int): DogWalkerField?

    suspend fun getDogWalkersInfo(accountName: String): DogWalkersInfo?

    suspend fun publicDogWalkersAll() : List<PublicDogWalkerInfo>

    suspend fun privateDogWalkersAll(): List<PrivateDogWalkerInfo>

    suspend fun insert(id: Int): Boolean

    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}