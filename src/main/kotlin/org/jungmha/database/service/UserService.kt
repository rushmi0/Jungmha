package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.UserProfileField
import org.jungmha.database.form.IdentityForm
import org.jungmha.database.form.UserProfileForm
import org.jungmha.database.record.NormalInfo

/**
 * **ข้อมูล Interface UserService**
 *
 * Interface นี้ใช้เพื่อกำหนดส่วนของ Contract สำหรับ Service ที่เกี่ยวข้องกับข้อมูลผู้ใช้ (User) ในระบบ
 * โดยจะอธิบายหน้าที่และการทำงานของแต่ละเมธอดที่ปรากฏใน Interface นี้ดังนี้
 */

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
interface UserService {

    /**
     * **ดึงข้อมูลผู้ใช้งานจากชื่อบัญชี**
     *
     * @param accountName ชื่อบัญชีผู้ใช้
     * @return ข้อมูลผู้ใช้งานแบบ NormalInfo หรือ null ถ้าไม่พบข้อมูล
     */
    suspend fun getUserInfo(accountName: String): NormalInfo?

    /**
     * **ดึงข้อมูลทั้งหมดของผู้ใช้งาน**
     *
     * @return รายการข้อมูล UserProfileField ทั้งหมด
     */
    suspend fun userAll(): List<UserProfileField>

    /**
     * **ค้นหาข้อมูลผู้ใช้งานจากชื่อบัญชี**
     *
     * @param accountName ชื่อบัญชีผู้ใช้
     * @return ข้อมูลผู้ใช้งานแบบ UserProfileField หรือ null ถ้าไม่พบข้อมูล
     */
    suspend fun findUser(accountName: String): UserProfileField?

    /**
     * **เพิ่มข้อมูลผู้ใช้งานใหม่**
     *
     * @param payload ข้อมูลสำหรับสร้าง IdentityForm
     * @return true หากการเพิ่มสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun insert(payload: IdentityForm): Boolean

    /**
     * **แก้ไขข้อมูลหลายฟิลด์ของผู้ใช้งาน**
     *
     * @param userName ชื่อผู้ใช้
     * @param payload ข้อมูลที่ใช้ในการแก้ไข UserProfileForm
     * @return true หากการแก้ไขสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun updateMultiField(userName: String, payload: UserProfileForm): Boolean

    /**
     * **แก้ไขข้อมูลเดี่ยวของผู้ใช้งาน**
     *
     * @param id ไอดีของผู้ใช้
     * @param fieldName ชื่อฟิลด์ที่ต้องการแก้ไข
     * @param newValue ค่าใหม่ที่ใช้ในการแก้ไข
     * @return true หากการแก้ไขสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    /**
     * **ลบข้อมูลผู้ใช้งาน**
     *
     * @param id ไอดีของผู้ใช้
     * @return true หากการลบสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun delete(id: Int): Boolean

}
