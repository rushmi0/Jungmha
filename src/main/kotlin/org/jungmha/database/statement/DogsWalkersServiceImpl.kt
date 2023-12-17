package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jungmha.database.field.DogWalkerField
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.infra.database.tables.Dogwalkers.DOGWALKERS
import org.jungmha.service.DogsWalkersService

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogsWalkersServiceImpl(
    private val query: DSLContext
) : DogsWalkersService {

    override suspend fun dogWalkersAll(): List<DogWalkerField> {

        val data = withContext(Dispatchers.IO) {
            query.select()
                .from(DOGWALKERS).fetch()

        };

        return data.map { record ->
            DogWalkerField(
                record[DOGWALKERS.WALKER_ID],
                record[DOGWALKERS.USER_ID],
                record[DOGWALKERS.LOCATION_NAME],
                record[DOGWALKERS.ID_CARD_NUMBER],
                record[DOGWALKERS.VERIFICATION],
                record[DOGWALKERS.PRICE_SMALL],
                record[DOGWALKERS.PRICE_MEDIUM],
                record[DOGWALKERS.PRICE_BIG]
            )
        }

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