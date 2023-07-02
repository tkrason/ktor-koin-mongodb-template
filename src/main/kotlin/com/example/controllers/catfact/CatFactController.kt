package com.example.controllers.catfact

import com.example.clients.catfact.CatFactClient
import com.example.controllers.Controller
import com.example.controllers.catfact.dto.toResponseDto
import com.example.services.InMemoryFastCatFactService
import com.example.services.InMemorySlowCatFactService
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.koin.core.annotation.Singleton

@Singleton
class CatFactController(
    private val catFactClient: CatFactClient,
    private val inMemoryFastCatFactService: InMemoryFastCatFactService,
    private val inMemorySlowCatFactService: InMemorySlowCatFactService,
) : Controller(basePath = "/cat-fact") {

    override fun registerRoutes(route: Route) {
        route.getFactFromApi()
        route.inMemoryFact()
    }

    private fun Route.getFactFromApi() = get("/from-api") {
        val catFact = catFactClient.getCatFact()
        call.respond(catFact.toResponseDto())
    }

    private fun Route.inMemoryFact() {
        route("/from-memory") {
            get("/fast") {
                call.respond(getInMemoryFastFact().toResponseDto())
            }
            get("/slow") {
                call.respond(getInMemorySlowFact().toResponseDto())
            }
        }
    }

    private fun getInMemoryFastFact() = inMemoryFastCatFactService.getFastCatFact()

    private suspend fun getInMemorySlowFact() = inMemorySlowCatFactService.getSlowCatFact()
}
