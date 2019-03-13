package ru.wheelman.github.model.repositories

import ru.wheelman.github.model.entities.User

interface IGithubUsersRepo {

    suspend fun getUsers(): List<User>
}
