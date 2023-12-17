package org.jungmha.database.field

import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.serde.annotation.Serdeable

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Serdeable.Serializable
data class UserProfileField(
    val userID: Int,
    val imageProfile: String,
    val userName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val authenKey: String,
    val createAt: String,
    val userType: String
)
