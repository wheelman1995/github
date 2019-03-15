package ru.wheelman.github.model.repositories

import ru.wheelman.github.model.entities.Result

interface IGithubUsersRepo {

    suspend fun getUsers(): Result
}
