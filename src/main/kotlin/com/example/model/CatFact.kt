package com.example.model

import org.bson.types.ObjectId

data class CatFact(
    override val id: ObjectId?,
    val fact: String,
) : Model()
