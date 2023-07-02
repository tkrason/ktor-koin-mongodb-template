package com.example.repository

import com.example.model.CatFact
import kotlinx.coroutines.delay
import org.koin.core.annotation.Singleton

@Singleton
class SlowInMemoryCatFactRepository {
    suspend fun getCatFact(): CatFact {
        delay(timeMillis = 1000L)
        return CatFact("This is a slow cat fact, nonetheless they are still cool!")
    }
}
