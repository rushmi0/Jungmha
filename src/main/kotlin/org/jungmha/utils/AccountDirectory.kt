package org.jungmha.utils

import io.micronaut.core.annotation.Introspected
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

@Introspected
object AccountDirectory {

    val LOG: Logger = LoggerFactory.getLogger(AccountDirectory::class.java)

    fun createDirectory(typeAccount: String, directoryID: Int): Boolean {
        // กำหนดเส้นทางของไดเร็กทอรีหลัก
        val baseDirectory = "src/main/resources/images/account/$typeAccount/usr_$directoryID"

        return try {

            when (typeAccount) {

                "DogWalkers",
                "Normal" -> {
                    // สร้างไดเร็กทอรี `profileImage` สำหรับลูกค้า
                    File("$baseDirectory/profileImage").apply { mkdirs() }
                    true // สร้างไดเร็กทอรีสำเร็จ
                }

                else -> false
            }

        } catch (e: Exception) {
            // แสดงข้อผิดพลาดที่เกิดขึ้น
            LOG.error("There was an error in creating a directory: ${e.message}")
            false
        }
    }


    fun deleteFilesInPathAndCheckExistence(filePath: String): Boolean {
        // สร้างอ็อบเจกต์ของ File จากที่กำหนดให้
        val fileToDelete = File(filePath)

        return try {
            // ตรวจสอบว่าไฟล์หรือเส้นทางที่ระบุมีอยู่หรือไม่
            if (!fileToDelete.exists() || !fileToDelete.isFile) {
                LOG.warn("The specified file path does not exist: $filePath")
            }

            // ทำการลบไฟล์
            val deleted = fileToDelete.delete()

            // ตรวจสอบผลการลบไฟล์และแสดงผลลัพธ์
            if (deleted) {
                LOG.info("The [$filePath] file has been deleted")
            } else {
                LOG.error("cannot to delete [$filePath] files")
            }

            // ส่งค่าผลลัพธ์การลบไฟล์ออกไป
            deleted
        }catch (e: IllegalArgumentException) {
            LOG.error("IllegalArgumentException: ${e.message}")
            false
        } catch (e: SecurityException) {
            LOG.error("SecurityException: ${e.message}")
            false
        } catch (e: Exception) {
            LOG.error("Unexpected error: ${e.message}")
            false
        }
    }


}