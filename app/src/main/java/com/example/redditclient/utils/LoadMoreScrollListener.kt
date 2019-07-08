package com.example.redditclient.utils

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class LoadMoreScrollListener(
    val onLoadMore: () -> Unit,
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    private var previousTotal = 0
    private var loading = true
    private var visibleThreshold = 2
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy > 0) {
            visibleItemCount = recyclerView.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemCount
                }
            }
            
            if(totalItemCount == Const.LOAD_NEWS_MAX_CONT) return

            if (!loading && (((firstVisibleItem + visibleThreshold) >= totalItemCount/2) && firstVisibleItem >= 0)) {
                onLoadMore()
                loading = true
            }
        }
    }
}