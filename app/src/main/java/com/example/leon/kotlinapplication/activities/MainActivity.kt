package com.example.leon.kotlinapplication.activities

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.ViewPagerAdapter
import com.example.leon.kotlinapplication.model.Movie
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    var rootLayout: CoordinatorLayout by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.rootLayout) as CoordinatorLayout
        val viewPager = findViewById(R.id.viewPager) as ViewPager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, this)
        var tabLayout = findViewById(R.id.tab_layout) as TabLayout
        var toolbar = findViewById(R.id.toolbar) as Toolbar



        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)



        tabLayout.addTab(tabLayout.newTab().setText("Popular"))
        tabLayout.addTab(tabLayout.newTab().setText("Cinema"))
        tabLayout.addTab(tabLayout.newTab().setText("My List"))

        tabLayout.tabGravity = TabLayout.GRAVITY_FILL



        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> viewPager.setCurrentItem(0)
                    1 -> viewPager.setCurrentItem(1)
                    2 -> viewPager.setCurrentItem(2)
                }
            }

        })

        viewPager.adapter = viewPagerAdapter

    }

    fun addToFavorite(movie: Movie) {
        val snackbar = Snackbar
                .make(rootLayout, movie.title + " added to Watchlist", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    val snackbar1 = Snackbar.make(rootLayout, movie.title + " remove from Watchlist!", Snackbar.LENGTH_SHORT)
                    snackbar1.show()
                }
        snackbar.show()
    }

    fun removeFromFavorite(movie: Movie) {
        val snackbar = Snackbar
                .make(rootLayout, movie.title + " removed from Watchlist", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    val snackbar1 = Snackbar.make(rootLayout, movie.title + " added to Watchlist!", Snackbar.LENGTH_SHORT)
                    snackbar1.show()
                }
        snackbar.show()
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}

