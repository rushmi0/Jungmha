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
import org.jungmha.database.field.DogField
import org.jungmha.database.form.DogForm
import org.jungmha.infra.database.tables.Dogs.DOGS
import org.jungmha.service.DogsService
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

    override suspend fun dogsAll(): List<DogField> {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Retrieve dogs operation started on thread [$currentThreadName]")

                val data = query.select()
                    .from(DOGS)

                val result = data.map { record ->
                    DogField(
                        record[DOGS.DOG_ID],
                        record[DOGS.DOG_IMAGE],
                        record[DOGS.BREED_NAME],
                        record[DOGS.SIZE]
                    )
                }

                if (result.isNotEmpty()) {
                    LOG.info("Retrieve dogs operation successful on thread [$currentThreadName]")
                } else {
                    LOG.warn("No dogs found on thread [$currentThreadName]")
                }

                return@withContext result
            } catch (e: Exception) {
                LOG.error("Error during retrieve dogs operation on thread [$currentThreadName]", e)
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
                        payload.dogImage,
                        payload.breedName,
                        payload.size
                    )
                    .execute()

                if (result > 0) {
                    LOG.info("Insert dog successful on thread [$currentThreadName]")
                } else {
                    LOG.warn("Insert dog did not affect any rows on thread [$currentThreadName]")
                }

                return@withContext result > 0
            } catch (e: Exception) {
                LOG.error("Error during insert dog operation on thread [$currentThreadName]", e)
                return@withContext false
            }
        }
    }

    override suspend fun update(id: Int, fieldName: String, newValue: String): Boolean {
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
                    .set(field, newValue)
                    .where(DOGS.DOG_ID.eq(id)).execute()

                if (affectedRows > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for Dog ID [$id]")
                } else {
                    LOG.error("Update did not affect any rows for field [$fieldName] with new value [$newValue] for Dog ID [$id]")
                }

                return@withContext affectedRows > 0
            } catch (e: Exception) {
                LOG.error("An error occurred during update", e)
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
                LOG.error("Error during delete dog operation on thread [$currentThreadName]", e)
                return@withContext false
            }
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(DogsServiceImpl::class.java)
    }
}
