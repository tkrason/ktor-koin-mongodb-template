package com.example.controller

import io.ktor.server.routing.Route

abstract class Controller(val basePath: String, val useBearerAuth: Boolean = true) {
    fun registerRoutes(route: Route) = with(route) {
        this.routesForRegistrationOnBasePath()
    }

    abstract fun Route.routesForRegistrationOnBasePath()
}
