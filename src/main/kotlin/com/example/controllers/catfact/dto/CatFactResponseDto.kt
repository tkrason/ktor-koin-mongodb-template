package com.example.controllers.catfact.dto

import com.example.models.CatFact
import kotlinx.serialization.Serializable

@Serializable
data class CatFactResponseDto(
    val catFact: String,
)

fun CatFact.toResponseDto() = CatFactResponseDto(catFact = fact)
