package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.SignatureField
import org.jungmha.database.form.SignatureForm

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface SignatureService {

    suspend fun signAll(): List<SignatureField>

    suspend fun checkSign(userName: String, signature: String): Boolean

    suspend fun insert(payload: SignatureForm): Boolean

}