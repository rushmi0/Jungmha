package org.jungmha.database.service

import jakarta.inject.Inject
import nu.studer.sample.tables.Userprofiles.USERPROFILES
import org.jooq.DSLContext
import org.jooq.Record
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.UserProfileForm

class UserService(
    @Inject
    private val query: DSLContext
) {


    fun findUserName(accountName: String): UserProfileField? {
        val record: Record? = query.select()
            .from(USERPROFILES)
            .where(USERPROFILES.USERNAME.eq(accountName))
            .fetchOne()

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

    fun insert(payload: UserProfileForm): Boolean {
        try {
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

            // ถ้าไม่เกิดข้อผิดพลาดในขณะที่ execute คำสั่ง SQL
            return true
        } catch (e: Exception) {
            // จัดการข้อผิดพลาด (Exception) ตามที่คุณต้องการ
            return false
        }
    }


}
