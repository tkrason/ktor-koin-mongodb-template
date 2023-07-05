package com.example.model

import org.bson.types.ObjectId

data class CatFact(
    val objectId: ObjectId? = null,
    val fact: String,
) : Model(objectId)
