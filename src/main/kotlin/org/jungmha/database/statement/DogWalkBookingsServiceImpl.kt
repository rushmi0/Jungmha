package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkerField
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.service.DogWalkBookingsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogWalkBookingsServiceImpl : DogWalkBookingsService {
    override suspend fun bookingsAll(): List<DogWalkerField> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(payload: DogWalkerForm): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun update(id: Int, fieldName: String, newValue: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogWalkBookingsServiceImpl::class.java)
    }

}