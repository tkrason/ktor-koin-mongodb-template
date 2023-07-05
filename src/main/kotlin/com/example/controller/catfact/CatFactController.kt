package com.example.controller.catfact

import com.example.controller.Controller
import com.example.controller.catfact.dto.SaveCatFactsRequestBodyListWrapper
import com.example.controller.catfact.dto.toDto
import com.example.controller.catfact.dto.toModels
import com.example.controller.common.dto.toResponseDto
import com.example.model.CatFact
import com.example.services.CatFactService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.util.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import org.koin.core.annotation.Singleton

@Singleton
class CatFactController(
    private val catFactService: CatFactService,
) : Controller(basePath = "/cat-fact") {

    override fun Route.routesForRegistrationOnBasePath() {
        count()

        getFactFromApi()
        getFactFromDatabaseOrFromApi()

        findCatFactOrGetOneFromApi()
        multipleAsyncDbRequests()

        saveAllFactsToDatabase()

        delete()
    }

    private fun Route.getFactFromApi() = get("/from-api") {
        val catFact = getFactFromApiAndSaveItToDb()
        call.respond(catFact.toDto())
    }

    private fun Route.getFactFromDatabaseOrFromApi() = get("/from-db") {
        val factOrNull = catFactService.findFirstFactOrNull()
        val response = factOrNull ?: getFactFromApiAndSaveItToDb()

        call.respond(response.toDto())
    }

    private fun Route.findCatFactOrGetOneFromApi() = get("/fact") {
        val id: String by call.request.queryParameters

        val fact = catFactService.findModelByIdOrNull(id) ?: getFactFromApiAndSaveItToDb()

        call.respond(fact.toDto())
    }

    private fun Route.saveAllFactsToDatabase() = post("/save-to-db") {
        val facts = call.receive<SaveCatFactsRequestBodyListWrapper>()
        catFactService.saveMany(facts.toModels())
        call.respond(HttpStatusCode.Created)
    }

    private fun Route.multipleAsyncDbRequests() = get("/multiple-facts") {
        // Simulating fetching multiple sources at one
        // Result will be empty array, as we are generating random ObjectId's without match chance
        // (just imagine that each call is to different table)
        val facts = (0..100).map {
            async(Dispatchers.IO) { catFactService.findModelByIdOrNull(ObjectId.get().toHexString()) }
        }.awaitAll()

        call.respond(facts.mapNotNull { it?.toDto() })
    }

    private fun Route.count() = get("/count") {
        call.respond(catFactService.countAll().toResponseDto())
    }

    private fun Route.delete() = delete("/delete-where") {
        val fact: String by call.request.queryParameters

        val deletedCount = catFactService.deleteWhereCatFactMatching(fact)
        call.respond(deletedCount.toResponseDto())
    }

    private suspend fun getFactFromApiAndSaveItToDb(): CatFact {
        val factFromApi = catFactService.getFactFromApi()
        withContext(Dispatchers.IO) { launch { saveFactToDb(factFromApi) } }
        return factFromApi
    }

    private suspend fun saveFactToDb(catFact: CatFact) = catFactService.save(catFact)
}
