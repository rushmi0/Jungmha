package org.jungmha.database.statement

import org.jungmha.database.field.DogWalkerField
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.service.DogwalkersService

class DogwalkersServiceImpl : DogwalkersService {

    override suspend fun dogWalkersAll(): List<DogWalkerField> {
        TODO("Not yet implemented")
    }

    override suspend fun findUser(accountName: String): DogWalkerField? {
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

}