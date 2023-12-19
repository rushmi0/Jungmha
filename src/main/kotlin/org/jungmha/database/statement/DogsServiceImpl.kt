package org.jungmha.database.statement

import org.jungmha.database.field.DogField
import org.jungmha.database.form.DogForm
import org.jungmha.service.DogsService

class DogsServiceImpl : DogsService {
    override suspend fun dogsAll(): List<DogField> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(payload: DogForm): DogField {
        TODO("Not yet implemented")
    }

    override suspend fun update(payload: DogForm): DogField {
        TODO("Not yet implemented")
    }
}