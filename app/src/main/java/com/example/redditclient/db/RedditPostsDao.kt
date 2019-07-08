package com.example.redditclient.db

import android.arch.persistence.room.*


@Dao
interface RedditPostsDao {

    @Query("SELECT * FROM redditPosts")
    fun getAllPosts(): List<RedditPostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: RedditPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<RedditPostEntity>)

    @Delete
    fun delete(post: RedditPostEntity)
}