package org.jungmha

import com.sun.org.slf4j.internal.LoggerFactory
import io.micronaut.context.annotation.Bean
import io.micronaut.runtime.Micronaut
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.info.*


@OpenAPIDefinition(
    info = Info(
            title = "jungmha",
            version = "0.1"
    )
)
object Api {
}

@Bean
@RequestScope
@ExecuteOn(TaskExecutors.IO)
object Application {

    private val LOG = LoggerFactory.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {

        val symbols = """
                                         .--..--..--..--..--..--.
                                        .' \  (`._   (_)     _   \
                                      .'    |  '._)         (_)  |
                                      \ _.')\      .----..---.   /
                                      |(_.'  |    /    .-\-.  \  |
                                      \     0|    |   ( O| O) | o|
                                       |  _  |  .--.____.'._.-.  |
                                       \ (_) | o         -` .-`  |
                                        |    \   |`-._ _ _ _ _\ /
                                        \    |   |  `. |_||_|   |
                                        | o  |    \_      \     |     -.   .-.
                                        |.-.  \     `--..-'   O |     `.`-' .'
                                      _.'  .' |     `-.-'      /-.__   ' .-'
                                    .' `-.` '.|='=.='=.='=.='=|._/_ `-'.'
                                    `-._  `.  |________/\_____|    `-.'
                                       .'   ).| '=' '='\/ '=' |
                                       `._.`  '---------------'
                                               //___\   //___\
                                                 ||       ||
                                                 ||_.-.   ||_.-.
                                                (_.--__) (_.--__) 
        """.trimIndent()
        println(symbols)
        Micronaut.run(Application.javaClass)
    }

}