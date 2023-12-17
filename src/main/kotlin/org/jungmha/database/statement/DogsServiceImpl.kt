package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogField
import org.jungmha.database.form.DogForm
import org.jungmha.service.DogsService

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogsServiceImpl : DogsService {
    override suspend fun insert(id: Int): DogField {
        TODO("Not yet implemented")
    }

    override suspend fun update(params: DogForm): DogField {
        TODO("Not yet implemented")
    }
}