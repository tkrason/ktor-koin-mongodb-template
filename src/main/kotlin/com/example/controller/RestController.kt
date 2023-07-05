package com.example.controller

import com.example.controller.catfact.dto.CatFactResponseDto
import com.example.controller.catfact.dto.SaveCatFactRequestBodyListItem
import com.example.controller.catfact.dto.toDto
import com.example.controller.catfact.dto.toModel
import com.example.controller.common.dto.CountResponseDto
import com.example.controller.common.dto.ListWrapperDto
import com.example.controller.common.dto.toResponseDto
import com.example.model.CatFact
import com.example.model.Model
import com.example.services.CatFactService
import com.example.services.ModelService
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.util.getOrFail
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.toList
import org.koin.core.annotation.Singleton

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

            deleteOneById()
        }

        additionalRoutesForRegistration()
    }

    abstract fun Route.additionalRoutesForRegistration()

    abstract fun getNameOfModelForRestPath(): String

    abstract fun REQUEST_DTO.requestToModel(): MODEL
    abstract fun MODEL.toResponseDto(): RESPONSE_DTO

    abstract fun requestDtoTypeInfo(): TypeInfo
    abstract fun responseDtoTypeInfo(): TypeInfo
    abstract fun listResponseDtoTypeInfo(): TypeInfo

    private suspend fun ApplicationCall.getListRequestDto() = receive<ListWrapperDto<REQUEST_DTO>>()

    fun Route.countAll() = get("/${getNameOfModelForRestPath()}/count", {
        response { HttpStatusCode.OK to { body<CountResponseDto>() } }
    }) {
        val count = service.countAll()
        call.respond(count.toResponseDto())
    }

    fun Route.findAll() = get("/${getNameOfModelForRestPath()}/all", {
        response { HttpStatusCode.OK to { body<ListWrapperDto<RESPONSE_DTO>>() } }
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
        val model = call.receive<MODEL>(requestDtoTypeInfo())
        service.save(model)
        call.respond(HttpStatusCode.Created)
    }

    fun Route.saveMany() = post("/many-${getNameOfModelForRestPath()}", {
        request { body<ListWrapperDto<RESPONSE_DTO>>() }
        response { HttpStatusCode.Created to {} }
    }) {
        val models = call.getListRequestDto().data.map { it.requestToModel() }
        service.saveMany(models)
        call.respond(HttpStatusCode.Created)
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

@Singleton(binds = [Controller::class])
class TestController(
    catFactService: CatFactService,
) : RestController<CatFact, SaveCatFactRequestBodyListItem, CatFactResponseDto>(
    basePath = "api/v2",
    autoRegisterRoutes = true,
    service = catFactService,
) {
    override fun Route.additionalRoutesForRegistration() {
    }

    override fun getNameOfModelForRestPath() = "cat-fact"

    override fun requestDtoTypeInfo() = typeInfo<SaveCatFactRequestBodyListItem>()
    override fun responseDtoTypeInfo() = typeInfo<CatFactResponseDto>()
    override fun listResponseDtoTypeInfo() = typeInfo<ListWrapperDto<CatFactResponseDto>>()

    override fun CatFact.toResponseDto() = toDto()
    override fun SaveCatFactRequestBodyListItem.requestToModel() = toModel()
}
