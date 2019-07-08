package com.example.redditclient

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.example.redditclient.adapters.PostAdapter
import com.example.redditclient.api.RedditPost
import kotlinx.android.synthetic.main.activity_main.*
import android.support.customtabs.CustomTabsIntent
import android.net.Uri
import android.support.v4.content.ContextCompat
import com.arellomobile.mvp.MvpAppCompatActivity
import com.example.redditclient.mvp.MainView
import com.example.redditclient.utils.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.redditclient.mvp.MainPresenter
import com.example.redditclient.mvp.PostsViewModel


class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter

    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUi()
    }

    private fun initUi() {
        adapter = PostAdapter { openPostPage(it) }
        rvRedditPosts.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        rvRedditPosts.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            mainPresenter.getRedditNews()
        }
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
        mainPresenter.getRedditNews()
    }

    private fun updatePostsList(list: List<RedditPost>) {
        val diffUtilCallback = RedditPostItemDiffUtilCallback(adapter.postsList, list)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        adapter.postsList = list
        diffResult.dispatchUpdatesTo(adapter)

        showEmpty()
    }

    private fun showEmpty() {
        tvEmpty.beVisibleIf(adapter.postsList.isEmpty())
    }

    override fun showError(throwable: Throwable) {
        showEmpty()
        val alertDialogBuilder = AlertDialog.Builder(this)

        if(!isNetworkConnected()) alertDialogBuilder.setMessage(getString(R.string.no_internet_error))
        else alertDialogBuilder.setMessage(getString(R.string.something_went_wong_error))

        alertDialogBuilder.show()
    }

    override fun showLoader() {
        progressBar.beVisible()
    }

    override fun hideLoader() {
        progressBar.beGone()
        swipeRefresh.isRefreshing = false
    }

    override fun render(viewModel: PostsViewModel) {
        updatePostsList(viewModel.postsList)
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }
}
