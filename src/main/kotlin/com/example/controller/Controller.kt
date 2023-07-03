package com.example.controller

import io.ktor.server.routing.Route

abstract class Controller(val basePath: String) {
    fun registerRoutes(route: Route) = with(route) {
        this.routesForRegistrationOnBasePath()
    }

    abstract fun Route.routesForRegistrationOnBasePath()
}
