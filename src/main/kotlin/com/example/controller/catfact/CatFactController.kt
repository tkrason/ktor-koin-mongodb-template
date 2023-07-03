package com.example.controller.catfact

import com.example.controller.Controller
import com.example.controller.catfact.dto.SaveCatFactsRequestBodyListWrapper
import com.example.controller.catfact.dto.toModels
import com.example.controller.catfact.dto.toResponseDto
import com.example.services.CatFactService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.core.annotation.Singleton

@Singleton
class CatFactController(
    private val catFactService: CatFactService,
) : Controller(basePath = "/cat-fact") {

    override fun Route.routesForRegistrationOnBasePath() {
        getFactFromApi()
        getFactFromMemory()
        getFactFromDatabase()

        findCatFact()

        saveAllFactsToDatabase()
    }

    private fun Route.getFactFromApi() = get("/from-api") {
        val catFact = catFactService.getFactFromApi()
        call.respond(catFact.toResponseDto())
    }

    private fun Route.getFactFromMemory() {
        route("/from-memory") {
            get("/fast") {
                call.respond(catFactService.getFastCatFact().toResponseDto())
            }
            get("/slow") {
                call.respond(catFactService.getSlowCatFact().toResponseDto())
            }
        }
    }

    private fun Route.getFactFromDatabase() = get("/from-db") {
        call.respond(catFactService.asyncGetFactFromDb().await().toResponseDto())
    }

    private fun Route.saveAllFactsToDatabase() = post("/save-to-db") {
        val facts = call.receive<SaveCatFactsRequestBodyListWrapper>()
        catFactService.asyncSaveFactsToDb(facts.toModels()).await()
        call.respond(HttpStatusCode.Created)
    }
}
