package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jungmha.database.field.DogWalkerField
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.infra.database.tables.Dogwalkers.DOGWALKERS
import org.jungmha.infra.database.tables.Userprofiles
import org.jungmha.service.DogsWalkersService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogsWalkersServiceImpl  @Inject constructor(
    @Bean
    private val query: DSLContext
) : DogsWalkersService {


    override suspend fun dogWalkersAll(): List<DogWalkerField> {
        return withContext(Dispatchers.IO) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing dogWalkersAll")

                val data = query.select()
                    .from(DOGWALKERS).fetch()

                LOG.info("Retrieved ${data.size} dog walkers from the database")

                return@withContext data.map { record ->
                    DogWalkerField(
                        record[DOGWALKERS.WALKER_ID],
                        record[DOGWALKERS.USER_ID],
                        record[DOGWALKERS.LOCATION_NAME],
                        record[DOGWALKERS.ID_CARD_NUMBER].toString(),
                        record[DOGWALKERS.VERIFICATION],
                        record[DOGWALKERS.PRICE_SMALL],
                        record[DOGWALKERS.PRICE_MEDIUM],
                        record[DOGWALKERS.PRICE_BIG]
                    )
                }
            } catch (e: Exception) {
                LOG.error("Error retrieving dog walkers from the database", e)
                emptyList()
            }
        }
    }


    override suspend fun insert(payload: DogWalkerForm): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing insert for Dog Walker")

                query.insertInto(
                    DOGWALKERS,
                    DOGWALKERS.USER_ID,
                    DOGWALKERS.LOCATION_NAME,
                    DOGWALKERS.ID_CARD_NUMBER,
                    DOGWALKERS.PRICE_SMALL,
                    DOGWALKERS.PRICE_MEDIUM,
                    DOGWALKERS.PRICE_BIG
                )
                    .values(
                        payload.userID,
                        payload.locationName,
                        payload.idCardNumber,
                        payload.priceSmall,
                        payload.priceMedium,
                        payload.priceBig
                    )
                    .execute()

                LOG.info("Insert successful for walker with User ID [${payload.userID}]")

                true
            } catch (e: Exception) {
                LOG.error("Error inserting walker with User ID [${payload.userID}]", e)
                false
            }
        }
    }


    override suspend fun update(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                LOG.info("Thread ${Thread.currentThread().name} executing update for Dog Walker")

                val status = when (fieldName) {

                    "userID" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.USER_ID, Integer.valueOf(newValue))
                        .where(DOGWALKERS.WALKER_ID.eq(id))
                        .execute()

                    "locationName" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.LOCATION_NAME, newValue)
                        .where(DOGWALKERS.WALKER_ID.eq(id))
                        .execute()

                    "idCardNumber" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.ID_CARD_NUMBER, newValue)
                        .where(DOGWALKERS.WALKER_ID.eq(id))
                        .execute()

                    "priceSmall" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_SMALL, Integer.valueOf(newValue))
                        .where(DOGWALKERS.WALKER_ID.eq(id))
                        .execute()

                    "priceMedium" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_MEDIUM, Integer.valueOf(newValue))
                        .where(DOGWALKERS.WALKER_ID.eq(id))
                        .execute()

                    "priceBig" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_BIG, Integer.valueOf(newValue))
                        .where(DOGWALKERS.WALKER_ID.eq(id))
                        .execute()

                    else -> {
                        LOG.error("Field name [$fieldName] not found!!!")
                        return@withContext false
                    }
                }

                if (status > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for walker ID [$id]")
                } else {
                    LOG.warn("Update did not affect any rows for field [$fieldName] with new value [$newValue] for walker ID [$id]")
                }

                status > 0
            } catch (e: Exception) {
                LOG.error("Error updating field [$fieldName] for walker ID [$id]", e)
                false
            }
        }
    }


    override suspend fun delete(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing delete")

                val deletedRows = query.deleteFrom(DOGWALKERS)
                    .where(DOGWALKERS.WALKER_ID.eq(id))
                    .execute()

                if (deletedRows > 0) {
                    LOG.info("Delete successful for DogWalker with ID [$id]")
                } else {
                    LOG.warn("Delete did not affect any rows for DogWalker with ID [$id]")
                }

                deletedRows > 0
            } catch (e: Exception) {
                LOG.error("Error deleting DogWalker with ID [$id]", e)
                false
            }
        }
    }


    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogsWalkersServiceImpl::class.java)
    }

}
