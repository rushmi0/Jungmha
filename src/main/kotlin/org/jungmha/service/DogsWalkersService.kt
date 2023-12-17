package org.jungmha.service

import org.jungmha.database.field.DogWalkerField
import org.jungmha.database.form.DogWalkerForm

interface DogsWalkersService {

    suspend fun dogWalkersAll() : List<DogWalkerField>

    suspend fun findUser(accountName: String): DogWalkerField?

    suspend fun insert(payload: DogWalkerForm): Boolean

    suspend fun update(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}