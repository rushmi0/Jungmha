package org.jungmha.security.securekey

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.*


data class user(
    val id: Int,
    val authenKey: String,
    val createdAt: Long
)

object Token {


    fun buildToken(id: Int, authenKey: String): String? {

        val timestampInSeconds = (Date().time / 1000).coerceAtLeast(0) + 2592000

        val userObject = user(
            id,
            authenKey,
            timestampInSeconds
        )

        val text = jacksonObjectMapper().writeValueAsString(userObject)
        val encode = text.toByteArray(charset("UTF-8"))
        return Base64.getEncoder().encodeToString(encode)
    }


    fun detectedToken(token: String): user {

        val decode = Base64.getDecoder().decode(token)

        val jsonMap = jacksonObjectMapper().readValue<Map<String, Any>>(decode)
        val data = jacksonObjectMapper().writeValueAsString(jsonMap)
        return jacksonObjectMapper().readValue(data)
    }


}

//fun main() {
//
//
//    val id = 12
//    val authenKey = "028fda492e3522673b0b0561526e4b1b96b3bdf81484ca5a1db4f30125fc04be54"
//
//    val token = Token.buildToken(id, authenKey)
//    val data = token?.let { Token.detectedToken(it) }
//
//    println(token)
//    println(data)
//
//}