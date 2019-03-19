package ru.wheelman.github.model.datasources.remote

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("/users")
    fun getUsers(@Query("per_page") perPage: Int): Deferred<Response<List<GithubUser>>>

    @GET("/users")
    fun getNextUsers(
        @Query("per_page") perPage: Int,
        @Query("since") since: Long
    ): Deferred<Response<List<GithubUser>>>
}