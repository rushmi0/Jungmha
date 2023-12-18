package org.jungmha.service

import org.jungmha.database.field.DogField
import org.jungmha.database.form.DogForm

interface DogsService {

    suspend fun insert(id: Int): DogField
    suspend fun update(params: DogForm): DogField

}