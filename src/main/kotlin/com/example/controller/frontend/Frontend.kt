package com.example.controller.frontend

import com.example.controller.Controller
import com.example.services.CatFactService
import io.ktor.server.application.call
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.coroutines.flow.toList
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h4
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.tr
import org.koin.core.annotation.Singleton

@Singleton
class Frontend(
    private val catFactService: CatFactService,
) : Controller(basePath = "/ktor-sample", useBearerAuth = false) {
    override fun Route.routesForRegistrationOnBasePath() {
        basePage()
    }

    private fun Route.basePage() = get {
        val facts = catFactService.findAll().toList()
        call.respondHtml {
            body {
                div {
                    h1 {
                        text("Here we have all the cat facts from our DB!")
                    }
                    h4 { text("Probably using React / Vue would be easier...") }
                    table {
                        tbody {
                            facts.onEach {
                                tr {
                                    td {
                                        text(it.id?.toHexString() ?: "null")
                                    }
                                    td {
                                        text(it.fact)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
