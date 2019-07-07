package com.example.redditclient.api

data class RedditApiResponse(val data: RedditDataResponse)

data class RedditDataResponse(
    val children: List<RedditChildrenResponse>,
    val after: String?,
    val before: String?
)

data class RedditChildrenResponse(val data: RedditPost)

data class RedditPost(
    val id: String?,
    val title: String?,
    val author: String?,
    val subreddit: String?,
    val created: Long?,
    val thumbnail: String?,
    val score: Int?,
    val num_comments: Int?,
    val permalink: String?
)