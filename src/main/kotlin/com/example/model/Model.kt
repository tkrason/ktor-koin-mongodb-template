package com.example.model

import org.bson.types.ObjectId

interface Model {
    val id: ObjectId?
}
