package com.example.redditclient.mvp

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.redditclient.RedditApp
import com.example.redditclient.adapters.PostAdapterItem
import com.example.redditclient.api.RedditApiClient
import com.example.redditclient.db.RedditPostEntity
import com.example.redditclient.db.RedditPostsDb
import com.example.redditclient.api.Const
import kotlinx.coroutines.*

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    private val job = SupervisorJob()
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }
    private val scopeUi = CoroutineScope(Dispatchers.Main + job + errorHandler)

    private var after = ""

    override fun destroyView(view: MainView) {
        super.destroyView(view)
        cancelAllWork()
    }

    private fun handleError(throwable: Throwable) {
        hideLoader()
        viewState.showError(throwable)
    }

    private fun cancelAllWork() {
        scopeUi.coroutineContext.cancelChildren()
    }

    private fun showLoader() {
        viewState.showLoader()
    }

    private fun hideLoader() {
        viewState.hideLoader()
    }

    fun getRedditNews(isInitialLoading: Boolean) {
        if (isInitialLoading) after = ""
        scopeUi.launch {
            showLoader()

            val apiResponse = withContext(Dispatchers.IO) {
                RedditApiClient().getTopNewsAsync(after, Const.LOAD_NEWS_LIMIT)
            }
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                after = apiResponse.body()!!.data.after.orEmpty()
                val posts = apiResponse.body()!!.data.children.map {
                    it.data
                }

                val postsFromDb = withContext(Dispatchers.IO) {
                    val db = RedditPostsDb.getAppDataBase(RedditApp.instance.applicationContext)
                    if (isInitialLoading) db?.clearAllTables()
                    posts.let { postsList ->
                        postsList.forEach { post ->
                            db?.postsDao()?.insert(
                                RedditPostEntity(
                                    post.id,
                                    post.title,
                                    post.author,
                                    post.subreddit,
                                    post.created,
                                    post.thumbnail,
                                    post.score,
                                    post.num_comments,
                                    post.permalink
                                )
                            )
                        }
                    }

                    db?.postsDao()?.getAllPosts()
                }

                val adapterItems = mutableListOf<PostAdapterItem>()
                postsFromDb?.forEach {
                    adapterItems.add(
                        PostAdapterItem(
                            it.id,
                            it.title,
                            it.author,
                            it.subreddit,
                            it.created,
                            it.thumbnail,
                            it.score,
                            it.num_comments,
                            it.permalink
                        )
                    )
                }

                viewState.render(PostsViewModel(postsList = adapterItems))
            } else {
                viewState.showError(Exception())
            }

            hideLoader()
        }
    }
}