package com.example.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class CatFact(
    @BsonId val id: ObjectId? = null,
    val fact: String,
)
