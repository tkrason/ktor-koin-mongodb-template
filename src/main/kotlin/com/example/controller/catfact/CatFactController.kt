package com.example.controller.catfact

import com.example.controller.Controller
import com.example.controller.RestController
import com.example.controller.catfact.dto.CatFactResponseDto
import com.example.controller.catfact.dto.SaveCatFactRequestBodyListItem
import com.example.controller.catfact.dto.toDto
import com.example.controller.catfact.dto.toModel
import com.example.controller.common.dto.ListWrapperDto
import com.example.model.CatFact
import com.example.services.CatFactService
import io.ktor.server.routing.Route
import io.ktor.util.reflect.typeInfo
import org.koin.core.annotation.Singleton

@Singleton(binds = [Controller::class])
class CatFactController(
    catFactService: CatFactService,
) : RestController<CatFact, SaveCatFactRequestBodyListItem, CatFactResponseDto>(
    basePath = "api/v1",
    autoRegisterRoutes = true,
    service = catFactService,
) {
    override fun Route.additionalRoutesForRegistration() {
    }

    override fun getNameOfModelForRestPath() = "cat-fact"

    override fun requestDtoTypeInfo() = typeInfo<SaveCatFactRequestBodyListItem>()
    override fun listRequestTypeInfo() = typeInfo<ListWrapperDto<SaveCatFactRequestBodyListItem>>()

    override fun responseDtoTypeInfo() = typeInfo<CatFactResponseDto>()
    override fun listResponseDtoTypeInfo() = typeInfo<ListWrapperDto<CatFactResponseDto>>()

    override fun CatFact.toResponseDto() = toDto()
    override fun SaveCatFactRequestBodyListItem.requestToModel() = toModel()
}
