package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.jungmha.constants.BaseEndpoint.BASE_URL_DOG
import org.jungmha.database.field.DogField
import org.jungmha.database.form.DogForm
import org.jungmha.infra.database.tables.Dogs.DOGS
import org.jungmha.database.service.DogsService
import org.jungmha.utils.ShiftTo.ByteArrayToHex
import org.jungmha.utils.ShiftTo.SHA256
import org.jungmha.utils.ShiftTo.toFileName
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogsServiceImpl @Inject constructor(
    private val query: DSLContext,
    coroutineDispatcher: CoroutineDispatcher?
) : DogsService {

    private val dispatcher: CoroutineDispatcher = coroutineDispatcher ?: Dispatchers.IO

    override suspend fun findDog(dogID: Int): DogField? {
        return try {
            val currentThreadName = Thread.currentThread().name
            LOG.info("Thread $currentThreadName executing findDog")

            val record: Record? = withContext(dispatcher) {
                query.select()
                    .from(DOGS)
                    .where(DOGS.DOG_ID.eq(DSL.`val`(dogID)))
                    .fetchOne()
            }

            return if (record != null) {
                LOG.info("Dog found with ID [$dogID] on thread [$currentThreadName]")
                DogField(
                    record[DOGS.DOG_ID],
                    record[DOGS.DOG_IMAGE],
                    record[DOGS.BREED_NAME],
                    record[DOGS.SIZE]
                )
            } else {
                null
            }
        } catch (e: DataAccessException) {
            LOG.error(
                "Error accessing data while finding dog with ID [$dogID]",
                e
            )
            null
        } catch (e: Exception) {
            LOG.error(
                "An unexpected error occurred while finding dog with ID [$dogID]",
                e
            )
            null
        }
    }



    override suspend fun dogsAll(): List<DogField> {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Retrieve dogs operation started on thread [$currentThreadName]")

                val data = query.select()
                    .from(DOGS)

                val result = data.fetch { record ->
                    DogField(
                        dogId = record[DOGS.DOG_ID],
                        dogImage = if (record[DOGS.DOG_IMAGE].toString() != "N/A") "$BASE_URL_DOG/${record[DOGS.DOG_ID]}/image/${record[DOGS.DOG_IMAGE].SHA256().ByteArrayToHex().substring(0, 8)}/${record[DOGS.DOG_IMAGE].toFileName()}" else "N/A",
                        breedName = record[DOGS.BREED_NAME],
                        size = record[DOGS.SIZE]
                    )
                }

                if (result.isNotEmpty()) {
                    LOG.info("Retrieve dogs operation successful on thread [$currentThreadName]")
                } else {
                    LOG.warn("No dogs found on thread [$currentThreadName]")
                }

                return@withContext result
            } catch (e: Exception) {
                LOG.error("Error during retrieve dogs operation on thread [$currentThreadName]", e.message)
                return@withContext emptyList()
            }
        }
    }


    override suspend fun insert(payload: DogForm): Boolean {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Insert dog operation started on thread [$currentThreadName]")

                val result = query.insertInto(
                    DOGS,
                    DOGS.DOG_IMAGE,
                    DOGS.BREED_NAME,
                    DOGS.SIZE
                )
                    .values(
                        DSL.`val`(payload.dogImage),
                        DSL.`val`(payload.breedName),
                        DSL.`val`(payload.size)
                    )
                    .execute()

                if (result > 0) {
                    LOG.info("Insert dog successful on thread [$currentThreadName]")
                } else {
                    LOG.warn("Insert dog did not affect any rows on thread [$currentThreadName]")
                }

                return@withContext result > 0
            } catch (e: Exception) {
                LOG.error("Error during insert dog operation on thread [$currentThreadName]", e.message)
                return@withContext false
            }
        }
    }


    override suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(dispatcher) {
            try {
                val field = when (fieldName) {
                    "dogImage" -> DOGS.DOG_IMAGE
                    "breedName" -> DOGS.BREED_NAME
                    "size" -> DOGS.SIZE
                    else -> {
                        LOG.error("Field name [$fieldName] not found!!!")
                        return@withContext false
                    }
                }

                val affectedRows = query.update(DOGS)
                    .set(field, DSL.`val`(newValue))
                    .where(DOGS.DOG_ID.eq(id)).execute()

                if (affectedRows > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for Dog ID [$id]")
                } else {
                    LOG.error("Update did not affect any rows for field [$fieldName] with new value [$newValue] for Dog ID [$id]")
                }

                return@withContext affectedRows > 0
            } catch (e: Exception) {
                LOG.error("An error occurred during update", e.message)
                return@withContext false
            }
        }
    }


    override suspend fun delete(id: Int): Boolean {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Delete dog operation started on thread [$currentThreadName]")

                val result = query.deleteFrom(DOGS)
                    .where(DOGS.DOG_ID.eq(id))
                    .execute()

                if (result > 0) {
                    LOG.info("Delete dog successful on thread [$currentThreadName]")
                } else {
                    LOG.warn("Delete dog did not affect any rows on thread [$currentThreadName]")
                }

                return@withContext result > 0
            } catch (e: Exception) {
                LOG.error("Error during delete dog operation on thread [$currentThreadName]", e.message)
                return@withContext false
            }
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogsServiceImpl::class.java)
    }
}
