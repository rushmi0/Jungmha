package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jungmha.database.field.KeyField
import org.jungmha.infra.database.tables.Serverkey.SERVERKEY
import org.jungmha.service.ServerKeyService

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class ServerKeyServiceImpl(
    private val query: DSLContext
) : ServerKeyService {

    override suspend fun getServerKey(id: Int): KeyField? {
        return withContext(Dispatchers.IO) {
            val record = query.select()
                .from(SERVERKEY)
                .where(SERVERKEY.KEY_ID.eq(id))
                .fetchOne()

            if (record != null) {
                return@withContext KeyField(
                    record[SERVERKEY.KEY_ID],
                    record[SERVERKEY.PRIVATE_KEY],
                    record[SERVERKEY.TAG]
                )
            } else {
                return@withContext null
            }

        }
    }


    override suspend fun insert(privateKey: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: Int): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun update(id: Int, fieldName: String, newValue: String): Boolean {
        TODO("Not yet implemented")
    }

}