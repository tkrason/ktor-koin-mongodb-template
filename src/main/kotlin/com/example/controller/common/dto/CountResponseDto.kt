package com.example.controller.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class CountResponseDto(
    val count: Long,
)

fun Int.toResponseDto() = CountResponseDto(this.toLong())

fun Long.toResponseDto() = CountResponseDto(this)
