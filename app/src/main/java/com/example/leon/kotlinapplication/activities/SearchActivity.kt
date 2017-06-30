package com.example.leon.kotlinapplication.activities

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.leon.kotlinapplication.R


class SearchActivity : AppCompatActivity() {
    val TAG = SearchActivity::class.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            Log.i(TAG, "Search for: " + query);

        }
    }
}
