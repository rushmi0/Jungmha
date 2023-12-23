package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.jooq.TableField
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.IdentityForm
import org.jungmha.database.form.UserProfileForm
import org.jungmha.infra.database.tables.Userprofiles.USERPROFILES
import org.jungmha.infra.database.tables.records.UserprofilesRecord
import org.jungmha.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory


@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class UserServiceImpl @Inject constructor(
    private val query: DSLContext
) : UserService {


    override suspend fun findUser(accountName: String): UserProfileField? {
        return withContext(Dispatchers.IO) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing findUser")

                val record: Record? = query.select()
                    .from(USERPROFILES)
                    .where(USERPROFILES.USERNAME.eq(accountName))
                    .fetchOne()

                if (record != null) {
                    LOG.info("User found with account name [$accountName]")
                    return@withContext UserProfileField(
                        record[USERPROFILES.USER_ID],
                        record[USERPROFILES.AUTHEN_KEY],
                        record[USERPROFILES.SHARE_KEY],
                        record[USERPROFILES.IMAGE_PROFILE],
                        record[USERPROFILES.USERNAME],
                        record[USERPROFILES.FIRST_NAME],
                        record[USERPROFILES.LAST_NAME],
                        record[USERPROFILES.EMAIL],
                        record[USERPROFILES.PHONE_NUMBER],
                        record[USERPROFILES.CREATED_AT].toString(),
                        record[USERPROFILES.USER_TYPE]
                    )
                } else {
                    LOG.info("User not found with account name [$accountName]")
                    return@withContext null
                }
            } catch (e: Exception) {
                LOG.error("Error finding user with account name [$accountName]", e)
                null
            }
        }
    }


    override suspend fun userAll(): List<UserProfileField> {
        return withContext(Dispatchers.IO) {
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
                        record[USERPROFILES.CREATED_AT].toString(),
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
        return withContext(Dispatchers.IO) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing insert")

                val result = query.insertInto(
                    USERPROFILES,
                    USERPROFILES.USERNAME,
                    USERPROFILES.AUTHEN_KEY,
                    USERPROFILES.SHARE_KEY
                )
                    .values(
                        payload.userName,
                        payload.authenKey,
                        payload.shareKey
                    )
                    .execute()

                val success = result > 0 // ตรวจสอบว่ามีการเพิ่มแถวในฐานข้อมูลหรือไม่

                if (success) {
                    LOG.info("Insert successful for User Profile")
                } else {
                    LOG.warn("No rows inserted for User Profile")
                }

                return@withContext success
            } catch (e: Exception) {
                LOG.error("Error inserting User Profile", e)
                false
            }
        }
    }


    override suspend fun updateMultiField(userName: String, payload: UserProfileForm): Boolean {
        return withContext(Dispatchers.IO) {
            val currentThreadName = Thread.currentThread().name
            try {
                LOG.info("Update operation started for user [$userName] on thread [$currentThreadName]")

                validateAndLogSize("First Name", payload.firstName, 50)
                validateAndLogSize("Last Name", payload.lastName, 50)
                validateAndLogSize("Email", payload.email, 50)
                validateAndLogSize("Phone Number", payload.phoneNumber, 10)
                validateAndLogSize("User Type", payload.userType, 255)

                val updateRows = query.update(USERPROFILES)
                    .set(USERPROFILES.FIRST_NAME, payload.firstName)
                    .set(USERPROFILES.LAST_NAME, payload.lastName)
                    .set(USERPROFILES.EMAIL, payload.email)
                    .set(USERPROFILES.PHONE_NUMBER, payload.phoneNumber)
                    .set(USERPROFILES.USER_TYPE, payload.userType)
                    .where(USERPROFILES.USERNAME.eq(userName))
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

    private fun validateAndLogSize(fieldName: String, value: String?, maxSize: Int) {
        if (value != null && value.length > maxSize) {
            LOG.error("Field [$fieldName] has a size exceeding the limit (max: $maxSize): $value")
        }
    }






    override suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                LOG.info("Thread ${Thread.currentThread().name} executing update")

                val field = getField(fieldName)
                if (field == null) {
                    LOG.error("Field name [$fieldName] not found!!!")
                    return@withContext false
                }

                val updateRows = query.update(USERPROFILES)
                    .set(field, newValue)
                    .where(USERPROFILES.USER_ID.eq(id))
                    .execute()

                if (updateRows > 0) {
                    LOG.info("Update successful for field [$fieldName] with new value [$newValue] for user ID [$id]")
                } else {
                    LOG.warn("Update did not affect any rows for field [$fieldName] with new value [$newValue] for user ID [$id]")
                }

                updateRows > 0
            } catch (e: Exception) {
                LOG.error("Error updating field [$fieldName] for user ID [$id]", e)
                false
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
        return withContext(Dispatchers.IO) {
            try {
                LOG.info("Thread ${Thread.currentThread().name} executing delete")

                val deletedRows = query.deleteFrom(USERPROFILES)
                    .where(USERPROFILES.USER_ID.eq(id))
                    .execute()

                if (deletedRows > 0) {
                    LOG.info("Delete successful for user with ID [$id]")
                } else {
                    LOG.warn("Delete did not affect any rows for user with ID [$id]")
                }

                deletedRows > 0
            } catch (e: Exception) {
                LOG.error("Error deleting user with ID [$id]", e)
                false
            }
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }

}