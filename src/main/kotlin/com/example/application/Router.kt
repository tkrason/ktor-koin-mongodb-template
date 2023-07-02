package com.example.application

import com.example.application.plugins.BEARER_SECURITY_AUTH
import com.example.controllers.Controller
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import org.koin.core.annotation.Singleton

@Singleton
class Router(
    private val controllers: List<Controller>,
) {
    fun routeAll(application: Application) = application.routing {
        authenticate(BEARER_SECURITY_AUTH) {
            controllers.forEach { it.registerRoutes(route = this) }
        }
    }
}
