package com.example.leon.kotlinapplication.activities

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.model.Movie
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import io.realm.internal.IOException
import java.io.FileNotFoundException
import kotlin.properties.Delegates

class DetailActivity : AppCompatActivity() {


    var realm: Realm by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var textViewTitle = findViewById(R.id.textViewMovieTitle) as TextView
        var textViewOverview = findViewById(R.id.textViewMovieOverview) as TextView
        var imageView = findViewById(R.id.imageView) as ImageView
        var textViewDate = findViewById(R.id.textViewDate) as TextView


        val extras: Bundle = intent.extras
        var movieid: Int = extras.getInt("movieid")


            // Initialize realm
            Realm.init(this)
            realm = Realm.getDefaultInstance()

        httpRequest()

        var movie: Movie = findMovie(movieid)

            movie.addChangeListener(RealmChangeListener {
                Log.d("DetailActivity","MovieChangeListener Trigger")
                textViewTitle.text = movie.title
                textViewOverview.text = movie.overview
                textViewDate.text = movie.release_date
            })

            textViewTitle.text = movie.title
            textViewOverview.text = movie.overview
            textViewDate.text = movie.release_date

            var uri: Uri = Uri.parse(getString(R.string.image_base_url)
                    + "/w1280"
                    + movie.backdrop_path)
            Picasso.with(this).load(uri).into(imageView)

    }

    private fun httpRequest() {
        val queue = Volley.newRequestQueue(this)
        val url = getString(R.string.base_url) +
                "movie/popular?api_key=" +
                getString(R.string.key) +
                "&language=en-US&page=1"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
            override fun onResponse(response: String) {
                // Display the first 500 characters of the response string.
                Log.d("Resonse", response)
                fetchRequest(response)
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                error.printStackTrace()
            }
        })

        queue.add(stringRequest);
    }


    private fun findMovie(movieid: Int): Movie {
        var query: RealmQuery<Movie> = realm.where(Movie::class.java)
        var results: RealmResults<Movie> = query.equalTo("id", movieid).findAll()
        Log.d("eventListener", " " + results.size)

        return results.first()
    }

    fun fetchRequest(reponse: String) {
        realm.executeTransactionAsync(Realm.Transaction() {
            @Override
            fun execute(bgRealm: Realm) {
                try {
                    //val input: InputStream = assets.open("response.json")
                    bgRealm.createOrUpdateObjectFromJson(Movie::class.java, reponse)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                } catch (e: RealmPrimaryKeyConstraintException) {
                    e.printStackTrace()
                }
            }
        }, Realm.Transaction.OnSuccess() {
            @Override
            fun onSuccess() {
                // Transaction was a success.
                Log.d("DetailActivity", "Update of RealmObject was successfull.")
            }
        }, Realm.Transaction.OnError() {
            @Override
            fun onError(error: Throwable) {
                // Transaction failed and was automatically canceled.
                error.printStackTrace()
            }
        });

    }

}
