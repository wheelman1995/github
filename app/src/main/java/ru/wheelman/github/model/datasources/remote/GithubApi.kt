package ru.wheelman.github.model.datasources.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("/users")
    fun getUsers(@Query("per_page") number: Int = 20): Call<List<GithubUser>>
}