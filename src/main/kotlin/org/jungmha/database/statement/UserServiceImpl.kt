package org.jungmha.database.statement

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.UserProfileForm
import org.jungmha.infra.database.tables.Userprofiles
import org.jungmha.service.UserService

class UserServiceImpl(
    @Inject
    private val query: DSLContext
) : UserService {

    override suspend fun findUser(accountName: String): UserProfileField? {
        val record: Record? = query.select()
            .from(Userprofiles.USERPROFILES)
            .where(Userprofiles.USERPROFILES.USERNAME.eq(accountName))
            .fetchOne()

        return record?.let {
            UserProfileField(
                it[Userprofiles.USERPROFILES.USER_ID],
                it[Userprofiles.USERPROFILES.IMAGE_PROFILE],
                it[Userprofiles.USERPROFILES.USERNAME],
                it[Userprofiles.USERPROFILES.FIRST_NAME],
                it[Userprofiles.USERPROFILES.LAST_NAME],
                it[Userprofiles.USERPROFILES.EMAIL],
                it[Userprofiles.USERPROFILES.PHONE_NUMBER],
                it[Userprofiles.USERPROFILES.AUTHEN_KEY],
                it[Userprofiles.USERPROFILES.CREATED_AT].toString(),
                it[Userprofiles.USERPROFILES.USER_TYPE]
            )
        }
    }

    override suspend fun insert(payload: UserProfileForm): Boolean {
        try {
            query.insertInto(
                Userprofiles.USERPROFILES,
                Userprofiles.USERPROFILES.USERNAME,
                Userprofiles.USERPROFILES.FIRST_NAME,
                Userprofiles.USERPROFILES.LAST_NAME,
                Userprofiles.USERPROFILES.EMAIL,
                Userprofiles.USERPROFILES.PHONE_NUMBER,
                Userprofiles.USERPROFILES.AUTHEN_KEY,
                Userprofiles.USERPROFILES.USER_TYPE
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