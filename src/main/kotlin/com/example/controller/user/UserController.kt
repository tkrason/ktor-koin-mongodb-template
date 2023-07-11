package com.example.controller.user

import com.example.controller.Controller
import com.example.controller.RestController
import com.example.controller.common.dto.ListWrapperDto
import com.example.controller.user.dto.UserDto
import com.example.controller.user.dto.toDto
import com.example.controller.user.dto.toModel
import com.example.model.User
import com.example.services.UserService
import io.ktor.server.routing.Route
import io.ktor.util.reflect.typeInfo
import org.koin.core.annotation.Singleton

@Singleton(binds = [Controller::class])
class UserController(
    userService: UserService,
) : RestController<User, UserDto, UserDto>(
    basePath = "api/v2",
    autoRegisterRoutes = true,
    service = userService,
) {
    override fun Route.additionalRoutesForRegistration() {}

    override fun getNameOfModelForRestPath() = "user"
    override fun User.toResponseDto(): UserDto = toDto()

    override fun UserDto.requestToModel() = toModel()

    override fun requestDtoTypeInfo() = typeInfo<UserDto>()

    override fun listRequestTypeInfo() = typeInfo<ListWrapperDto<UserDto>>()

    override fun responseDtoTypeInfo() = requestDtoTypeInfo()

    override fun listResponseDtoTypeInfo() = listRequestTypeInfo()
}
