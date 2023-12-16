package org.jungmha.database.statement

import org.jungmha.database.field.DogField
import org.jungmha.database.form.DogForm
import org.jungmha.service.DogsService

class DogsServiceImpl : DogsService {
    override suspend fun insert(id: Int): DogField {
        TODO("Not yet implemented")
    }

    override suspend fun update(params: DogForm): DogField {
        TODO("Not yet implemented")
    }
}