package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
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

    override fun getServerKey(id: Int): KeyField? {
        val record = query.select()
            .from(SERVERKEY)
            .where(SERVERKEY.KEY_ID.eq(id))
            .fetchOne()

        return if (record != null) {
            KeyField(
                record[SERVERKEY.KEY_ID],
                record[SERVERKEY.PRIVATE_KEY],
                record[SERVERKEY.TAG]
            )
        } else {
            null
        }
    }


}