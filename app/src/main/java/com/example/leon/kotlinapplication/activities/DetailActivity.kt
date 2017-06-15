package com.example.leon.kotlinapplication.activities

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.dateParser
import com.example.leon.kotlinapplication.model.Movie
import com.example.leon.kotlinapplication.moneyParser
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmQuery
import io.realm.RealmResults
import kotlin.properties.Delegates

class DetailActivity : AppCompatActivity() {


    var realm: Realm by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val textViewTitle = findViewById(R.id.textViewMovieTitle) as TextView
        val textViewOverview = findViewById(R.id.textViewMovieOverview) as TextView
        val imageView = findViewById(R.id.imageView) as ImageView
        val textViewDate = findViewById(R.id.textViewDate) as TextView
        val textViewTagline = findViewById(R.id.textViewTagline) as TextView
        val textViewScore = findViewById(R.id.textViewScore) as TextView
        val textViewRevenue = findViewById(R.id.textViewRevenue) as TextView
        val imageViewAdd = findViewById(R.id.imageViewAdd) as ImageView


        val extras: Bundle = intent.extras
        val movieid: Int = extras.getInt("movieid")


        // Initialize realm
        Realm.init(this)
        realm = Realm.getDefaultInstance()

        httpRequest(movieid)

        val movie: Movie = findMovie(movieid)

        movie.addChangeListener(RealmChangeListener {
            Log.d("DetailActivity", "MovieChangeListener Trigger")
            with(movie) {
                textViewTitle.text = title
                textViewOverview.text = overview
                textViewDate.text = dateParser(release_date).parse().dateString
                textViewTagline.text = tagline
                textViewRevenue.text = moneyParser(revenue).parse()
                textViewScore.text = popularity.toString()
            }

        })

        imageViewAdd.setColorFilter(Color.DKGRAY)

        with(movie) {
            textViewTitle.text = title
            textViewOverview.text = "Overview:\n" + overview
            textViewDate.text = dateParser(release_date).parse().dateString
            textViewTagline.text = tagline
            textViewRevenue.text = moneyParser(revenue).parse()
            textViewScore.text = popularity.toString()
        }

        val uri: Uri = Uri.parse(getString(R.string.image_base_url)
                + "/w1280"
                + movie.backdrop_path)
        Picasso.with(this).load(uri).into(imageView)

    }

    private fun httpRequest(id: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = getString(R.string.base_url) +
                "movie/$id?api_key=" +
                getString(R.string.key) +
                "&language=en-US"
        Log.d("DetailActivity", url)
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            Log.d("DetailActivity", "Respose: " + response)
            fetchRequest(response)
        }, Response.ErrorListener { error -> error.printStackTrace() })

        queue.add(stringRequest)
        queue.start()
    }


    private fun findMovie(movieid: Int): Movie {
        var query: RealmQuery<Movie> = realm.where(Movie::class.java)
        var results: RealmResults<Movie> = query.equalTo("id", movieid).findAll()
        Log.d("eventListener", " " + results.size)

        return results.first()
    }

    fun fetchRequest(response: String) {
        realm.executeTransaction {
            realm.createOrUpdateObjectFromJson(Movie::class.java, response)
        }
    }

}
