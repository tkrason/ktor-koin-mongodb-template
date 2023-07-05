package com.example.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

open class Model(
    @BsonId val id: ObjectId?,
)
