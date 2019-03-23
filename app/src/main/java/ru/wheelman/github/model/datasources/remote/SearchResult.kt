package ru.wheelman.github.model.datasources.remote

data class SearchResult constructor(val items: List<GithubUser>, val totalCount: Long)