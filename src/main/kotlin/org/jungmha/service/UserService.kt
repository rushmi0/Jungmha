package org.jungmha.service

import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.UserProfileForm

interface UserService {

    suspend fun userAll(): List<UserProfileField>

    suspend fun findUser(accountName: String): UserProfileField?

    suspend fun insert(payload: UserProfileForm): Boolean

    suspend fun update(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}