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
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.domain.response.*
import org.jungmha.infra.database.tables.Dogwalkerreviews.DOGWALKERREVIEWS
import org.jungmha.infra.database.tables.Dogwalkers.DOGWALKERS
import org.jungmha.infra.database.tables.Userprofiles.USERPROFILES
import org.jungmha.service.DogsWalkersService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogsWalkersServiceImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val query: DSLContext
) : DogsWalkersService {


    override suspend fun publicDogWalkersAll(): List<PublicDogWalkerInfo> {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing dogWalkersAll")

                val dw = DOGWALKERS
                val up = USERPROFILES

                return@withContext query.select(
                    dw.WALKER_ID,
                    up.USERNAME,
                    dw.VERIFICATION,
                    dw.LOCATION_NAME,
                    dw.PRICE_SMALL,
                    dw.PRICE_MEDIUM,
                    dw.PRICE_BIG,
                    up.EMAIL,
                    up.PHONE_NUMBER
                )
                    .from(dw)
                    .join(up).on(dw.USER_ID.eq(up.USER_ID))
                    .fetch { record ->
                        PublicDogWalkerInfo(
                            walkerID = record[dw.WALKER_ID],
                            detail = WalkerDetail(
                                name = record[up.USERNAME],
                                verify = record[dw.VERIFICATION],
                                location = record[dw.LOCATION_NAME],
                                price = PriceData(
                                    small = record[dw.PRICE_SMALL],
                                    medium = record[dw.PRICE_MEDIUM],
                                    big = record[dw.PRICE_BIG]
                                )
                            )
                        )
                    }

            } catch (e: Exception) {
                LOG.error("Error retrieving dog walkers from the database", e)
                emptyList()
            }
        }
    }



    override suspend fun privateDogWalkersAll(): List<PrivateDogWalkerInfo> {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing publicDogWalkersAll")

                val dw = DOGWALKERS
                val up = USERPROFILES
                val dwr = DOGWALKERREVIEWS

                return@withContext query.select(
                    dw.WALKER_ID,
                    up.USERNAME,
                    dw.VERIFICATION,
                    dw.LOCATION_NAME,
                    dw.PRICE_SMALL,
                    dw.PRICE_MEDIUM,
                    dw.PRICE_BIG,
                    up.EMAIL,
                    up.PHONE_NUMBER,
                    up.USER_ID,
                    up.USERNAME,
                    up.IMAGE_PROFILE,
                    dwr.RATING,
                    dwr.REVIEW_TEXT
                )
                    .from(dw)
                    .join(up).on(dw.USER_ID.eq(up.USER_ID))
                    .leftJoin(dwr).on(dw.WALKER_ID.eq(dwr.WALKER_ID))
                    .fetch { record ->
                        PrivateDogWalkerInfo(
                            walkerID = record[dw.WALKER_ID],
                            detail = WalkerDetail(
                                name = record[up.USERNAME],
                                verify = record[dw.VERIFICATION],
                                location = record[dw.LOCATION_NAME],
                                price = PriceData(
                                    small = record[dw.PRICE_SMALL],
                                    medium = record[dw.PRICE_MEDIUM],
                                    big = record[dw.PRICE_BIG]
                                )
                            ),
                            contact = WalkerContact(
                                email = record[up.EMAIL],
                                phoneNumber = record[up.PHONE_NUMBER]
                            ),
                            review = WalkerReview(
                                userID = record[up.USER_ID] ?: 0,
                                name = record[up.USERNAME] ?: "",
                                profileImage = record[up.IMAGE_PROFILE] ?: "",
                                rating = record[dwr.RATING] ?: 0,
                                reviewText = record[dwr.REVIEW_TEXT] ?: ""
                            )
                        )
                    }

            } catch (e: Exception) {
                LOG.error("Error retrieving public dog walkers from the database", e)
                emptyList()
            }
        }
    }


    override suspend fun insert(payload: DogWalkerForm): Boolean {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing insert for Dog Walker")

                val result = query.insertInto(
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

                val success: Boolean = result > 0 // ตรวจสอบว่ามีการเพิ่มข้อมูลลงในฐานข้อมูลหรือไม่

                if (success) {
                    LOG.info("Insert successful for walker with User ID [${payload.userID}]")
                } else {
                    LOG.warn("No rows inserted for walker with User ID [${payload.userID}]")
                }

                return@withContext success
            } catch (e: Exception) {
                LOG.error("Error inserting walker with User ID [${payload.userID}]", e)
                false
            }
        }
    }


    override suspend fun update(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing update for Dog Walker")

                val status = when (fieldName) {
                    "userID" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.USER_ID, Integer.valueOf(newValue))
                    "locationName" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.LOCATION_NAME, newValue)
                    "idCardNumber" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.ID_CARD_NUMBER, newValue)
                    "priceSmall" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_SMALL, Integer.valueOf(newValue))
                    "priceMedium" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_MEDIUM, Integer.valueOf(newValue))
                    "priceBig" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_BIG, Integer.valueOf(newValue))
                    else -> {
                        LOG.error("Field name [$fieldName] not found!!!")
                        return@withContext false
                    }
                }

                val affectedRows = status.where(DOGWALKERS.WALKER_ID.eq(id)).execute()

                if (affectedRows > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for walker ID [$id]")
                } else {
                    LOG.error("Update did not affect any rows for field [$fieldName] with new value [$newValue] for walker ID [$id]")
                }

                return@withContext affectedRows > 0
            } catch (e: Exception) {
                LOG.error("Error updating field [$fieldName] for walker ID [$id]", e)
                return@withContext false
            }
        }
    }



    override suspend fun delete(id: Int): Boolean {
        return withContext(dispatcher) {
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

                return@withContext deletedRows > 0
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
