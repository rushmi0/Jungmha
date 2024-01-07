package org.jungmha.database.service

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import org.jungmha.database.field.SignatureField
import org.jungmha.database.form.SignatureForm

/**
 * **Interface SignatureService**
 *
 * Interface นี้ให้บริการเกี่ยวกับการจัดการข้อมูลลายเซ็นต์ (Signature) ในระบบ
 * โดยมีเมธอดต่าง ๆ ที่จะทำหน้าที่ค้นหา ตรวจสอบลายเซ็นต์ และดำเนินการเพิ่มข้อมูลลายเซ็นต์
 */

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
interface SignatureService {

    /**
     * **เมธอด signAll**
     *
     * ใช้สำหรับค้นหาข้อมูลลายเซ็นต์ทั้งหมดในระบบ
     *
     * @return รายการข้อมูล SignatureField ทั้งหมด
     */
    suspend fun signAll(): List<SignatureField>

    /**
     * **เมธอด checkSign**
     *
     * ใช้สำหรับตรวจสอบลายเซ็นต์ของผู้ใช้
     *
     * @param userName ชื่อผู้ใช้
     * @param signature ลายเซ็นต์ที่ต้องการตรวจสอบ
     * @return true หากลายเซ็นต์ตรงกับที่มีในระบบ, false หากไม่ตรงหรือเกิดข้อผิดพลาด
     */
    suspend fun checkSign(userName: String, signature: String): Boolean

    /**
     * **เมธอด insert**
     *
     * ใช้สำหรับเพิ่มข้อมูลลายเซ็นต์ใหม่
     *
     * @param payload ข้อมูลลายเซ็นต์ SignatureForm
     * @return true หากการเพิ่มลายเซ็นต์สำเร็จ, false หากไม่สำเร็จ
     */
    suspend fun insert(payload: SignatureForm): Boolean

}
