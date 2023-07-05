package com.example.controller

import com.example.controller.common.dto.CountResponseDto
import com.example.controller.common.dto.ListWrapperDto
import com.example.controller.common.dto.toResponseDto
import com.example.model.Model
import com.example.services.ModelService
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.put
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.util.getOrFail
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.flow.toList
import org.bson.BsonObjectId

abstract class RestController<MODEL : Model, REQUEST_DTO : Any, RESPONSE_DTO : Any>(
    basePath: String,
    private val autoRegisterRoutes: Boolean = false,
    private val service: ModelService<MODEL>,
) : Controller(basePath) {

    final override fun Route.routesForRegistrationOnBasePath() {
        if (autoRegisterRoutes) {
            countAll()

            findAll()
            findFirstByIdOrNull()

            saveOne()
            saveMany()

            updateOne()

            deleteOneById()
        }

        additionalRoutesForRegistration()
    }

    abstract fun Route.additionalRoutesForRegistration()

    abstract fun getNameOfModelForRestPath(): String

    abstract fun REQUEST_DTO.requestToModel(): MODEL
    abstract fun MODEL.toResponseDto(): RESPONSE_DTO

    abstract fun requestDtoTypeInfo(): TypeInfo
    abstract fun listRequestTypeInfo(): TypeInfo

    abstract fun responseDtoTypeInfo(): TypeInfo
    abstract fun listResponseDtoTypeInfo(): TypeInfo

    fun Route.countAll() = get("/${getNameOfModelForRestPath()}/count", {
        response { HttpStatusCode.OK to { body<CountResponseDto>() } }
    }) {
        val count = service.countAll()
        call.respond(count.toResponseDto())
    }

    fun Route.findAll() = get("/${getNameOfModelForRestPath()}/all", {
        response { HttpStatusCode.OK to { body(listResponseDtoTypeInfo().type) } }
    }) {
        val allModels = service.findAll().toList()
        val response = ListWrapperDto(allModels.map { it.toResponseDto() })
        call.respond(response, listResponseDtoTypeInfo())
    }

    fun Route.findFirstByIdOrNull() = get("/${getNameOfModelForRestPath()}/{id}", {
        request { pathParameter<String>("id") { this.description = "MongoDB ObjectId" } }
        response {
            HttpStatusCode.NotFound to { }
            HttpStatusCode.OK to { body(responseDtoTypeInfo().type) }
        }
    }) {
        val id = call.parameters.getOrFail("id")

        when (val model = service.findModelByIdOrNull(id)) {
            null -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(model.toResponseDto(), responseDtoTypeInfo())
        }
    }

    fun Route.saveOne() = post("/${getNameOfModelForRestPath()}", {
        request { body(requestDtoTypeInfo().type) }
        response { HttpStatusCode.Created to {} }
    }) {
        val model = call.receive<REQUEST_DTO>(requestDtoTypeInfo()).requestToModel()

        when (val createdId = service.save(model)) {
            null -> call.respond(HttpStatusCode.InternalServerError)
            else -> call.respond(HttpStatusCode.Created, (createdId as BsonObjectId).toResponseDto())
        }
    }

    fun Route.saveMany() = post("/many-${getNameOfModelForRestPath()}", {
        request { body(listRequestTypeInfo().type) }
        response { HttpStatusCode.Created to {} }
    }) {
        val models = call.receive<ListWrapperDto<REQUEST_DTO>>(listRequestTypeInfo()).data.map { it.requestToModel() }
        val createdIds = service.saveMany(models)
        val response = ListWrapperDto(createdIds.map { (it as BsonObjectId).toResponseDto() })
        call.respond(HttpStatusCode.Created, response)
    }

    fun Route.updateOne() = put("/${getNameOfModelForRestPath()}", {
        request { body(requestDtoTypeInfo().type) }
        response { HttpStatusCode.Accepted to {} }
    }) {
        val model = call.receive<REQUEST_DTO>(requestDtoTypeInfo()).requestToModel()
        service.updateOne(model)
        call.respond(HttpStatusCode.Accepted)
    }

    fun Route.deleteOneById() = delete("/${getNameOfModelForRestPath()}", {
        request { queryParameter<String>("id") }
        response { HttpStatusCode.OK to { body<CountResponseDto>() } }
    }) {
        val id = call.parameters.getOrFail("id")
        val count = service.deleteOneById(id)
        call.respond(count.toResponseDto())
    }
}
