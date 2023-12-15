package org.jungmha.utils

import java.io.File

object AccountDirectory {


    fun createDirectory(typeAccount: String, directoryID: Int): Boolean {
        // กำหนดเส้นทางของไดเร็กทอรีหลัก
        val baseDirectory = "src/main/resources/images/account/$typeAccount/usr_$directoryID"

        return try {
            when {
                typeAccount == "Normal" -> {
                    // สร้างไดเร็กทอรี `profileImage` สำหรับลูกค้า
                    File("$baseDirectory/profileImage").apply { mkdirs() }
                    true // สร้างไดเร็กทอรีสำเร็จ
                }

                typeAccount == "DogWalkers" -> {
                    // สร้างไดเร็กทอรี `profileImage` สำหรับร้านค้า
                    File("$baseDirectory/profileImage").apply { mkdirs() }
                    File("$baseDirectory/kyc").apply { mkdirs() }
                    true // สร้างไดเร็กทอรีสำเร็จ
                }

                else -> false // ไม่สามารถสร้างไดเร็กทอรี เนื่องจากไม่รู้จักประเภทบัญชี
            }

        } catch (e: Exception) {
            // แสดงข้อผิดพลาดที่เกิดขึ้น
            println("เกิดข้อผิดพลาดในการสร้างไดเร็กทอรี: ${e.message}")
            false
        }
    }


}