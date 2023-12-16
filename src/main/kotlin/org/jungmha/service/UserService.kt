package org.jungmha.service

import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.UserProfileForm

interface UserService {

    suspend fun findUser(accountName: String): UserProfileField?
    suspend fun insert(payload: UserProfileForm): Boolean

}