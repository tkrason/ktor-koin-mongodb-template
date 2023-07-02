package com.example.application.plugins

import com.example.application.config.Config
import io.ktor.server.application.Application
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.bearer

const val BEARER_SECURITY_AUTH = "bearer-security"

fun Application.configureSecurity(config: Config) {
    authentication {
        bearer(BEARER_SECURITY_AUTH) {
            authenticate {
                if (it.token == config.apiKey) UserIdPrincipal("Ktor sample server token") else null
            }
        }
    }
}
