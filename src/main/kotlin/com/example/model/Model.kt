package com.example.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

abstract class Model {
    @get:BsonId
    abstract val id: ObjectId?
}
