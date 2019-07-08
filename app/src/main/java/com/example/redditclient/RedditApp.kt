package com.example.redditclient

import android.app.Application

class RedditApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RedditApp
            private set
    }
}