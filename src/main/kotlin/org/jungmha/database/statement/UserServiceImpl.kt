package org.jungmha.database.statement

import io.micronaut.context.annotation.Bean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.jooq.TableField
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.UserProfileForm
import org.jungmha.infra.database.tables.Userprofiles.USERPROFILES
import org.jungmha.infra.database.tables.records.UserprofilesRecord
import org.jungmha.service.UserService

@Bean
class UserServiceImpl(
    @Bean
    private val query: DSLContext
) : UserService {

    override suspend fun findUser(accountName: String): UserProfileField? {
        val record: Record? = withContext(Dispatchers.IO) {
            query.select()
                .from(USERPROFILES)
                .where(USERPROFILES.USERNAME.eq(accountName))
                .fetchOne()
        }

        return record?.let {
            UserProfileField(
                it[USERPROFILES.USER_ID],
                it[USERPROFILES.IMAGE_PROFILE],
                it[USERPROFILES.USERNAME],
                it[USERPROFILES.FIRST_NAME],
                it[USERPROFILES.LAST_NAME],
                it[USERPROFILES.EMAIL],
                it[USERPROFILES.PHONE_NUMBER],
                it[USERPROFILES.AUTHEN_KEY],
                it[USERPROFILES.CREATED_AT].toString(),
                it[USERPROFILES.USER_TYPE]
            )
        }
    }


    override suspend fun userAll(): List<UserProfileField> {
        val result: Result<Record> = withContext(Dispatchers.IO) {
            query.select()
                .from(USERPROFILES)
                .fetch()
        }

        return result.map { record ->
            UserProfileField(
                record[USERPROFILES.USER_ID],
                record[USERPROFILES.IMAGE_PROFILE],
                record[USERPROFILES.USERNAME],
                record[USERPROFILES.FIRST_NAME],
                record[USERPROFILES.LAST_NAME],
                record[USERPROFILES.EMAIL],
                record[USERPROFILES.PHONE_NUMBER],
                record[USERPROFILES.AUTHEN_KEY],
                record[USERPROFILES.CREATED_AT].toString(),
                record[USERPROFILES.USER_TYPE]
            )
        }
    }



    override suspend fun insert(payload: UserProfileForm): Boolean {
        try {
            withContext(Dispatchers.IO) {
                query.insertInto(
                    USERPROFILES,
                    USERPROFILES.USERNAME,
                    USERPROFILES.FIRST_NAME,
                    USERPROFILES.LAST_NAME,
                    USERPROFILES.EMAIL,
                    USERPROFILES.PHONE_NUMBER,
                    USERPROFILES.AUTHEN_KEY,
                    USERPROFILES.USER_TYPE
                )
                    .values(
                        payload.userName,
                        payload.firstName,
                        payload.lastName,
                        payload.email,
                        payload.phoneNumber,
                        payload.authenKey,
                        payload.userType
                    )
                    .execute()
            }

            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun update(id: Int, fieldName: String, newValue: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val updateRows = query.update(USERPROFILES)
                    .set(getField(fieldName), newValue)  // เรียกใช้เมทอด getField ที่ได้กำหนดไว้ด้านล่าง
                    .where(USERPROFILES.USER_ID.eq(id))
                    .execute()
                updateRows > 0
            } catch (e: Exception) {
                false
            }
        }
    }

    // สร้างเมทอดเพิ่มเติมเพื่อ map ชื่อฟิลด์กับคอลัมน์ใน JOOQ
    private fun getField(fieldName: String): TableField<UserprofilesRecord, String>? {
        return when (fieldName) {
            "imageProfile" -> USERPROFILES.IMAGE_PROFILE
            "userName" -> USERPROFILES.USERNAME
            "email" -> USERPROFILES.EMAIL
            "phoneNumber" -> USERPROFILES.PHONE_NUMBER
            "userType" -> USERPROFILES.USER_TYPE
            "authKey" -> USERPROFILES.AUTHEN_KEY
            else -> throw IllegalArgumentException("Field name [$fieldName] not found!!!")
        }
    }

    override suspend fun delete(id: Int): Boolean {
        try {
            val deletedRows = withContext(Dispatchers.IO) {
                query.deleteFrom(USERPROFILES)
                    .where(USERPROFILES.USER_ID.eq(id))
                    .execute()
            }

            return deletedRows > 0
        } catch (e: Exception) {
            return false
        }
    }


}