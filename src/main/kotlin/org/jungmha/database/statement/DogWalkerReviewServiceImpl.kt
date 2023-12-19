package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkerReviewField
import org.jungmha.database.form.DogWalkerReviewForm
import org.jungmha.service.DogWalkerReviewService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogWalkerReviewServiceImpl : DogWalkerReviewService {
    override suspend fun dogWalkerReviewAll(): List<DogWalkerReviewField> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(payload: DogWalkerReviewForm): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun update(id: Int, fieldName: String, newValue: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogWalkerReviewServiceImpl::class.java)
    }

}