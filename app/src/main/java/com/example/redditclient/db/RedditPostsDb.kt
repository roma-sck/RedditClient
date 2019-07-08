package com.example.redditclient.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [RedditPostEntity::class], version = 1, exportSchema = false)
abstract class RedditPostsDb: RoomDatabase() {

    abstract fun postsDao(): RedditPostsDao

    companion object {
        var INSTANCE: RedditPostsDb? = null

        fun getAppDataBase(context: Context): RedditPostsDb? {
            if (INSTANCE == null) {
                synchronized(RedditPostsDb::class) {
                    INSTANCE =
                        Room.databaseBuilder(context.applicationContext, RedditPostsDb::class.java, "posts_db").build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}