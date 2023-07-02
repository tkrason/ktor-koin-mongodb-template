package com.example.services

import com.example.models.CatFact
import kotlinx.coroutines.delay
import org.koin.core.annotation.Singleton

@Singleton
class InMemorySlowCatFactService {
    suspend fun getSlowCatFact(): CatFact {
        delay(timeMillis = 1000L)
        return CatFact("This is a slow cat fact, nonetheless they are still cool!")
    }
}
