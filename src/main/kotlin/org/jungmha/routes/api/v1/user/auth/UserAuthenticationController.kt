package org.jungmha.routes.api.v1.user.auth

import io.micronaut.http.annotation.Controller
import io.micronaut.runtime.http.scope.RequestScope
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn

@Controller("api/v1")
@RequestScope
@ExecuteOn(TaskExecutors.IO)
class UserAuthenticationController {



}