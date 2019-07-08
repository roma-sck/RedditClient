package com.example.redditclient.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "redditPosts")
data class RedditPostEntity(
    @PrimaryKey
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