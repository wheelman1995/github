package ru.wheelman.github.model.datasources.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("/users")
    fun getUsers(@Query("per_page") perPage: Int): Call<List<GithubUser>>

    @GET("/users")
    fun getNextUsers(
        @Query("per_page") perPage: Int,
        @Query("since") since: Long
    ): Call<List<GithubUser>>
}