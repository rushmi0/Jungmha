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
import org.jooq.Result
import org.jooq.TableField
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import win.rushmi0.jungmha.constants.BaseEndpoint.BASE_URL_USER
import win.rushmi0.jungmha.database.field.UserProfileField
import win.rushmi0.jungmha.database.form.IdentityForm
import win.rushmi0.jungmha.database.form.UserProfileForm
import win.rushmi0.jungmha.database.service.UserService
import win.rushmi0.jungmha.database.record.NormalInfo
import win.rushmi0.jungmha.database.record.BookingList
import win.rushmi0.jungmha.infra.database.tables.Dogs.DOGS
import win.rushmi0.jungmha.infra.database.tables.Dogwalkbookings.DOGWALKBOOKINGS
import win.rushmi0.jungmha.infra.database.tables.Dogwalkers.DOGWALKERS
import win.rushmi0.jungmha.infra.database.tables.Userprofiles.USERPROFILES
import win.rushmi0.jungmha.infra.database.tables.records.UserprofilesRecord
import win.rushmi0.jungmha.utils.ShiftTo.ByteArrayToHex
import win.rushmi0.jungmha.utils.ShiftTo.SHA256
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
class UserServiceImpl @Inject constructor(
    private val query: DSLContext,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserService {


    override suspend fun getUserInfo(accountName: String): NormalInfo? {
        return withContext(dispatcher) {

            val up = USERPROFILES.`as`("up")
            val dw = DOGWALKERS.`as`("dw")
            val d = DOGS.`as`("d")
            val up2 = USERPROFILES.`as`("up2")
            val dk = DOGWALKBOOKINGS.`as`("dk")

            /**
             * SELECT dk.BOOKING_ID, up2.USERNAME AS walker_name, d.BREED_NAME, d.SIZE, dk.BOOKING_DATE,
             * dk.TIME_START, dk.TIME_END, dk.DURATION, dk.TOTAL, dk.STATUS, dk.TIMESTAMP, dk.SERVICE_STATUS
             * FROM dogwalkbookings dk
             * JOIN userprofiles up ON up.USER_ID = dk.USER_ID
             * JOIN dogwalkers dw ON dw.WALKER_ID = dk.WALKER_ID
             * JOIN userprofiles up2 ON up2.USER_ID = dw.USER_ID
             * JOIN dogs d ON d.DOG_ID = dk.DOG_ID
             * WHERE up.USERNAME = :accountName
             */
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

            /**
             * SELECT up.USER_ID, up.USERNAME, up.IMAGE_PROFILE, up.FIRST_NAME, up.LAST_NAME, up.EMAIL,
             * up.PHONE_NUMBER, up.USER_TYPE
             * FROM userprofiles up
             * WHERE up.USERNAME = :accountName
             */
            val mainQuery = query.select(
                up.USER_ID,
                up.USERNAME,
                up.IMAGE_PROFILE,
                up.FIRST_NAME,
                up.LAST_NAME,
                up.EMAIL,
                up.PHONE_NUMBER,
                up.USER_TYPE
            )
                .from(up)
                .where(up.USERNAME.eq(DSL.`val`(accountName)))

            val result = mainQuery.fetchOne { record ->

                val bookings = subQuery.fetch { subRecord ->
                    BookingList(
                        bookingID = subRecord[dk.BOOKING_ID],
                        userName = subRecord["walker_name"].toString(),
                        breedName = subRecord[d.BREED_NAME],
                        size = subRecord[d.SIZE],
                        status = subRecord[dk.STATUS],
                        bookingDate = subRecord[dk.BOOKING_DATE],
                        timeStart = subRecord[dk.TIME_START],
                        timeEnd = subRecord[dk.TIME_END],
                        duration = subRecord[dk.DURATION],
                        total = subRecord[dk.TOTAL],
                        timeStamp = subRecord[dk.TIMESTAMP],
                        serviceStatus = subRecord[dk.SERVICE_STATUS]
                    )
                }.toList().takeIf { it.isNotEmpty() }

                NormalInfo(
                    userID = record[up.USER_ID],
                    profileImage = if (record[up.IMAGE_PROFILE].toString() != "N/A") "$BASE_URL_USER/${record[up.USERNAME]}/image/${
                        record[up.IMAGE_PROFILE].SHA256().ByteArrayToHex().substring(0, 8)
                    }" else "N/A",
                    userName = record[up.USERNAME],
                    firstName = record[up.FIRST_NAME],
                    lastName = record[up.LAST_NAME],
                    email = record[up.EMAIL],
                    phoneNumber = record[up.PHONE_NUMBER],
                    accountType = record[up.USER_TYPE],
                    booking = bookings
                )
            }

            LOG.info("\n${mainQuery.fetchOne()}")
            LOG.info("\n${subQuery.fetch()}")

            if (result == null) {
                LOG.warn("User not found for Account Name: $accountName")
                return@withContext null
            }

            if (result.booking == null) {
                result.copy(booking = emptyList())
            } else {
                result
            }
        }
    }



    override suspend fun findUser(accountName: String): UserProfileField? {
        return withContext(dispatcher) {
            val stackTrace = Thread.currentThread().stackTrace

            try {

                /**
                 * SELECT * FROM userprofiles
                 * WHERE userprofiles.USERNAME = :accountName
                 */
                val result: Record? = query.select()
                    .from(USERPROFILES)
                    .where(USERPROFILES.USERNAME.eq(DSL.`val`(accountName).coerce(String::class.java)))
                    .fetchOne()

                LOG.info("\n$result")

                return@withContext if (result != null) {
                    LOG.info("User found with account name [$accountName]")

                    UserProfileField(
                        result[USERPROFILES.USER_ID],
                        result[USERPROFILES.AUTHEN_KEY],
                        result[USERPROFILES.SHARE_KEY],
                        result[USERPROFILES.IMAGE_PROFILE],
                        result[USERPROFILES.USERNAME],
                        result[USERPROFILES.FIRST_NAME],
                        result[USERPROFILES.LAST_NAME],
                        result[USERPROFILES.EMAIL],
                        result[USERPROFILES.PHONE_NUMBER],
                        result[USERPROFILES.CREATED_AT],
                        result[USERPROFILES.USER_TYPE]
                    )
                } else {
                    LOG.info("User not found with account name [$accountName] from [$stackTrace]")
                    null
                }
            } catch (e: DataAccessException) {
                LOG.error(
                    "Error accessing data while finding user with account name [$accountName] from [$stackTrace]",
                    e.message
                )
                null
            } catch (e: Exception) {
                LOG.error(
                    "An unexpected error occurred while finding user with account name [$accountName] from [$stackTrace]",
                    e.message
                )
                null
            }
        }
    }



    override suspend fun userAll(): List<UserProfileField> {
        return withContext(dispatcher) {
            try {
                val result: Result<Record> = query.select()
                    .from(USERPROFILES)
                    .fetch()

                LOG.info("Current Class: ${Thread.currentThread().stackTrace[1].className}")
                LOG.info("Executing Method: ${Thread.currentThread().stackTrace[1].methodName}")
                LOG.info("Thread ${Thread.currentThread().name} [ID: ${Thread.currentThread().id}] in state ${Thread.currentThread().state}. Is Alive: ${Thread.currentThread().isAlive}")

                return@withContext result.map { record ->
                    UserProfileField(
                        record[USERPROFILES.USER_ID],
                        record[USERPROFILES.AUTHEN_KEY],
                        record[USERPROFILES.SHARE_KEY],
                        record[USERPROFILES.IMAGE_PROFILE],
                        record[USERPROFILES.USERNAME],
                        record[USERPROFILES.FIRST_NAME],
                        record[USERPROFILES.LAST_NAME],
                        record[USERPROFILES.EMAIL],
                        record[USERPROFILES.PHONE_NUMBER],
                        record[USERPROFILES.CREATED_AT],
                        record[USERPROFILES.USER_TYPE]
                    )
                }
            } catch (e: Exception) {
                LOG.error("Error retrieving user profiles from the database ${e.message}")
                emptyList()
            }
        }
    }


    override suspend fun insert(payload: IdentityForm): Boolean {
        return withContext(dispatcher) {
            try {

                /**
                 * INSERT INTO userprofiles
                 * (userprofiles.USERNAME, userprofiles.AUTHEN_KEY, userprofiles.SHARE_KEY)
                 * VALUES (:payload.userName, :payload.authenKey, :payload.shareKey)
                 */
                val record = query.insertInto(
                    USERPROFILES,
                    USERPROFILES.USERNAME,
                    USERPROFILES.AUTHEN_KEY,
                    USERPROFILES.SHARE_KEY
                )
                    .values(
                        DSL.value(payload.userName).coerce(String::class.java),
                        DSL.value(payload.authenKey).coerce(String::class.java),
                        DSL.value(payload.shareKey).coerce(String::class.java)
                    )

                val result = record.execute()
                val success = result > 0

                if (success) {
                    LOG.info("Insert successful for User : ${payload.userName}")
                } else {
                    LOG.warn("No rows inserted for User : ${payload.userName}")
                }
                LOG.info("\n$record")

                return@withContext success
            } catch (e: Exception) {
                LOG.error("Error inserting User Profile ${e.message}")
                false
            }
        }
    }



    override suspend fun updateMultiField(userName: String, payload: UserProfileForm): Boolean {
        return withContext(dispatcher) {
            val stackTrace = Thread.currentThread().stackTrace
            try {

                /**
                 * UPDATE userprofiles
                 * SET
                 * userprofiles.FIRST_NAME = :payload.firstName,
                 * userprofiles.LAST_NAME = :payload.lastName,
                 * userprofiles.EMAIL = :payload.email,
                 * userprofiles.PHONE_NUMBER = :payload.phoneNumber,
                 * userprofiles.USER_TYPE = :payload.userType
                 * WHERE userprofiles.USERNAME = :userName
                 */
                val updateRows = query.update(USERPROFILES)
                    .set(USERPROFILES.FIRST_NAME, payload.firstName)
                    .set(USERPROFILES.LAST_NAME, payload.lastName)
                    .set(USERPROFILES.EMAIL, payload.email)
                    .set(USERPROFILES.PHONE_NUMBER, payload.phoneNumber)
                    .set(USERPROFILES.USER_TYPE, payload.userType)
                    .where(USERPROFILES.USERNAME.eq(userName))

                val result = updateRows.execute()

                if (result > 0) {
                    LOG.info("Update successful for user [$userName] ")
                } else {
                    LOG.warn("Update did not affect any rows for user [$userName] from [$stackTrace]")
                }
                LOG.info("\n$updateRows")

                return@withContext result > 0
            } catch (e: Exception) {
                LOG.error(
                    "An error occurred during update for user [$userName] from [$stackTrace]",
                    e.message
                )
                return@withContext false
            }
        }
    }


    // *************************************************************************************************** \\

    override suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(dispatcher) {
            try {

                val field = getField(fieldName)
                if (field == null) {
                    LOG.error("Field name [$fieldName] not found!!!")
                    return@withContext false
                }

                /**
                 * UPDATE userprofiles
                 * SET <field> = :newValue
                 * WHERE userprofiles.USER_ID = :id
                 */
                val updateRows = query.update(USERPROFILES)
                    .set(field, DSL.value(newValue).coerce(String::class.java))
                    .where(USERPROFILES.USER_ID.eq(id))

                val result = updateRows.execute()

                if (result > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for user ID [$id]")
                } else {
                    LOG.warn("Update did not affect any rows for field [$fieldName] with new value [$newValue] for user ID [$id]")
                }
                LOG.info("\n$updateRows")

                return@withContext result > 0
            } catch (e: Exception) {
                LOG.error("Error updating field [$fieldName] for user ID [$id]", e.message)
                return@withContext false
            }
        }
    }

    // สร้างเมทอดเพิ่มเติมเพื่อ map ชื่อฟิลด์กับคอลัมน์ใน JOOQ
    private fun getField(fieldName: String): TableField<UserprofilesRecord, String>? {
        return when (fieldName) {
            "imageProfile" -> USERPROFILES.IMAGE_PROFILE
            "email" -> USERPROFILES.EMAIL
            "phoneNumber" -> USERPROFILES.PHONE_NUMBER
            else -> throw IllegalArgumentException("Field name [$fieldName] not found!!!")
        }
    }


    // *************************************************************************************************** \\


    override suspend fun delete(id: Int): Boolean {
        return withContext(dispatcher) {
            try {

                /**
                 * DELETE FROM userprofiles
                 * WHERE userprofiles.USER_ID = :id
                 */
                val deletedRows = query.deleteFrom(USERPROFILES)
                    .where(USERPROFILES.USER_ID.eq(DSL.`val`(id)))
                    .execute()

                if (deletedRows > 0) {
                    LOG.info("Delete successful for user with ID [$id]")
                } else {
                    LOG.warn("Delete did not affect any rows for user with ID [$id]")
                }

                return@withContext deletedRows > 0
            } catch (e: Exception) {
                LOG.error("Error deleting user with ID [$id] ${e.message}")
                return@withContext false
            }
        }
    }


    companion object {
        val LOG: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }

}