package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkerReviewField
import org.jungmha.database.form.DogWalkerReviewForm

/**
 * **Interface DogWalkerReviewService**
 *
 * Interface นี้ให้บริการเกี่ยวกับการจัดการข้อมูลรีวิวของผู้เดินสุนัข (Dog Walker Reviews) ในระบบ
 * โดยมีเมธอดต่าง ๆ ที่จะทำหน้าที่ค้นหา แสดงข้อมูลทั้งหมด และดำเนินการเพิ่ม แก้ไข และลบข้อมูลรีวิว
 */

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
interface DogWalkerReviewService {

    /**
     * **เมธอด dogWalkerReviewAll**
     *
     * ใช้สำหรับค้นหาข้อมูลรีวิวของผู้เดินสุนัขทั้งหมดในระบบ
     *
     * @return รายการข้อมูล DogWalkerReviewField ทั้งหมด
     */
    suspend fun dogWalkerReviewAll(): List<DogWalkerReviewField>

    /**
     * **เมธอด insert**
     *
     * ใช้สำหรับเพิ่มข้อมูลรีวิวของผู้เดินสุนัขใหม่
     *
     * @param payload ข้อมูลรีวิว DogWalkerReviewForm
     * @return true หากการเพิ่มสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun insert(payload: DogWalkerReviewForm): Boolean

    /**
     * **เมธอด updateSingleField**
     *
     * ใช้สำหรับแก้ไขข้อมูลเดี่ยวของรีวิว
     *
     * @param id ID ของรีวิว
     * @param fieldName ชื่อฟิลด์ที่ต้องการแก้ไข
     * @param newValue ค่าใหม่ที่ใช้ในการแก้ไข
     * @return true หากการแก้ไขสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    /**
     * **เมธอด delete**
     *
     * ใช้สำหรับลบข้อมูลรีวิว
     *
     * @param id ID ของรีวิว
     * @return true หากการลบสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun delete(id: Int): Boolean

}
