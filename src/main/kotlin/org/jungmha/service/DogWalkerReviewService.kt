package org.jungmha.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkerReviewField
import org.jungmha.database.form.DogWalkerReviewForm

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface DogWalkerReviewService {

    suspend fun dogWalkerReviewAll(): List<DogWalkerReviewField>

    suspend fun insert(payload: DogWalkerReviewForm): Boolean

    suspend fun update(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean


}