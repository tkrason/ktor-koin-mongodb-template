package com.example.controller.catfact

import com.example.controller.Controller
import com.example.controller.catfact.dto.SaveCatFactsRequestBodyListWrapper
import com.example.controller.catfact.dto.toModels
import com.example.controller.catfact.dto.toResponseDto
import com.example.model.CatFact
import com.example.services.CatFactService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.util.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.koin.core.annotation.Singleton

@Singleton
class CatFactController(
    private val catFactService: CatFactService,
) : Controller(basePath = "/cat-fact") {

    override fun Route.routesForRegistrationOnBasePath() {
        getFactFromApi()
        getFactFromMemory()
        getFactFromDatabaseOrFromApi()

        findCatFact()

        saveAllFactsToDatabase()
    }

    private fun Route.getFactFromApi() = get("/from-api") {
        val catFact = getFactFromApiAndSaveItToDb(coroutineScope = this)
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

    private fun Route.getFactFromDatabaseOrFromApi() = get("/from-db") {
        val factOrNull = catFactService.findFirstFactOrNullAsync().await()
        val response = factOrNull?.toResponseDto() ?: getFactFromApiAndSaveItToDb(coroutineScope = this).toResponseDto()
        call.respond(response)
    }

    private fun Route.findCatFact() = get("/fact") {
        val id: Int by call.request.queryParameters
        supervisorScope {
            val fact = catFactService
                .findFactByIdOrNullAsync(id)
                .await()
                ?: getFactFromApiAndSaveItToDb(coroutineScope = this)

            call.respond(fact.toResponseDto())
        }
    }

    private fun Route.saveAllFactsToDatabase() = post("/save-to-db") {
        val facts = call.receive<SaveCatFactsRequestBodyListWrapper>()
        catFactService.insertManyFactsAsync(facts.toModels()).await()
        call.respond(HttpStatusCode.Created)
    }

    // Get fact from API and then save to DB. Save to DB is not blocking function and is done in background,
    // so we can respond to request asap (we don't care if saving to DB will fail)
    private suspend fun getFactFromApiAndSaveItToDb(coroutineScope: CoroutineScope): CatFact {
        val factFromApi = catFactService.getFactFromApi()
        coroutineScope.launch {
            // error("If this error and delay bellow is enabled, and we don't use supervisor job the request newer finishes")
            // This launch function would error, propagating exception and thus .respond() wouldn't be called
            saveFactToDb(factFromApi)
        }
        // delay(500)
        return factFromApi
    }

    private suspend fun saveFactToDb(catFact: CatFact) = catFactService.insertManyFactsAsync(listOf(catFact)).await()
}
