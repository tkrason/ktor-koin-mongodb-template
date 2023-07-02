package com.example.controllers

import io.ktor.server.routing.Route

abstract class Controller(val basePath: String) {
    abstract fun registerRoutes(route: Route)
}
