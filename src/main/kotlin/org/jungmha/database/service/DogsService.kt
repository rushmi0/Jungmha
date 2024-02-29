package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogField
import org.jungmha.database.form.DogForm

/**
 * **DogsService**
 *
 * Interface นี้ให้บริการเกี่ยวกับการจัดการข้อมูลสุนัขในระบบ
 * โดยมีเมธอดต่าง ๆ ที่จะทำหน้าที่ค้นหา แสดงข้อมูลทั้งหมด และดำเนินการเพิ่ม แก้ไข และลบข้อมูลสุนัข
 */

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
interface DogsService {

    /**
     * **เมธอด findDog**
     *
     * ใช้สำหรับค้นหาข้อมูลสุนัขตาม ID ที่กำหนด
     *
     * @param dogID ID ของสุนัข
     * @return DogField หรือ null ถ้าไม่พบข้อมูล
     */
    suspend fun findDog(dogID: Int): DogField?

    /**
     * **เมธอด dogsAll**
     *
     * ใช้สำหรับแสดงข้อมูลทั้งหมดของสุนัข
     *
     * @return รายการข้อมูล DogField ทั้งหมด
     */
    suspend fun dogsAll(): List<DogField>

    /**
     * **เมธอด insert**
     *
     * ใช้สำหรับเพิ่มข้อมูลสุนัขใหม่
     *
     * @param payload ข้อมูลสำหรับสร้าง DogForm
     * @return true หากการเพิ่มสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun insert(payload: DogForm): Boolean

    /**
     * **เมธอด updateSingleField**
     *
     * ใช้สำหรับแก้ไขข้อมูลเดี่ยวของสุนัข
     *
     * @param id ID ของสุนัข
     * @param fieldName ชื่อฟิลด์ที่ต้องการแก้ไข
     * @param newValue ค่าใหม่ที่ใช้ในการแก้ไข
     * @return true หากการแก้ไขสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    /**
     * **เมธอด delete**
     *
     * ใช้สำหรับลบข้อมูลสุนัข
     *
     * @param id ID ของสุนัข
     * @return true หากการลบสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun delete(id: Int): Boolean

}
