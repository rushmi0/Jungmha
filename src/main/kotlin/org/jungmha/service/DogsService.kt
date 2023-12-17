package org.jungmha.service

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

    suspend fun insert(id: Int): DogField
    suspend fun update(params: DogForm): DogField

}