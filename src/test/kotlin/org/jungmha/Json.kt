package org.jungmha

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.jungmha.database.record.BookingList
import org.jungmha.database.record.NormalInfo
import java.time.OffsetDateTime
import java.time.LocalDate
import java.time.LocalTime

fun main() {
    // Instantiate ObjectMapper
    val objectMapper = ObjectMapper().registerModule(KotlinModule()).registerModule(JavaTimeModule())

    // Sample data
    val demoData = NormalInfo(
        userID = 12,
        profileImage = "https://jungmha.notoshi.win/api/v1/user/Aura/image/9deab787",
        userName = "Aura",
        firstName = "Aurora",
        lastName = "Smith",
        locationName = "เขตพระขโนง",
        email = "asda@gmail.com",
        phoneNumber = "0123456778",
        accountType = "Normal",
        booking = listOf(
            BookingList(
                bookingID = 4,
                userName = "Jane",
                breedName = "German Shepherd",
                size = "Big",
                status = "Pending",
                bookingDate = LocalDate.parse("2024-02-23"),
                timeStart = LocalTime.parse("14:00"),
                timeEnd = LocalTime.parse("15:00"),
                duration = LocalTime.parse("01:00"),
                total = 40,
                timeStamp = OffsetDateTime.parse("2024-02-23T00:48:32.005946+07:00"),
                serviceStatus = "In Progress"
            )
        )
    )

    // Convert Kotlin object to JSON string
    val jsonString = objectMapper.writeValueAsString(demoData)
    println(jsonString)
}
