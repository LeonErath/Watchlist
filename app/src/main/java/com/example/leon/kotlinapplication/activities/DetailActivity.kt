package com.example.leon.kotlinapplication.activities

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.CastAdapter
import com.example.leon.kotlinapplication.adapter.QueryAdapter
import com.example.leon.kotlinapplication.dateParser
import com.example.leon.kotlinapplication.model.Movie
import com.example.leon.kotlinapplication.moneyParser
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmChangeListener
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
        val recylcerViewCast = findViewById(R.id.recyclerViewCast) as RecyclerView

        // Set up recycler view
        var adapter = CastAdapter(this)
        recylcerViewCast.adapter = adapter
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 300
        itemAnimator.removeDuration = 300
        recylcerViewCast.itemAnimator = itemAnimator
        val layout = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recylcerViewCast.layoutManager = layout


        val extras: Bundle = intent.extras
        val movieid: Int = extras.getInt("movieid")


        // Initialize realm
        Realm.init(this)
        realm = Realm.getDefaultInstance()

        val queryAdapter = QueryAdapter(this)
        val movie: Movie = queryAdapter.getDetail(movieid)
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
            adapter.addData(movie.cast)

        })
        adapter.addData(movie.cast)




        imageViewAdd.setColorFilter(Color.GRAY)

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




}
