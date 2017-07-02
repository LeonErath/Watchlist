package com.example.leon.kotlinapplication.activities

import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.IdRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import com.charbgr.BlurNavigationDrawer.v7.BlurActionBarDrawerToggle
import com.charbgr.BlurNavigationDrawer.v7.BlurDrawerLayout
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.ViewPagerAdapter
import com.example.leon.kotlinapplication.model.Movie


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val TAG: String = MainActivity::class.simpleName!!

    private val rootLayout: CoordinatorLayout by bind(R.id.rootLayout)
    private val toolbar: Toolbar by bind(R.id.toolbar)
    private val drawerLayoutgesamt: BlurDrawerLayout by bind(R.id.drawer_layout)
    private val navigationView: NavigationView by bind(R.id.navView)
    private val tabLayout: TabLayout by bind(R.id.tab_layout)
    private val viewPager: ViewPager by bind(R.id.viewPager)

    private val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, this)
    lateinit var drawerToggle: BlurActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.home)

        setUpToolbar()
        setupDrawer()
        setupTablayout()
        setUpViewPager()

    }

    fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy { findViewById(res) as T }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_drawer)
        toolbar.title = "Movies"
        toolbar.setTitleTextColor(Color.WHITE)
    }

    private fun setUpViewPager() {
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 3
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.home -> {

            }

            R.id.top_rated -> {

            }

            R.id.watched -> {

            }
            R.id.personalRecom -> {
                val intent = Intent(this, Test::class.java)
                startActivity(intent)
                finish()
            }
        }

        drawerLayoutgesamt.closeDrawers()
        menuItem.isChecked = true

        return true
    }

    private fun setupTablayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Popular"))
        tabLayout.addTab(tabLayout.newTab().setText("Cinema"))
        tabLayout.addTab(tabLayout.newTab().setText("My List"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> viewPager.currentItem = 0
                    1 -> viewPager.currentItem = 1
                    2 -> viewPager.currentItem = 2
                }
            }

        })
    }

    private fun setupDrawer() {
        drawerToggle = BlurActionBarDrawerToggle(this@MainActivity, drawerLayoutgesamt,
                R.string.auf,
                R.string.zu)
        drawerToggle.setRadius(15)
        drawerToggle.setDownScaleFactor(6.0f)
        drawerLayoutgesamt.setDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    fun addToFavorite(movie: Movie) {
        val snackbar = Snackbar.make(rootLayout, movie.title + " added to Watchlist", Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    fun removeFromFavorite(movie: Movie) {
        val snackbar = Snackbar.make(rootLayout, movie.title + " removed from Watchlist", Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val searchViewMenuItem = menu!!.findItem(R.id.search_btn)
        var mSearchView = searchViewMenuItem.actionView as SearchView
        val searchImgId = resources.getIdentifier("android:id/search_button", null, null)
        val v = mSearchView.findViewById(searchImgId) as ImageView
        v.setImageResource(R.drawable.ic_search_white_24dp)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu?.findItem(R.id.search_btn)
        val searchView = searchMenuItem?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this, SearchActivity::class.java)))
        searchView.queryHint = resources.getString(R.string.search_hint)

        return true
    }


}

