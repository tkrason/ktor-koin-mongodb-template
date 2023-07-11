package com.example.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDate

data class User(
    @BsonId override val id: ObjectId? = null,
    val username: String,
    val dateOfBirth: LocalDate,
    val articles: List<Article>,
) : Model
