package ru.wheelman.github.model.datasources.remote

data class GithubUser(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val score: Float,
    val htmlUrl: String
)