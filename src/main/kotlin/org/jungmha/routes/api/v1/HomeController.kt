package org.jungmha.routes.api.v1

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.nio.charset.Charset

@Controller
class HomeController {

    @Get(uri = "/", produces = [MediaType.TEXT_HTML])
    fun index(): String? {
        return javaClass.getResourceAsStream("/web/index.html")
            ?.readBytes()
            ?.toString(Charset.defaultCharset())
    }
}
