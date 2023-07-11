package com.example.services

import com.example.model.User
import com.example.repository.UserRepository
import org.koin.core.annotation.Singleton

@Singleton
class UserService(userRepository: UserRepository) : ModelService<User>(userRepository)
