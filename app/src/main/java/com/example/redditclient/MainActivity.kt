package com.example.redditclient

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.example.redditclient.adapters.PostAdapter
import com.example.redditclient.api.RedditApiClient
import com.example.redditclient.api.RedditPost
import com.example.redditclient.utils.Const
import com.example.redditclient.utils.RedditPostItemDiffUtilCallback
import com.example.redditclient.utils.beGone
import com.example.redditclient.utils.beVisible
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import android.support.customtabs.CustomTabsIntent
import android.net.Uri
import android.support.v4.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: PostAdapter

    private val job = SupervisorJob()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    private val scopeUi = CoroutineScope(Dispatchers.Main + job + errorHandler)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUi()
    }

    private fun initUi() {
        adapter = PostAdapter { openPostPage(it) }
        rvRedditPosts.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rvRedditPosts.adapter = adapter
    }

    private fun openPostPage(item: RedditPost?) {
        item?.permalink?.let { link ->
            val builder = CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setShowTitle(true)
                .setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
                .setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
            val customTabsIntent = builder.build()
            try {
                customTabsIntent.launchUrl(this, Uri.parse(Const.REDDIT_BASE_URL + link))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getRedditNews()
    }

    override fun onStop() {
        super.onStop()
        cancelAllWork()
    }

    private fun cancelAllWork() {
        scopeUi.coroutineContext.cancelChildren()
    }

    private fun getRedditNews() {
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
                updateUi(news)
            } else {
                showError(Exception())
            }

            hideLoader()
        }
    }

    private fun updateUi(postsList: List<RedditPost>) {
        updatePostsList(postsList)
    }

    private fun updatePostsList(list: List<RedditPost>) {
        val diffUtilCallback = RedditPostItemDiffUtilCallback(adapter.postsList, list)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        adapter.postsList = list
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun showLoader() {
        progressBar.beVisible()
    }

    private fun hideLoader() {
        progressBar.beGone()
    }

    private fun handleError(throwable: Throwable) {
        hideLoader()
        showError(throwable)
    }

    private fun showError(throwable: Throwable) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        if(!isNetworkConnected()) alertDialogBuilder.setMessage(getString(R.string.no_internet_error))
        else alertDialogBuilder.setMessage(getString(R.string.something_went_wong_error))

        alertDialogBuilder.show()
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }
}
