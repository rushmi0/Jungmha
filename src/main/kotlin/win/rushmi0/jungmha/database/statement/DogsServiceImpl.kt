package win.rushmi0.jungmha.database.statement


import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
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
import win.rushmi0.jungmha.constants.BaseEndpoint.BASE_URL_DOG
import win.rushmi0.jungmha.database.field.DogField
import win.rushmi0.jungmha.database.form.DogForm
import win.rushmi0.jungmha.infra.database.tables.Dogs.DOGS
import win.rushmi0.jungmha.database.service.DogsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import win.rushmi0.jungmha.utils.ShiftTo.ByteArrayToHex
import win.rushmi0.jungmha.utils.ShiftTo.SHA256
import win.rushmi0.jungmha.utils.ShiftTo.toFileName

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
class DogsServiceImpl @Inject constructor(
    private val query: DSLContext,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DogsService {



    override suspend fun findDog(dogID: Int): DogField? {
        return try {

            /**
             * SELECT *
             * FROM dogs
             * WHERE dogs.dog_id = :dogID;
             */
            val record: Record? = withContext(dispatcher) {
                query.select()
                    .from(DOGS)
                    .where(DOGS.DOG_ID.eq(DSL.`val`(dogID)))
                    .fetchOne()
            }

            return if (record != null) {
                LOG.info("Dog found with ID [$dogID]")
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
            try {

                /**
                 * SELECT *
                 * FROM dogs;
                 */

                val data = query.select()
                    .from(DOGS)

                val result = data.fetch { record ->
                    DogField(
                        dogId = record[DOGS.DOG_ID],
                        dogImage = if (record[DOGS.DOG_IMAGE].toString() != "N/A") "$BASE_URL_DOG/${record[DOGS.DOG_ID]}/image/${
                            record[DOGS.DOG_IMAGE].SHA256().ByteArrayToHex().substring(0, 8)
                        }/${record[DOGS.DOG_IMAGE].toFileName()}" else "N/A",
                        breedName = record[DOGS.BREED_NAME],
                        size = record[DOGS.SIZE]
                    )
                }

                if (result.isNotEmpty()) {
                    LOG.info("Retrieve dogs operation successful")
                } else {
                    LOG.warn("No dogs found")
                }

                return@withContext result
            } catch (e: Exception) {
                LOG.error("Error during retrieve dogs operation", e.message)
                return@withContext emptyList()
            }
        }
    }



    override suspend fun insert(payload: DogForm): Boolean {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Insert dog operation started on thread [$currentThreadName]")

                /**
                 * INSERT INTO dogs (dog_image, breed_name, size)
                 * VALUES (dogImage, breedName, size);
                 */

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

                LOG.info("Update operation started for field [$fieldName] with new value [$newValue] for Dog ID [$id]")

                /**
                 * UPDATE dogs
                 * SET $fieldName = <newValue>
                 * WHERE dog_id = <id>;
                 */

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
                LOG.error("An error occurred during update: ${e.message}")
                return@withContext false
            }
        }
    }



    override suspend fun delete(id: Int): Boolean {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Delete dog operation started on thread [$currentThreadName]")

                /**
                 * DELETE FROM dogs
                 * WHERE dog_id = :id;
                 */

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
