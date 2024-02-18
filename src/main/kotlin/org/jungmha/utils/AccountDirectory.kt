package org.jungmha.utils

import io.micronaut.core.annotation.Introspected
import org.jungmha.utils.AccountDirectory.randomFileName
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

@Introspected
object AccountDirectory {

    val LOG: Logger = LoggerFactory.getLogger(AccountDirectory::class.java)

    private fun String.listFilesInDefaultDirectory(): List<String> {
        val defaultDirectory = File(this)
        return if (defaultDirectory.exists() && defaultDirectory.isDirectory) {
            defaultDirectory.listFiles()?.map { file -> this + "/" + file.name } ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun randomFileName(path: String): String? {
        val files = path.listFilesInDefaultDirectory()
        return if (files.isNotEmpty()) {
            val randomIndex = files.indices.random()
            files[randomIndex]
        } else {
            null
        }
    }


    fun createDirectory(typeAccount: String, directoryID: Int): Boolean {
        // กำหนดเส้นทางของไดเร็กทอรีหลัก
        val baseDirectory = "src/main/resources/images/account/$typeAccount/usr_$directoryID"

        return try {

            when (typeAccount) {

                "DogWalkers",
                "Normal" -> {
                    File("$baseDirectory/profileImage").apply { mkdirs() }
                    true
                }

                else -> false
            }

        } catch (e: Exception) {
            LOG.error("There was an error in creating a directory: ${e.message}")
            false
        }
    }


    fun deleteFilesInPathAndCheckExistence(filePath: String): Boolean {
        val fileToDelete = File(filePath)
        return try {
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