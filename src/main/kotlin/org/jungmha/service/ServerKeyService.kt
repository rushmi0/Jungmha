package org.jungmha.service

import org.jungmha.database.field.KeyField

fun interface ServerKeyService {

    fun getServerKey(id: Int): KeyField?


}