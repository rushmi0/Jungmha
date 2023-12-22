package org.jungmha.service

import org.jungmha.database.field.KeyField

interface ServerKeyService {

    suspend fun getServerKey(id: Int): KeyField?

    suspend fun insert(privateKey: String): Boolean

    suspend fun update(id: Int, fieldName: String, newValue: String): Boolean

    suspend fun delete(id: Int): Boolean

}