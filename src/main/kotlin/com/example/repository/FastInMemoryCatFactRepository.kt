package com.example.repository

import com.example.model.CatFact
import org.koin.core.annotation.Singleton

@Singleton
class FastInMemoryCatFactRepository {
    fun getCatFact(): CatFact = CatFact("Cats are cool!")
}
