package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.IdentityField

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface IdentityService {

    suspend fun sharedKeyAll(): List<IdentityField>



}