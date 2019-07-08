package com.example.redditclient.adapters

data class PostAdapterItem(
    val id: String,
    val title: String?,
    val author: String?,
    val subreddit: String?,
    val created: Long?,
    val thumbnail: String?,
    val score: Int?,
    val num_comments: Int?,
    val permalink: String?
)