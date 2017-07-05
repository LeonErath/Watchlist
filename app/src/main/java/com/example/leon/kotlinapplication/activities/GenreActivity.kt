package com.example.leon.kotlinapplication.activities

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.IdRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.charbgr.BlurNavigationDrawer.v7.BlurActionBarDrawerToggle
import com.charbgr.BlurNavigationDrawer.v7.BlurDrawerLayout
import com.example.leon.kotlinapplication.R
import io.realm.Realm

class GenreActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val rootLayout: CoordinatorLayout by bind(R.id.rootLayout)
    private val toolbar: Toolbar by bind(R.id.toolbar)
    private val drawerLayoutgesamt: BlurDrawerLayout by bind(R.id.drawer_layout)
    private val navigationView: NavigationView by bind(R.id.navView)
    private val tabLayout: TabLayout by bind(R.id.tab_layout)
    private val viewPager: ViewPager by bind(R.id.viewPager)

    lateinit var drawerToggle: BlurActionBarDrawerToggle
    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genre)


        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.personalRecom)

        setUpToolbar()
        setupDrawer()

    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        with(supportActionBar!!) {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_drawer)
            title = "Genres"
            toolbar.setTitleTextColor(Color.WHITE)
        }
    }


    fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy { findViewById(res) as T }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.top_rated -> {

            }

            R.id.watched -> {

            }
            R.id.personalRecom -> {

            }
        }

        drawerLayoutgesamt.closeDrawers()
        menuItem.isChecked = true

        return true
    }

    private fun setupDrawer() {
        drawerToggle = BlurActionBarDrawerToggle(this@GenreActivity, drawerLayoutgesamt,
                R.string.auf,
                R.string.zu)
        drawerToggle.setRadius(15)
        drawerToggle.setDownScaleFactor(6.0f)
        drawerLayoutgesamt.setDrawerListener(drawerToggle)
        drawerToggle.syncState()
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


}
