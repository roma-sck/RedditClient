package com.example.redditclient.mvp

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.redditclient.api.RedditApiClient
import com.example.redditclient.api.RedditPost
import com.example.redditclient.utils.Const
import kotlinx.coroutines.*

@InjectViewState
class MainPresenter: MvpPresenter<MainView>() {

    private val job = SupervisorJob()
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }
    private val scopeUi = CoroutineScope(Dispatchers.Main + job + errorHandler)

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

    fun getRedditNews() {
        scopeUi.launch {
            showLoader()

            val apiResponse = withContext(Dispatchers.IO) {
                RedditApiClient().getTopNewsAsync("", Const.LOAD_NEWS_LIMIT)
            }
            if(apiResponse.isSuccessful && apiResponse.body() != null) {
                val news = apiResponse.body()!!.data.children.map {
                    val item = it.data
                    RedditPost(
                        item.id,
                        item.title,
                        item.author,
                        item.subreddit,
                        item.created,
                        item.thumbnail,
                        item.score,
                        item.num_comments,
                        item.permalink
                    )
                }
                viewState.render(PostsViewModel(postsList = news))
            } else {
                viewState.showError(Exception())
            }

            hideLoader()
        }
    }
}