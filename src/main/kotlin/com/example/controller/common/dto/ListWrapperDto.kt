package com.example.controller.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class ListWrapperDto<T>(val data: List<T>)
