package com.example.controller.catfact

import com.example.application.DatabaseUtils
import com.example.application.transactionalDelete
import com.example.application.transactionalGet
import com.example.application.transactionalPost
import com.example.controller.Controller
import com.example.controller.catfact.dto.SaveCatFactsRequestBodyListWrapper
import com.example.controller.catfact.dto.toModels
import com.example.controller.catfact.dto.toResponseDto
import com.example.controller.common.dto.toResponseDto
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
import kotlinx.coroutines.awaitAll
import org.koin.core.annotation.Singleton
import java.util.UUID

@Singleton
class CatFactController(
    private val catFactService: CatFactService,
    private val databaseUtils: DatabaseUtils,
) : Controller(basePath = "/cat-fact") {

    override fun Route.routesForRegistrationOnBasePath() {
        count()

        getFactFromApi()
        getFactFromMemory()
        getFactFromDatabaseOrFromApi()

        findCatFact()
        multipleAsyncDbRequests()

        saveAllFactsToDatabase()

        delete()
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
        val id: UUID by call.request.queryParameters
        val fact = catFactService.findFactByIdOrNull(id) ?: getFactFromApiAndSaveItToDb()

        call.respond(fact.toResponseDto())
    }

    private fun Route.saveAllFactsToDatabase() = transactionalPost(databaseUtils, "/save-to-db") {
        val facts = call.receive<SaveCatFactsRequestBodyListWrapper>()
        catFactService.insertManyFacts(facts.toModels())
        call.respond(HttpStatusCode.Created)
    }

    private fun Route.multipleAsyncDbRequests() = transactionalGet(databaseUtils, "/multiple-facts") {
        // Simulating fetching multiple sources at one
        // Result will be empty array, as we are generating random UUIDs without match chance
        // (just imagine that each call is to different table)
        val facts = (0..100).map {
            databaseUtils.executeInNewTransactionAsync { catFactService.findFactByIdOrNull(UUID.randomUUID()) }
        }.awaitAll()

        call.respond(facts.mapNotNull { it?.toResponseDto() })
    }

    private fun Route.count() = transactionalGet(databaseUtils, "/count") {
        call.respond(catFactService.count().toResponseDto())
    }

    private fun Route.delete() = transactionalDelete(databaseUtils, "/delete-where") {
        val fact: String by call.request.queryParameters

        val deletedCount = catFactService.deleteWhereCatFactMatching(CatFact(fact = fact))
        call.respond(deletedCount.toResponseDto())
    }

    private suspend fun getFactFromApiAndSaveItToDb(): CatFact {
        val factFromApi = catFactService.getFactFromApi()

        @Suppress("DeferredResultUnused") // we don't want to wait for the result
        databaseUtils.executeInNewTransactionAsync { saveFactToDb(factFromApi) }

        return factFromApi
    }

    private fun saveFactToDb(catFact: CatFact) = catFactService.insertManyFacts(listOf(catFact))
}
