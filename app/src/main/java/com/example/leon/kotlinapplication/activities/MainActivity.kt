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
        val edMovieName = findViewById(R.id.editMovieName) as EditText
        val edYearName = findViewById(R.id.editMovieYear) as EditText
        val viewPager = findViewById(R.id.viewPager) as ViewPager
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,this)
        viewPager.adapter = viewPagerAdapter

        Realm.init(this)
        realm = Realm.getDefaultInstance()


        btn.setOnClickListener({ view ->
            if (edMovieName.text.toString().trim() != "" && edYearName.text.toString().trim() != ""){
                saveMovie(edMovieName.text.toString(),edYearName.text.toString().toInt())

                edMovieName.setText("")
                edYearName.setText("")

                if (eventListener != null){
                    eventListener!!.updateRecyclerView()
                }

            }else{
                Log.d("MainActivity","Please type in a movie name and the year it was created.")
            }
        })
    }

    fun saveMovie(movieName:String,movieYear:Int){
        realm.executeTransaction{
            var movie = realm.createObject(Movie::class.java)
            movie.name = movieName
            movie.year = movieYear

        }
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

