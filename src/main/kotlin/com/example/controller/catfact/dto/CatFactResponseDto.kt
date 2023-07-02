package com.example.controller.catfact.dto

import com.example.model.CatFact
import kotlinx.serialization.Serializable

@Serializable
data class CatFactResponseDto(
    val catFact: String,
)

fun CatFact.toResponseDto() = CatFactResponseDto(catFact = fact)
