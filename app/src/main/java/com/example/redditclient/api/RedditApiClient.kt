package com.example.redditclient.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import java.util.concurrent.TimeUnit

class RedditApiClient {
    private val redditApi: RedditApi

    init {
        val okHttpClient = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Const.REDDIT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        redditApi = retrofit.create(RedditApi::class.java)
    }

    suspend fun getTopNewsAsync(after: String, limit: Int): Response<RedditApiResponse> {
        return redditApi.getTopNewsAsync(after, limit)
    }
}