package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.DogWalkerField
import org.jungmha.database.record.DogWalkersInfo
import org.jungmha.database.record.PrivateDogWalkerInfo
import org.jungmha.database.record.PublicDogWalkerInfo

/**
 * **Interface DogsWalkersService**
 *
 * Interface นี้ให้บริการเกี่ยวกับการจัดการข้อมูลคนเดินสุนัข (Dog Walkers) ในระบบ
 * โดยมีเมธอดต่าง ๆ ที่จะทำหน้าที่ค้นหา แสดงข้อมูลทั้งหมด และดำเนินการเพิ่ม แก้ไข และลบข้อมูลคนเดินสุนัข
 */

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Introspected
interface DogsWalkersService {

    /**
     * **เมธอด getSingleDogWalkersInfo**
     *
     * ใช้สำหรับค้นหาข้อมูลคนเดินสุนัขตาม ID ที่กำหนด
     *
     * @param id ID ของคนเดินสุนัข
     * @return DogWalkerField หรือ null ถ้าไม่พบข้อมูล
     */
    suspend fun getSingleDogWalkersInfo(id: Int): DogWalkerField?

    /**
     * **เมธอด getDogWalkersInfo**
     *
     * ใช้สำหรับค้นหาข้อมูลคนเดินสุนัขตามชื่อบัญชีผู้ใช้
     *
     * @param accountName ชื่อบัญชีผู้ใช้ของคนเดินสุนัข
     * @return DogWalkersInfo หรือ null ถ้าไม่พบข้อมูล
     */
    suspend fun getDogWalkersInfo(accountName: String): DogWalkersInfo?

    /**
     * **เมธอด publicDogWalkersAll**
     *
     * ใช้สำหรับแสดงข้อมูลคนเดินสุนัขทั้งหมดที่เปิดให้บริการสาธารณะ
     *
     * @return รายการข้อมูล PublicDogWalkerInfo ทั้งหมด
     */
    suspend fun publicDogWalkersAll(): List<PublicDogWalkerInfo>

    /**
     * **เมธอด privateDogWalkersAll**
     *
     * ใช้สำหรับแสดงข้อมูลคนเดินสุนัขทั้งหมดที่เปิดให้บริการเฉพาะ
     *
     * @return รายการข้อมูล PrivateDogWalkerInfo ทั้งหมด
     */
    suspend fun privateDogWalkersAll(): List<PrivateDogWalkerInfo>

    /**
     * **เมธอด insert**
     *
     * ใช้สำหรับเพิ่มข้อมูลคนเดินสุนัขใหม่
     *
     * @param id ID ของผู้ใช้งาน
     * @return true หากการเพิ่มสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun insert(id: Int): Boolean

    /**
     * **เมธอด updateSingleField**
     *
     * ใช้สำหรับแก้ไขข้อมูลเดี่ยวของคนเดินสุนัข
     *
     * @param id ID ของคนเดินสุนัข
     * @param fieldName ชื่อฟิลด์ที่ต้องการแก้ไข
     * @param newValue ค่าใหม่ที่ใช้ในการแก้ไข
     * @return true หากการแก้ไขสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun updateSingleField(id: Int, fieldName: String, newValue: String): Boolean

    /**
     * **เมธอด delete**
     *
     * ใช้สำหรับลบข้อมูลคนเดินสุนัข
     *
     * @param id ID ของคนเดินสุนัข
     * @return true หากการลบสำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun delete(id: Int): Boolean

}
