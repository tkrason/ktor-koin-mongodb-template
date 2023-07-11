package com.example.repository

import com.example.application.Mongo
import com.example.model.User
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.koin.core.annotation.Singleton

@Singleton
class UserRepository(mongo: Mongo) : MongoCrudRepository<User>(mongo, "ktor-sample") {
    override fun MongoDatabase.selectRepositoryCollection(): MongoCollection<User> {
        return getCollection<User>("user")
    }
}
