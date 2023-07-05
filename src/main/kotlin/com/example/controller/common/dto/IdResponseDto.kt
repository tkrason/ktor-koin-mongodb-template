package com.example.controller.common.dto

import kotlinx.serialization.Serializable
import org.bson.BsonObjectId

@Serializable
data class IdResponseDto(
    val id: String,
)

fun BsonObjectId.toResponseDto() = IdResponseDto(
    id = value.toHexString(),
)
