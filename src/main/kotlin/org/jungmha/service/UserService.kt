package org.jungmha.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.UserProfileForm

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface UserService {

    suspend fun userAll(): List<UserProfileField>

    suspend fun findUser(accountName: String): UserProfileField?

    suspend fun insert(payload: UserProfileForm): Boolean

    suspend fun update(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}