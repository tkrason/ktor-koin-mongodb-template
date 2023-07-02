package com.example.client.catfact.dto

import kotlinx.serialization.Serializable

@Serializable
data class CatFactDto(
    val fact: String,
    val length: Int,
)
