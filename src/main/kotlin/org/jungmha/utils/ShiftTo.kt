package org.jungmha.utils

import io.micronaut.core.annotation.Introspected
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

@Introspected
object ShiftTo {


    fun ByteArray.ByteArrayToBigInteger(): BigInteger {
        return BigInteger(1, this)
    }


    fun ByteArray.ByteArrayToHex(): String {
        return joinToString("") { byte -> byte.toUByte().toString(16).padStart(2, '0') }
    }

    fun String.HexToByteArray(): ByteArray = ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }



    fun ByteArray.SHA256(): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(this)
    }


    fun String.SHA256(): ByteArray {
        return toByteArray().SHA256()
    }


    fun String.toFileName(): String {
        val fileName = File(this).name
        return fileName.substringAfterLast(File.separator)
    }


}