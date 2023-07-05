package com.example.controller.catfact.dto

import com.example.model.CatFact
import kotlinx.serialization.Serializable

@Serializable
data class CatFactResponseDto(
    val id: String?,
    val catFact: String,
)

fun CatFact.toDto() = CatFactResponseDto(id = id?.toHexString(), catFact = fact)
