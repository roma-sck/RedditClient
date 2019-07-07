package com.example.redditclient.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditApi {

    @GET("/top.json")
    suspend fun getTopNewsAsync(@Query("after") after: String,
                                @Query("limit") limit: Int): Response<RedditApiResponse>
}