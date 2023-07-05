package com.example.controller.catfact.dto

import com.example.model.CatFact
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class SaveCatFactsRequestBodyListWrapper(
    val data: List<SaveCatFactRequestBodyListItem>,
)

@Serializable
data class SaveCatFactRequestBodyListItem(
    val fact: String,
)

fun SaveCatFactsRequestBodyListWrapper.toModels() = data.map { it.toModel() }
fun SaveCatFactRequestBodyListItem.toModel() = CatFact(id = ObjectId.get(), fact = fact)
