package org.jungmha.routes.api.v1.home


import io.micronaut.context.annotation.Bean
import io.micronaut.http.annotation.*
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import jakarta.inject.Inject
import org.jungmha.database.statement.DogsWalkersServiceImpl
import org.jungmha.security.securekey.Token
import org.slf4j.LoggerFactory


@Controller("api/v1")
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class HomeController @Inject constructor(
    private val service: DogsWalkersServiceImpl,
    private val token: Token
){


    companion object {
        private val LOG = LoggerFactory.getLogger(HomeController::class.java)
    }


}