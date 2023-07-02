package com.example.application.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.AuthScheme
import io.github.smiley4.ktorswaggerui.dsl.AuthType
import io.ktor.server.application.Application
import io.ktor.server.application.install

fun Application.registerSwagger() {
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger-ui"
            forwardRoot = true
        }
        info {
            title = "Ktor Koin template API docs"
            version = "latest"
        }

        defaultSecuritySchemeNames = listOf(BEARER_SECURITY_AUTH)
        securityScheme(BEARER_SECURITY_AUTH) {
            type = AuthType.HTTP
            scheme = AuthScheme.BEARER
            description = "Type in the api key"
        }
    }
}
