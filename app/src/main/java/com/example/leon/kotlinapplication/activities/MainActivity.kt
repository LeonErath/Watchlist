package com.example.leon.kotlinapplication.activities

import com.example.leon.kotlinapplication.model.Movie
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TabHost
import com.example.leon.kotlinapplication.EventListener
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.ViewPagerAdapter
import io.realm.Realm
import kotlin.properties.Delegates
import com.example.leon.kotlinapplication.R.id.viewPager



class MainActivity : AppCompatActivity() {

    private var realm: Realm by Delegates.notNull()
    private var eventListener: EventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById(R.id.button) as FloatingActionButton;
        val viewPager = findViewById(R.id.viewPager) as ViewPager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, this)
        var tabLayout = findViewById(R.id.tab_layout) as TabLayout

        tabLayout.addTab(tabLayout.newTab().setText("Popular"));
        tabLayout.addTab(tabLayout.newTab().setText("Cinema"));
        tabLayout.addTab(tabLayout.newTab().setText("My List"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

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

        supportActionBar?.title = "Movies"

        Realm.init(this)
        realm = Realm.getDefaultInstance()


        btn.setOnClickListener({
            if (eventListener != null) {
                eventListener!!.updateRecyclerView()
            }
        })
    }


    fun nextActivity() {
        //val intent = Intent(this, MenuActivity::class.java)
        //startActivity(intent)
    }

    fun setOnEventListener(listener: EventListener) {
        this.eventListener = listener
    }


    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

