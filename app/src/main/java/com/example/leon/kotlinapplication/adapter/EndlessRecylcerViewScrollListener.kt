package com.example.leon.kotlinapplication.adapter

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log


/**
 * Created by Leon on 14.06.17.
 */

abstract class EndlessRecylcerViewScrollListener(linearLayoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    var TAG = EndlessRecylcerViewScrollListener::class.java!!.getSimpleName()

    private var previousTotal = 0 // The total number of items in the dataset after the last load
    private var loading = true // True if we are still waiting for the last set of data to load.
    private val visibleThreshold = 5 // The minimum amount of items to have below your current scroll position before loading more.
    var firstVisibleItem: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0

    private var current_page = 1

    private val mLinearLayoutManager: LinearLayoutManager

    init {
        this.mLinearLayoutManager = linearLayoutManager
    }


    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView!!.childCount
        totalItemCount = mLinearLayoutManager.itemCount
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            // End has been reached

            // Do something
            current_page++
            Log.d(TAG, "End of RecyclerView reached")
            onLoadMore(current_page)

            loading = true
        }
    }

    abstract fun onLoadMore(current_page: Int)

}