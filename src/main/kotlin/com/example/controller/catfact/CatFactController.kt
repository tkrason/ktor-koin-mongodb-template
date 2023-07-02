package com.example.controller.catfact

import com.example.controller.Controller
import com.example.controller.catfact.dto.toResponseDto
import com.example.services.CatFactService
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.core.annotation.Singleton

@Singleton
class CatFactController(
    private val catFactService: CatFactService,
) : Controller(basePath = "/cat-fact") {

    override fun registerRoutes(route: Route) {
        route.getFactFromApi()
        route.inMemoryFact()
    }

    private fun Route.getFactFromApi() = get("/from-api") {
        val catFact = catFactService.getFactFromApi()
        call.respond(catFact.toResponseDto())
    }

    private fun Route.inMemoryFact() {
        route("/from-memory") {
            get("/fast") {
                call.respond(catFactService.getFastCatFact())
            }
            get("/slow") {
                call.respond(catFactService.getSlowCatFact())
            }
        }
    }
}
