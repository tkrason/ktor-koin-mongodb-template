package com.example.application

import com.example.application.plugins.BEARER_SECURITY_AUTH
import com.example.controller.Controller
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.core.annotation.Singleton

@Singleton
class Router(
    private val controllers: List<Controller>,
) {
    fun routeAll(application: Application) = application.routing {
        controllers.forEach {
            when (it.useBearerAuth) {
                true -> authenticate(BEARER_SECURITY_AUTH) { registerRoutes(it) }
                false -> registerRoutes(it)
            }
        }
    }

    private fun Route.registerRoutes(controller: Controller) = route(controller.basePath) {
        controller.registerRoutes(route = this)
    }
}
