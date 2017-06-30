package com.example.leon.kotlinapplication.activities

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.ViewPagerAdapter
import com.example.leon.kotlinapplication.model.List
import com.example.leon.kotlinapplication.model.Movie
import io.realm.Realm
import io.realm.RealmResults
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    val TAG: String = MainActivity::class.simpleName!!

    var rootLayout: CoordinatorLayout by Delegates.notNull()
    var toolbar: Toolbar by Delegates.notNull()
    lateinit var drawerLayoutgesamt: DrawerLayout
    lateinit var drawerToggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    lateinit var realm: Realm


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Realm.init(applicationContext)
        realm = Realm.getDefaultInstance()

        rootLayout = findViewById(R.id.rootLayout) as CoordinatorLayout
        val viewPager = findViewById(R.id.viewPager) as ViewPager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, this)
        val tabLayout = findViewById(R.id.tab_layout) as TabLayout
        toolbar = findViewById(R.id.toolbar) as Toolbar
        drawerLayoutgesamt = findViewById(R.id.drawer_layout) as DrawerLayout
        drawerToggle = ActionBarDrawerToggle(this@MainActivity, drawerLayoutgesamt, R.string.auf, R.string.zu)
        drawerLayoutgesamt.setDrawerListener(drawerToggle)

        navigationView = findViewById(R.id.navView) as NavigationView

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {

                }

                R.id.top_rated -> {

                }

                R.id.recommendation -> {

                }
            }

            drawerLayoutgesamt.closeDrawers()
            menuItem.isChecked = true

            false
        }
        navigationView.setCheckedItem(0)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_drawer)
        drawerToggle.syncState()


        toolbar.title = "Movies"
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
                    0 -> viewPager.currentItem = 0
                    1 -> viewPager.currentItem = 1
                    2 -> viewPager.currentItem = 2
                }
            }

        })


        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 3


    }





    fun addToFavorite(movie: Movie) {
        val snackbar = Snackbar
                .make(rootLayout, movie.title + " added to Watchlist", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    realm.executeTransaction {
                        val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2).findAll()
                        if (results.size > 0) {
                            Log.d("MovieFlatBinder", "MyList is not empty -> updates List")
                            val List = results[0]
                            List.results.remove(movie)
                            List.total_results--
                        }
                    }
                    val snackbar1 = Snackbar.make(rootLayout, movie.title + " remove from Watchlist!", Snackbar.LENGTH_SHORT)
                    snackbar1.show()
                }
        snackbar.show()
    }

    fun removeFromFavorite(movie: Movie) {
        val snackbar = Snackbar
                .make(rootLayout, movie.title + " removed from Watchlist", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    realm.executeTransaction {
                        val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2).findAll()
                        if (results.size > 0) {
                            Log.i(TAG, "MyList is not empty -> updates List")
                            val List = results[0]
                            val check = List.results.any { movie!!.id == it.id }

                            if (!check) {
                                List.results.add(movie)
                                List.total_results++
                            }

                        } else {
                            Log.i(TAG, "MyList is empty -> creates new List")
                            val List = List()
                            List.id = 2
                            List.name = "MyList"
                            List.results.add(movie)
                            List.total_results++
                            realm.copyToRealmOrUpdate(List)
                        }
                    }
                    val snackbar1 = Snackbar.make(rootLayout, movie.title + " added to Watchlist!", Snackbar.LENGTH_SHORT)
                    snackbar1.show()
                }
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

    override fun onDestroy() {
        super.onDestroy()
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

