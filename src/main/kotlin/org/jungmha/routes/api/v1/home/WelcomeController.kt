package org.jungmha.routes.api.v1.home


import io.micronaut.context.annotation.Bean
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.MediaType
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.tags.Tag
import java.nio.charset.Charset

@Tag(
    name = "Jungmha Wab",
    description = "หน้าเว็บทั้งหมดของ Jungmha"
)
@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
@Controller("/")
@Introspected
class WelcomeController {

    private val resource = javaClass.getResourceAsStream("/public/index.html")
        ?.readBytes()
        ?.toString(Charset.defaultCharset())

    @Get(uri = "/", produces = [MediaType.TEXT_HTML])
    fun public(): String? = resource

    @Get(uri = "/login/user", produces = [MediaType.TEXT_HTML])
    fun loginUser(): String? = resource

    @Get(uri = "/login/caretaker", produces = [MediaType.TEXT_HTML])
    fun loginCaretaker(): String? = resource

    @Get(uri = "/register/user", produces = [MediaType.TEXT_HTML])
    fun registerUser(): String? = resource

    @Get(uri = "/register/caretaker", produces = [MediaType.TEXT_HTML])
    fun registerCaretaker(): String? = resource





}

