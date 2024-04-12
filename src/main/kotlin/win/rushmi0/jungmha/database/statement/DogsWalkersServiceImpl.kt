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
import org.jooq.impl.DSL
import win.rushmi0.jungmha.constants.BaseEndpoint.BASE_URL_USER
import win.rushmi0.jungmha.database.field.DogWalkerField
import win.rushmi0.jungmha.database.record.*
import win.rushmi0.jungmha.database.service.DogsWalkersService
import win.rushmi0.jungmha.infra.database.tables.Dogs.DOGS
import win.rushmi0.jungmha.infra.database.tables.Dogwalkbookings.DOGWALKBOOKINGS
import win.rushmi0.jungmha.infra.database.tables.Dogwalkerreviews.DOGWALKERREVIEWS
import win.rushmi0.jungmha.infra.database.tables.Dogwalkers.DOGWALKERS
import win.rushmi0.jungmha.infra.database.tables.Userprofiles.USERPROFILES
import win.rushmi0.jungmha.utils.ShiftTo.ByteArrayToHex
import win.rushmi0.jungmha.utils.ShiftTo.SHA256
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
class DogsWalkersServiceImpl @Inject constructor(
    private val query: DSLContext,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DogsWalkersService {


    override suspend fun getSingleDogWalkersInfo(id: Int): DogWalkerField? {
        return withContext(dispatcher) {

            /**
             * SELECT dk.walker_id,
             *        dk.user_id,
             *        dk.location_name,
             *        dk.id_card_number,
             *        dk.verification,
             *        dk.price_small,
             *        dk.price_medium,
             *        dk.price_big
             * FROM dogwalkers dk
             *          JOIN public.userprofiles u on u.user_id = dk.user_id
             * WHERE dk.user_id = 4;
             * */

            val u = USERPROFILES.`as`("u")
            val dk = DOGWALKERS.`as`("dk")

            val record = query.select(
                dk.WALKER_ID,
                dk.USER_ID,
                dk.LOCATION_NAME,
                dk.ID_CARD_NUMBER,
                dk.VERIFICATION,
                dk.PRICE_SMALL,
                dk.PRICE_MEDIUM,
                dk.PRICE_BIG
            )
                .from(dk)
                .join(u)
                .on(u.USER_ID.eq(dk.USER_ID))
                .where(dk.USER_ID.eq(id))
                .fetchOne()


            LOG.info("\n$record")
            return@withContext record?.let {
                DogWalkerField(
                    walkerID = it[DOGWALKERS.WALKER_ID],
                    userID = it[DOGWALKERS.USER_ID],
                    locationName = it[DOGWALKERS.LOCATION_NAME],
                    idCardNumber = it[DOGWALKERS.ID_CARD_NUMBER],
                    verification = it[DOGWALKERS.VERIFICATION],
                    priceSmall = it[DOGWALKERS.PRICE_SMALL],
                    priceMedium = it[DOGWALKERS.PRICE_MEDIUM],
                    priceBig = it[DOGWALKERS.PRICE_BIG]
                )
            }
        }
    }


    override suspend fun getDogWalkersInfo(accountName: String): DogWalkersInfo? {
        return withContext(dispatcher) {
            try {

                val up = USERPROFILES.`as`("up")
                val dw = DOGWALKERS.`as`("dw")
                val d = DOGS.`as`("d")
                val dwb = DOGWALKBOOKINGS.`as`("dwb")

                /**
                 * SELECT dw.walker_id,
                 *        up.image_profile,
                 *        up.username,
                 *        up.first_name,
                 *        up.last_name,
                 *        up.email,
                 *        up.phone_number,
                 *        up.user_type,
                 *        dw.count_used,
                 *        dw.count_review,
                 *        dw.total_review,
                 *        dw.location_name,
                 *        dw.verification,
                 *        dw.price_small,
                 *        dw.price_medium,
                 *        dw.price_big,
                 *        dw.id_card_number
                 * FROM userprofiles up
                 *      JOIN dogwalkers dw ON up.user_id = dw.user_id
                 * WHERE up.username = :accountName
                 *      AND up.user_type = 'DogWalkers';
                 */
                val mainQuery = query.select(
                    dw.WALKER_ID,
                    up.IMAGE_PROFILE,
                    up.USERNAME,
                    up.FIRST_NAME,
                    up.LAST_NAME,
                    up.EMAIL,
                    up.PHONE_NUMBER,
                    up.USER_TYPE,
                    dw.COUNT_USED,
                    dw.COUNT_REVIEW,
                    dw.TOTAL_REVIEW,
                    dw.LOCATION_NAME,
                    dw.VERIFICATION,
                    dw.PRICE_SMALL,
                    dw.PRICE_MEDIUM,
                    dw.PRICE_BIG,
                    dw.ID_CARD_NUMBER
                )
                    .from(up)
                    .join(dw).on(up.USER_ID.eq(dw.USER_ID))
                    .where(
                        up.USERNAME.eq(DSL.`val`(accountName))
                    )
                    .and(up.USER_TYPE.eq("DogWalkers"))

                val mainQueryResult = mainQuery.fetch { record ->
                    DogWalkersInfo(
                        UserID = record[dw.WALKER_ID],
                        profileImage = if (record[up.IMAGE_PROFILE].toString() != "N/A") "$BASE_URL_USER/${record[up.USERNAME]}/image/${
                            record[up.IMAGE_PROFILE].SHA256().ByteArrayToHex().substring(0, 8)
                        }" else "N/A",
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
                            locationName = record[dw.LOCATION_NAME],
                            idCardNumber = record[dw.ID_CARD_NUMBER],
                            verify = record[dw.VERIFICATION],

                            price = PriceData(
                                small = record[dw.PRICE_SMALL],
                                medium = record[dw.PRICE_MEDIUM],
                                big = record[dw.PRICE_BIG]
                            )
                        ),

                        booking = emptyList()
                    )
                }

                /**
                 * SELECT dwb.booking_id,
                 *        up.username AS user_name,
                 *        d.breed_name,
                 *        d.size,
                 *        dwb.status,
                 *        dwb.booking_date,
                 *        dwb.time_start,
                 *        dwb.time_end,
                 *        dwb.duration,
                 *        dwb.total,
                 *        dwb.timestamp,
                 *        dwb.service_status
                 * FROM dogwalkbookings dwb
                 *      JOIN dogwalkers dw ON dwb.walker_id = dw.walker_id
                 *      JOIN userprofiles up ON dw.user_id = up.user_id
                 *      JOIN dogs d ON dwb.dog_id = d.dog_id
                 * WHERE up.username = :accountName;
                 */
                val subQuery = query
                    .select(
                        dwb.BOOKING_ID,
                        up.USERNAME.`as`("user_name"),
                        d.BREED_NAME,
                        d.SIZE,
                        dwb.STATUS,
                        dwb.BOOKING_DATE,
                        dwb.TIME_START,
                        dwb.TIME_END,
                        dwb.DURATION,
                        dwb.TOTAL,
                        dwb.TIMESTAMP,
                        dwb.SERVICE_STATUS
                    )
                    .from(dwb)
                    .join(dw).on(dwb.WALKER_ID.eq(dw.WALKER_ID))
                    .join(up).on(dw.USER_ID.eq(up.USER_ID))
                    .join(d).on(dwb.DOG_ID.eq(d.DOG_ID))
                    .where(up.USERNAME.eq(DSL.`val`(accountName)))

                val subQueryResult = subQuery.fetch { subRecord ->
                    BookingList(
                        subRecord[dwb.BOOKING_ID],
                        subRecord["user_name"].toString(),
                        subRecord[d.BREED_NAME],
                        subRecord[d.SIZE],
                        subRecord[dwb.STATUS],
                        subRecord[dwb.BOOKING_DATE],
                        subRecord[dwb.TIME_START],
                        subRecord[dwb.TIME_END],
                        subRecord[dwb.DURATION],
                        subRecord[dwb.TOTAL],
                        subRecord[dwb.TIMESTAMP],
                        subRecord[dwb.SERVICE_STATUS]
                    )
                }

                LOG.info("\n${mainQuery.fetchOne()}")
                LOG.info("\n${subQuery.fetch()}")

                return@withContext mainQueryResult.firstOrNull()?.copy(booking = subQueryResult)

            } catch (e: Exception) {
                LOG.error("Error in getDogWalkersInfo: ${e.message}", e)
                return@withContext null
            }
        }
    }



    override suspend fun publicDogWalkersAll(): List<PublicDogWalkerInfo> {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing publicDogWalkersAll")

                val dw = DOGWALKERS
                val up = USERPROFILES

                /**
                 * SELECT dw.walker_id,
                 *        up.username,
                 *        up.image_profile,
                 *        dw.verification,
                 *        dw.total_review,
                 *        dw.location_name,
                 *        dw.price_small,
                 *        dw.price_medium,
                 *        dw.price_big,
                 *        up.email,
                 *        up.phone_number
                 * FROM dogwalkers dw
                 *      JOIN userprofiles up ON dw.user_id = up.user_id
                 * WHERE up.user_type = 'DogWalkers';
                 */
                return@withContext query.select(
                    dw.WALKER_ID,
                    up.USERNAME,
                    up.IMAGE_PROFILE,
                    dw.VERIFICATION,
                    dw.TOTAL_REVIEW,
                    dw.LOCATION_NAME,
                    dw.PRICE_SMALL,
                    dw.PRICE_MEDIUM,
                    dw.PRICE_BIG,
                    up.EMAIL,
                    up.PHONE_NUMBER
                )
                    .from(dw)
                    .join(up).on(dw.USER_ID.eq(up.USER_ID))
                    .where(up.USER_TYPE.eq(DSL.`val`("DogWalkers")))
                    .fetch { record ->

                        PublicDogWalkerInfo(
                            walkerID = record[dw.WALKER_ID],
                            totalReview = record[dw.TOTAL_REVIEW],
                            detail = WalkerDetail(
                                name = record[up.USERNAME],
                                profileImage = if (record[up.IMAGE_PROFILE].toString() != "N/A") "$BASE_URL_USER/${record[up.USERNAME]}/image/${
                                    record[up.IMAGE_PROFILE].SHA256().ByteArrayToHex().substring(0, 8)
                                }" else "N/A",
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

                /**
                 * SELECT dw.walker_id,
                 *        up.username,
                 *        dw.verification,
                 *        dw.count_used,
                 *        dw.count_review,
                 *        dw.total_review,
                 *        dw.location_name,
                 *        dw.price_small,
                 *        dw.price_medium,
                 *        dw.price_big,
                 *        up.email,
                 *        up.phone_number,
                 *        up.user_id,
                 *        up.username,
                 *        up.image_profile
                 * FROM dogwalkers dw
                 *      JOIN userprofiles up ON dw.user_id = up.user_id
                 * WHERE up.user_type = 'DogWalkers';
                 */
                val mainQuery = query.select(
                    dw.WALKER_ID,
                    up.USERNAME,
                    dw.VERIFICATION,
                    dw.COUNT_USED,
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
                    .where(up.USER_TYPE.eq(DSL.`val`("DogWalkers")))

                val result = mainQuery.fetch { record ->

                    /**
                     * SELECT dwr.user_id,
                     *        dwr.rating,
                     *        dwr.review_text
                     * FROM dogwalkerreviews dwr
                     * WHERE dwr.walker_id = :walkerID;
                     */
                    val subQuery = query
                        .select(
                            dwr.USER_ID,
                            dwr.RATING,
                            dwr.REVIEW_TEXT
                        )
                        .from(dwr)
                        .where(dwr.WALKER_ID.eq(record[dw.WALKER_ID]))

                    LOG.info("\n${mainQuery.fetch()}")
                    LOG.info("\n${subQuery.fetch()}")

                    PrivateDogWalkerInfo(
                        walkerID = record[dw.WALKER_ID],
                        countUsed = record[dw.COUNT_USED],
                        countReview = record[dw.COUNT_REVIEW],
                        totalReview = record[dw.TOTAL_REVIEW],

                        detail = WalkerDetail(
                            name = record[up.USERNAME],
                            profileImage = if (record[up.IMAGE_PROFILE].toString() != "N/A") "$BASE_URL_USER/${record[up.USERNAME]}/image/${
                                record[up.IMAGE_PROFILE].SHA256().ByteArrayToHex().substring(0, 8)
                            }" else "N/A",
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
                                profileImage = if (record[up.IMAGE_PROFILE].toString() != "N/A") "$BASE_URL_USER/${record[up.USERNAME]}/image/${
                                    record[up.IMAGE_PROFILE].SHA256().ByteArrayToHex().substring(0, 8)
                                }" else "N/A",
                                rating = subRecord[dwr.RATING] ?: 0,
                                reviewText = subRecord[dwr.REVIEW_TEXT] ?: ""
                            )
                        }.toList()
                    )

                }

                return@withContext result

            } catch (e: Exception) {
                LOG.error("Error retrieving private dog walkers from the database", e)
                return@withContext emptyList()
            }
        }
    }



    override suspend fun insert(id: Int): Boolean {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing insert for Dog Walker")

                /**
                 * INSERT INTO dogwalkers (user_id)
                 * VALUES (:id);
                 */
                val result = query.insertInto(
                    DOGWALKERS,
                    DOGWALKERS.USER_ID,
                )
                    .values(
                        DSL.`val`(id)
                    )
                    .execute()

                val success: Boolean = result > 0

                if (success) {
                    LOG.info("Insert successful for walker with User ID [$id]")
                } else {
                    LOG.warn("No rows inserted for walker with User ID [$id]")
                }

                return@withContext success
            } catch (e: Exception) {
                LOG.error("Error inserting walker with User ID [$id]", e)
                false
            }
        }
    }



    override suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing update for Dog Walker")

                // สร้างคำสั่ง SQL UPDATE โดยให้ fieldName เป็นชื่อคอลัมน์ที่ต้องการอัปเดต
                // และให้ newValue เป็นค่าใหม่ที่ต้องการให้คอลัมน์นั้นมี
                val updateRows = when (fieldName) {
                    "userID" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.USER_ID, DSL.`val`(Integer.valueOf(newValue)))

                    "countUsed" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.COUNT_USED, DSL.`val`(Integer.valueOf(newValue)))

                    "idCardNumber" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.ID_CARD_NUMBER, DSL.`val`(newValue))

                    "location" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.LOCATION_NAME, DSL.`val`(newValue))

                    "small" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_SMALL, DSL.`val`(Integer.valueOf(newValue)))

                    "medium" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_MEDIUM, DSL.`val`(Integer.valueOf(newValue)))

                    "big" -> query.update(DOGWALKERS)
                        .set(DOGWALKERS.PRICE_BIG, DSL.`val`(Integer.valueOf(newValue)))

                    else -> {
                        LOG.error("Field name [$fieldName] not found!!!")
                        return@withContext false
                    }
                }

                // กำหนดเงื่อนไขว่าต้องอัปเดตแถวใดๆ ที่มี walker_id เท่ากับ id ที่ระบุ
                val affectedRows = updateRows.where(DOGWALKERS.WALKER_ID.eq(id))

                // ประมวลผลคำสั่ง SQL UPDATE และนับแถวที่ได้รับผลกระทบ
                val result = affectedRows.execute()

                if (result > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for walker ID [$id]")
                    LOG.info("\n$affectedRows")
                } else {
                    LOG.error("Update did not affect any rows for field [$fieldName] with new value [$newValue] for walker ID [$id]")
                }

                return@withContext result > 0
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

                /**
                 * DELETE FROM dogwalkers
                 * WHERE walker_id = :id;
                 */
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
