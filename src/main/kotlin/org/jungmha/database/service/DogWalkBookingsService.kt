package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkBookingsField
import org.jungmha.database.record.DogWalkBookings

/**
 * DogWalkBookingsService
 *
 * Interface นี้ให้บริการเกี่ยวกับการจัดการข้อมูลการจองเดินเดินสุนัข (Dog Walk Bookings) ในระบบ
 * โดยมีเมธอดต่าง ๆ ที่จะทำหน้าที่ค้นหา แสดงข้อมูลทั้งหมด และดำเนินการเพิ่ม แก้ไข และลบข้อมูลการจอง
 */

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
interface DogWalkBookingsService {

    /**
     * **เมธอด bookingsAll**
     *
     * ใช้สำหรับค้นหาข้อมูลการจองทั้งหมดในระบบ
     *
     * @return รายการข้อมูล DogWalkBookingsField ทั้งหมด
     */
    suspend fun bookingsAll(): List<DogWalkBookingsField>

    /**
     * **เมธอด insert**
     *
     * ใช้สำหรับเพิ่มข้อมูลการจองใหม่
     *
     * @param userID ID ของผู้ใช้งานที่ทำการจอง
     * @param payload ข้อมูลการจอง DogWalkBookings
     * @return true หากการเพิ่มสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun insert(userID: Int, payload: DogWalkBookings): Boolean

    /**
     * **เมธอด updateSingleField**
     *
     * ใช้สำหรับแก้ไขข้อมูลเดี่ยวของการจอง
     *
     * @param id ID ของการจอง
     * @param fieldName ชื่อฟิลด์ที่ต้องการแก้ไข
     * @param newValue ค่าใหม่ที่ใช้ในการแก้ไข
     * @return true หากการแก้ไขสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    /**
     * **เมธอด delete**
     *
     * ใช้สำหรับลบข้อมูลการจอง
     *
     * @param id ID ของการจอง
     * @return true หากการลบสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun delete(id: Int): Boolean

}
