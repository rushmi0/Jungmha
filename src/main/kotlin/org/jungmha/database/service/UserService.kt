package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.IdentityForm
import org.jungmha.database.form.UserProfileForm
import org.jungmha.domain.response.AccountInfo

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface UserService {

    suspend fun getUserInfo(accountName: String): AccountInfo?

    suspend fun userAll(): List<UserProfileField>

    suspend fun findUser(accountName: String): UserProfileField?

    suspend fun insert(payload: IdentityForm): Boolean

    suspend fun updateMultiField(userName: String, payload: UserProfileForm): Boolean

    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}