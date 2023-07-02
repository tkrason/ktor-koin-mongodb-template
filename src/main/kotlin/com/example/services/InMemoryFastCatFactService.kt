package com.example.services

import com.example.models.CatFact
import org.koin.core.annotation.Singleton

@Singleton
class InMemoryFastCatFactService {
    fun getFastCatFact(): CatFact = CatFact("Cats are cool!")
}
