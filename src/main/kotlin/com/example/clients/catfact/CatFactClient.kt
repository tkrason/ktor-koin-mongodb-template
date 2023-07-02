package com.example.clients.catfact

import com.example.application.config.Config
import com.example.clients.catfact.dto.CatFactDto
import com.example.models.CatFact
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.annotation.Singleton

@Singleton
class CatFactClient(
    private val config: Config,
) {

    private val client = HttpClient {
        install(ContentNegotiation) { json() }
        defaultRequest {
            url(config.catFactConfig.baseUrl)
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getCatFact(): CatFact = client
        .get("/fact")
        .body<CatFactDto>()
        .let { CatFact(fact = it.fact) }
}
