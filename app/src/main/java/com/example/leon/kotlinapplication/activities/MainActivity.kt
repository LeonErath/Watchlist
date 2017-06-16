package com.example.leon.kotlinapplication.activities

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.example.leon.kotlinapplication.DataModel
import com.example.leon.kotlinapplication.DrawerItemCustomAdapter
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.ViewPagerAdapter
import com.example.leon.kotlinapplication.model.Movie
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    var rootLayout: CoordinatorLayout by Delegates.notNull()
    private var mNavigationDrawerItemTitles: Array<String>? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerList: ListView? = null
    var mDrawerToggle: android.support.v7.app.ActionBarDrawerToggle? = null
    private val mDrawerTitle: CharSequence? = null
    var toolbar: Toolbar by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.rootLayout) as CoordinatorLayout
        val viewPager = findViewById(R.id.viewPager) as ViewPager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, this)
        var tabLayout = findViewById(R.id.tab_layout) as TabLayout
        toolbar = findViewById(R.id.toolbar) as Toolbar
        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.categories)
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        mDrawerList = findViewById(R.id.left_drawer) as ListView


        setSupportActionBar(toolbar)
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
                    0 -> viewPager.setCurrentItem(0)
                    1 -> viewPager.setCurrentItem(1)
                    2 -> viewPager.setCurrentItem(2)
                }
            }

        })

        viewPager.adapter = viewPagerAdapter

        val drawerItem = arrayOfNulls<DataModel>(3)

        drawerItem[0] = DataModel(R.drawable.ic_favorite_black_24dp, "Discover")
        drawerItem[1] = DataModel(R.drawable.ic_favorite_black_24dp, "Search")
        drawerItem[2] = DataModel(R.drawable.ic_favorite_black_24dp, "Top Rated")
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(true)

        val adapter = DrawerItemCustomAdapter(this, R.layout.drawer_list_item, drawerItem)
        mDrawerList!!.adapter = adapter
        mDrawerList!!.setOnItemClickListener(DrawerItemClickListener())
        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        mDrawerLayout!!.setDrawerListener(mDrawerToggle)
        setupDrawerToggle()


    }

    fun setupDrawerToggle() {
        mDrawerToggle = android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name)
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle!!.syncState()
    }

    private inner class DrawerItemClickListener : AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            selectItem(position)
        }

    }

    private fun selectItem(position: Int) {


    }


    fun addToFavorite(movie: Movie) {
        val snackbar = Snackbar
                .make(rootLayout, movie.title + " added to Watchlist", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    //TODO implement UNDO Functio
                    val snackbar1 = Snackbar.make(rootLayout, movie.title + " remove from Watchlist!", Snackbar.LENGTH_SHORT)
                    snackbar1.show()
                }
        snackbar.show()
    }

    fun removeFromFavorite(movie: Movie) {
        val snackbar = Snackbar
                .make(rootLayout, movie.title + " removed from Watchlist", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    //TODO implement UNDO Functio
                    val snackbar1 = Snackbar.make(rootLayout, movie.title + " added to Watchlist!", Snackbar.LENGTH_SHORT)
                    snackbar1.show()
                }
        snackbar.show()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        mDrawerToggle!!.syncState();
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

