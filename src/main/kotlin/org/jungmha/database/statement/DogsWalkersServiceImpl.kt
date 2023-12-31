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
import org.jooq.impl.DSL
import org.jungmha.database.form.DogWalkerForm
import org.jungmha.database.service.DogsWalkersService
import org.jungmha.domain.response.*
import org.jungmha.infra.database.tables.Dogs
import org.jungmha.infra.database.tables.Dogwalkbookings
import org.jungmha.infra.database.tables.Dogwalkerreviews.DOGWALKERREVIEWS
import org.jungmha.infra.database.tables.Dogwalkers.DOGWALKERS
import org.jungmha.infra.database.tables.Userprofiles.USERPROFILES
import org.jungmha.utils.ShiftTo.ByteArrayToHex
import org.jungmha.utils.ShiftTo.SHA256
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class DogsWalkersServiceImpl @Inject constructor(
    private val query: DSLContext,
    taskDispatcher: CoroutineDispatcher?
) : DogsWalkersService {

    private val dispatcher: CoroutineDispatcher = taskDispatcher ?: Dispatchers.IO
    val BASE_URL = "http://localhost:8080/api/v1/user"


    override suspend fun getDogWalkersInfo(accountName: String): DogWalkersInfo? {
        return withContext(dispatcher) {

            val up = USERPROFILES.`as`("up")
            val dw = DOGWALKERS.`as`("dw")
            val d = Dogs.DOGS.`as`("d")
            val up2 = USERPROFILES.`as`("up2")
            val dk = Dogwalkbookings.DOGWALKBOOKINGS.`as`("dk")

            val subQuery = query.select(
                dk.BOOKING_ID,
                up2.USERNAME.`as`("walker_name"),
                d.BREED_NAME,
                d.SIZE,
                dk.BOOKING_DATE,
                dk.TIME_START,
                dk.TIME_END,
                dk.DURATION,
                dk.TOTAL,
                dk.STATUS,
                dk.TIMESTAMP,
                dk.SERVICE_STATUS
            )
                .from(dk)
                .join(up)
                .on(up.USER_ID.eq(dk.USER_ID))
                .join(dw)
                .on(dw.WALKER_ID.eq(dk.WALKER_ID))
                .join(up2)
                .on(up2.USER_ID.eq(dw.USER_ID))
                .join(d)
                .on(d.DOG_ID.eq(dk.DOG_ID))
                .where(up.USERNAME.eq(DSL.`val`(accountName)))
                .fetch { subRecord ->
                    TxBooking(
                        subRecord[dk.BOOKING_ID],
                        subRecord["walker_name"].toString(),
                        subRecord[d.BREED_NAME],
                        subRecord[d.SIZE],
                        subRecord[dk.STATUS],
                        subRecord[dk.BOOKING_DATE],
                        subRecord[dk.TIME_START],
                        subRecord[dk.TIME_END],
                        subRecord[dk.DURATION],
                        subRecord[dk.TOTAL],
                        subRecord[dk.TIMESTAMP],
                        subRecord[dk.SERVICE_STATUS]
                    )
                }

            val mainQuery = query.select(
                up.USER_ID,
                up.USERNAME,
                up.IMAGE_PROFILE,
                up.FIRST_NAME,
                up.LAST_NAME,
                up.EMAIL,
                up.PHONE_NUMBER,
                up.USER_TYPE,
                dw.COUNT_USED,
                dw.COUNT_REVIEW,
                dw.TOTAL_REVIEW,
                dw.LOCATION_NAME,
                dw.ID_CARD_NUMBER,
                dw.VERIFICATION,
                dw.PRICE_SMALL,
                dw.PRICE_MEDIUM,
                dw.PRICE_BIG
            )
                .from(up)
                .join(dk)
                .on(up.USER_ID.eq(dk.USER_ID))
                .join(dw)
                .on(up.USER_ID.eq(dw.USER_ID))
                .where(up.USERNAME.eq(DSL.`val`(accountName)))
                .fetch { record ->

                    DogWalkersInfo(
                        UserID = record[up.USER_ID],
                        profileImage = if (record[up.IMAGE_PROFILE].toString() != "N/A") "$BASE_URL/${record[up.USERNAME]}/image/${record[up.IMAGE_PROFILE].SHA256().ByteArrayToHex().substring(0, 8)}" else "N/A",
                        userName = record[up.USERNAME],
                        firstName = record[up.FIRST_NAME],
                        lastName = record[up.LAST_NAME],
                        email = record[up.EMAIL],
                        phoneNumber = record[up.PHONE_NUMBER],
                        accountType = record[up.USER_TYPE],

                        insights = Insights(
                            countUsed = record[dw.COUNT_USED],
                            countReview = record[dw.COUNT_REVIEW],
                            totalReview = record[dw.TOTAL_REVIEW],
                            location = record[dw.LOCATION_NAME],
                            idCard = record[dw.ID_CARD_NUMBER],
                            verify = record[dw.VERIFICATION],

                            price = PriceData(
                                small = record[dw.PRICE_SMALL],
                                medium = record[dw.PRICE_MEDIUM],
                                big = record[dw.PRICE_BIG]
                            )

                        ),

                        booking = subQuery
                    )
                }

            return@withContext mainQuery.firstOrNull()
        }
    }



    override suspend fun publicDogWalkersAll(): List<PublicDogWalkerInfo> {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing publicDogWalkersAll")

                val dw = DOGWALKERS
                val up = USERPROFILES

                return@withContext query.select(
                    dw.WALKER_ID,
                    up.USERNAME,
                    up.IMAGE_PROFILE,
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
                                profileImage = if (record[up.IMAGE_PROFILE].toString() != "N/A") "$BASE_URL/${record[up.USERNAME]}/image/${record[up.IMAGE_PROFILE].SHA256().ByteArrayToHex().substring(0, 8)}" else "N/A",
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
                LOG.info("Thread ${Thread.currentThread().name} executing privateDogWalkersAll")

                val dw = DOGWALKERS
                val up = USERPROFILES
                val dwr = DOGWALKERREVIEWS

                val mainQuery = query.select(
                    dw.WALKER_ID,
                    up.USERNAME,
                    dw.VERIFICATION,
                    dw.COUNT_REVIEW,
                    dw.TOTAL_REVIEW,
                    dw.LOCATION_NAME,
                    dw.PRICE_SMALL,
                    dw.PRICE_MEDIUM,
                    dw.PRICE_BIG,
                    up.EMAIL,
                    up.PHONE_NUMBER,
                    up.USER_ID,
                    up.USERNAME,
                    up.IMAGE_PROFILE
                )
                    .from(dw)
                    .join(up).on(dw.USER_ID.eq(up.USER_ID))
                    .fetch { record ->

                        val subQuery = query
                            .select(
                                dwr.USER_ID,
                                dwr.RATING,
                                dwr.REVIEW_TEXT
                            )
                            .from(dwr)
                            .where(dwr.WALKER_ID.eq(record[dw.WALKER_ID]))

                        PrivateDogWalkerInfo(
                            walkerID = record[dw.WALKER_ID],
                            countReview = record[dw.COUNT_REVIEW],
                            totalReview = record[dw.TOTAL_REVIEW],

                            detail = WalkerDetail(
                                name = record[up.USERNAME],
                                profileImage = if (record[up.IMAGE_PROFILE].toString() != "N/A") "$BASE_URL/${record[up.USERNAME]}/image" else "N/A",
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

                            review = subQuery.fetch { subRecord ->
                                WalkerReview(
                                    userID = subRecord[dwr.USER_ID],
                                    name = record[up.USERNAME],
                                    profileImage = if (record[up.IMAGE_PROFILE].toString() != "N/A") "$BASE_URL/${record[up.USERNAME]}/image" else "N/A",
                                    rating = subRecord[dwr.RATING] ?: 0,
                                    reviewText = subRecord[dwr.REVIEW_TEXT] ?: ""
                                )
                            }
                        )
                    }

                return@withContext mainQuery

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
                        DSL.`val`(payload.userID),
                        DSL.`val`(payload.locationName),
                        DSL.`val`(payload.idCardNumber),
                        DSL.`val`(payload.priceSmall),
                        DSL.`val`(payload.priceMedium),
                        DSL.`val`(payload.priceBig)
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


    override suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing update for Dog Walker")

                val status = when (fieldName) {
                    "userID" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.USER_ID, DSL.`val`(Integer.valueOf(newValue)))

                    "locationName" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.LOCATION_NAME, DSL.`val`(newValue))

                    "idCardNumber" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.ID_CARD_NUMBER, DSL.`val`(newValue))

                    "priceSmall" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_SMALL, DSL.`val`(Integer.valueOf(newValue)))

                    "priceMedium" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_MEDIUM, DSL.`val`(Integer.valueOf(newValue)))

                    "priceBig" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_BIG, DSL.`val`(Integer.valueOf(newValue)))

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
                    .where(DOGWALKERS.WALKER_ID.eq(DSL.`val`(id)))
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
