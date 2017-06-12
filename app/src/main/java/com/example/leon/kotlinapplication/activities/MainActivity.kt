package com.example.leon.kotlinapplication.activities

import com.example.leon.kotlinapplication.model.Movie
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.leon.kotlinapplication.EventListener
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.ViewPagerAdapter
import io.realm.Realm
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private var realm: Realm by Delegates.notNull()
    private var eventListener: EventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById(R.id.button) as Button;
        val viewPager = findViewById(R.id.viewPager) as ViewPager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,this)
        viewPager.adapter = viewPagerAdapter

        Realm.init(this)
        realm = Realm.getDefaultInstance()


        btn.setOnClickListener({ view ->
                if (eventListener != null){
                    eventListener!!.updateRecyclerView()
                }
        })
    }

    fun saveMovie(movieName:String,movieYear:Int){

    }

    fun nextActivity(){
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

