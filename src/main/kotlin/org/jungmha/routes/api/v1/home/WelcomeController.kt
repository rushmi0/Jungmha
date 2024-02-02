package org.jungmha.routes.api.v1.home

import io.micronaut.context.annotation.Bean
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.swagger.v3.oas.annotations.tags.Tag
import java.nio.charset.Charset

@Tag(
    name = "CamRent",
    description = "เว็บตัวอย่าง CamRent"
)
@Bean
@Controller("/")
class WelcomeController {

    private val resource = javaClass.getResourceAsStream("/public/index.html")
        ?.readBytes()
        ?.toString(Charset.defaultCharset())

    @Get(uri = "/", produces = [MediaType.TEXT_HTML])
    fun public(): String? = resource

    @Get(uri = "/sh", produces = [MediaType.TEXT_HTML])
    fun shop(): String? = resource

    @Get(uri = "/login/user", produces = [MediaType.TEXT_HTML])
    fun login(): String? = resource
    @Get(uri = "/login/caretaker", produces = [MediaType.TEXT_HTML])
    fun loginCaretaker(): String? = resource

    @Get(uri = "/register/user", produces = [MediaType.TEXT_HTML])
    fun register(): String? = resource
    @Get(uri = "/register/caretaker", produces = [MediaType.TEXT_HTML])
    fun registerCaretaker(): String? = resource

    @Get(uri = "/profile", produces = [MediaType.TEXT_HTML])
    fun profile(): String? = resource

    @Get(uri = "/CustomerHistory", produces = [MediaType.TEXT_HTML])
    fun customerHistory(): String? = resource

    @Get(uri = "/customerhistory", produces = [MediaType.TEXT_HTML])
    fun CustomerHistory(): String? = resource

    @Get(uri = "/customerrentorders", produces = [MediaType.TEXT_HTML])
    fun customerrentorders(): String? = resource


//    @Get(uri = "/", produces = [MediaType.TEXT_HTML])
//    fun shop(): String? {
//        return javaClass.getResourceAsStream("/web/index.html")
//            ?.readBytes()
//            ?.toString(Charset.defaultCharset())
//    }

}

