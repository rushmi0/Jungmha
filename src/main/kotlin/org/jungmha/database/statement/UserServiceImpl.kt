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
import org.jooq.Result
import org.jooq.TableField
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.IdentityForm
import org.jungmha.database.form.UserProfileForm
import org.jungmha.database.service.UserService
import org.jungmha.database.statement.ValidateData.validateAndLogSize
import org.jungmha.domain.response.NormalInfo
import org.jungmha.domain.response.TxBooking
import org.jungmha.infra.database.tables.Dogs.DOGS
import org.jungmha.infra.database.tables.Dogwalkbookings.DOGWALKBOOKINGS
import org.jungmha.infra.database.tables.Dogwalkers.DOGWALKERS
import org.jungmha.infra.database.tables.Userprofiles.USERPROFILES
import org.jungmha.infra.database.tables.records.UserprofilesRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class UserServiceImpl @Inject constructor(
    private val query: DSLContext,
    taskDispatcher: CoroutineDispatcher?
) : UserService {

    private val dispatcher: CoroutineDispatcher = taskDispatcher ?: Dispatchers.IO

    override suspend fun getUserInfo(accountName: String): NormalInfo? {
        return withContext(dispatcher) {

            val up = USERPROFILES.`as`("up")
            val dw = DOGWALKERS.`as`("dw")
            val d = DOGS.`as`("d")
            val up2 = USERPROFILES.`as`("up2")
            val dk = DOGWALKBOOKINGS.`as`("dk")

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
                up.USER_TYPE
            )
                .from(up)
                .join(dk)
                .on(up.USER_ID.eq(dk.USER_ID))
                .where(up.USERNAME.eq(DSL.`val`(accountName)))
                .fetch { record ->
                    NormalInfo(
                        UserID = record[up.USER_ID],
                        profileImage = record[up.IMAGE_PROFILE],
                        userName = record[up.USERNAME],
                        firstName = record[up.FIRST_NAME],
                        lastName = record[up.LAST_NAME],
                        email = record[up.EMAIL],
                        phoneNumber = record[up.PHONE_NUMBER],
                        accountType = record[up.USER_TYPE],
                        booking = subQuery
                    )
                }

            return@withContext mainQuery.firstOrNull()
        }
    }




    override suspend fun findUser(accountName: String): UserProfileField? {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name

            try {
                LOG.info("Thread $currentThreadName executing findUser")

                val record: Record? = query.select()
                    .from(USERPROFILES)
                    .where(USERPROFILES.USERNAME.eq(DSL.`val`(accountName).coerce(String::class.java)))
                    .fetchOne()

                return@withContext if (record != null) {
                    LOG.info("User found with account name [$accountName] on thread [$currentThreadName]")
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
                } else {
                    LOG.info("User not found with account name [$accountName] on thread [$currentThreadName]")
                    null
                }
            } catch (e: DataAccessException) {
                LOG.error(
                    "Error accessing data while finding user with account name [$accountName] on thread [$currentThreadName]",
                    e
                )
                null
            } catch (e: Exception) {
                LOG.error(
                    "An unexpected error occurred while finding user with account name [$accountName] on thread [$currentThreadName]",
                    e
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

                LOG.info("Thread ${Thread.currentThread().name} executing userAll")
                LOG.info("Retrieved ${result.size} user profiles from the database")

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
                LOG.error("Error retrieving user profiles from the database", e)
                emptyList()
            }
        }
    }


    override suspend fun insert(payload: IdentityForm): Boolean {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing insert")

                val result = query.insertInto(
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
                    .execute()

                val success = result > 0 // ตรวจสอบว่ามีการเพิ่มแถวในฐานข้อมูลหรือไม่

                if (success) {
                    LOG.info("Insert successful for User : ${payload.userName}")
                } else {
                    LOG.warn("No rows inserted for User : ${payload.userName}")
                }

                return@withContext success
            } catch (e: Exception) {
                LOG.error("Error inserting User Profile", e)
                false
            }
        }
    }


    override suspend fun updateMultiField(userName: String, payload: UserProfileForm): Boolean {
        return withContext(dispatcher) {
            val currentThreadName = Thread.currentThread().name
            try {
                LOG.info("Update operation started for user [$userName] on thread [$currentThreadName]")

                val updateRows = query.update(USERPROFILES)
                    .set(USERPROFILES.FIRST_NAME, DSL.value(payload.firstName).coerce(String::class.java))
                    .set(USERPROFILES.LAST_NAME, DSL.value(payload.lastName).coerce(String::class.java))
                    .set(USERPROFILES.EMAIL, DSL.value(payload.email).coerce(String::class.java))
                    .set(USERPROFILES.PHONE_NUMBER, DSL.value(payload.phoneNumber).coerce(String::class.java))
                    .set(USERPROFILES.USER_TYPE, DSL.value(payload.userType).coerce(String::class.java))
                    .where(USERPROFILES.USERNAME.eq(DSL.value(userName).coerce(String::class.java)))
                    .execute()

                if (updateRows > 0) {
                    LOG.info("Update successful for user [$userName] on thread [$currentThreadName]")
                } else {
                    LOG.warn("Update did not affect any rows for user [$userName] on thread [$currentThreadName]")
                }

                return@withContext updateRows > 0
            } catch (e: Exception) {
                LOG.error("An error occurred during update for user [$userName] on thread [$currentThreadName]", e)
                return@withContext false
            }
        }
    }


    override suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing update")

                val field = getField(fieldName)
                if (field == null) {
                    LOG.error("Field name [$fieldName] not found!!!")
                    return@withContext false
                }

                val updateRows = query.update(USERPROFILES)
                    .set(field, DSL.value(newValue).coerce(String::class.java))
                    .where(USERPROFILES.USER_ID.eq(id))
                    .execute()

                if (updateRows > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for user ID [$id]")
                } else {
                    LOG.warn("Update did not affect any rows for field [$fieldName] with new value [$newValue] for user ID [$id]")
                }

                return@withContext updateRows > 0
            } catch (e: Exception) {
                LOG.error("Error updating field [$fieldName] for user ID [$id]", e)
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
            "userType" -> USERPROFILES.USER_TYPE
            else -> throw IllegalArgumentException("Field name [$fieldName] not found!!!")

        }
    }


    override suspend fun delete(id: Int): Boolean {
        return withContext(dispatcher) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing delete")

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
                LOG.error("Error deleting user with ID [$id]", e)
                return@withContext false
            }
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }

}