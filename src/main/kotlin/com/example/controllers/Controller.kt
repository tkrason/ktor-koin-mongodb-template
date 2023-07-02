package com.example.controllers

import io.ktor.server.routing.Route

interface Controller {
    fun registerRoutes(route: Route): Unit
}
