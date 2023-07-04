package com.example.controller.catfact

import com.example.application.DatabaseUtils
import com.example.application.transactionalGet
import com.example.application.transactionalPost
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
import io.ktor.server.routing.route
import io.ktor.server.util.getValue
import org.koin.core.annotation.Singleton

@Singleton
class CatFactController(
    private val catFactService: CatFactService,
    private val databaseUtils: DatabaseUtils,
) : Controller(basePath = "/cat-fact") {

    override fun Route.routesForRegistrationOnBasePath() {
        getFactFromApi()
        getFactFromMemory()
        getFactFromDatabaseOrFromApi()

        findCatFact()

        saveAllFactsToDatabase()
    }

    private fun Route.getFactFromApi() = get("/from-api") {
        val catFact = getFactFromApiAndSaveItToDb()
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

    private fun Route.getFactFromDatabaseOrFromApi() = transactionalGet(databaseUtils, "/from-db") {
        val factOrNull = catFactService.findFirstFactOrNull()
        val response = factOrNull ?: getFactFromApiAndSaveItToDb()

        call.respond(response.toResponseDto())
    }

    private fun Route.findCatFact() = transactionalGet(databaseUtils, "/fact") {
        val id: Int by call.request.queryParameters
        val fact = catFactService.findFactByIdOrNull(id) ?: getFactFromApiAndSaveItToDb()

        call.respond(fact.toResponseDto())
    }

    private fun Route.saveAllFactsToDatabase() = transactionalPost(databaseUtils, "/save-to-db") {
        val facts = call.receive<SaveCatFactsRequestBodyListWrapper>()
        catFactService.insertManyFacts(facts.toModels())
        call.respond(HttpStatusCode.Created)
    }

    private suspend fun getFactFromApiAndSaveItToDb(): CatFact {
        val factFromApi = catFactService.getFactFromApi()

        @Suppress("DeferredResultUnused") // we don't want to wait for the result
        databaseUtils.executeInNewTransactionAsync { saveFactToDb(factFromApi) }

        return factFromApi
    }

    private fun saveFactToDb(catFact: CatFact) = catFactService.insertManyFacts(listOf(catFact))
}
