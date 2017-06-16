package com.example.leon.kotlinapplication.activities

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
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
import com.example.leon.kotlinapplication.model.Movie
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmChangeListener
import kotlin.properties.Delegates


class DetailActivity : AppCompatActivity() {


    var realm: Realm by Delegates.notNull()
    lateinit var textViewTitle: TextView
    lateinit var textViewOverview: TextView
    lateinit var imageView: ImageView
    lateinit var textViewDate: TextView
    lateinit var textViewTagline: TextView
    lateinit var textViewScore: TextView
    lateinit var textViewRevenue: TextView
    lateinit var imageViewAdd: ImageView
    lateinit var recylcerViewCast: RecyclerView
    lateinit var recyclerViewYoutube: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        textViewTitle = findViewById(R.id.textViewMovieTitle) as TextView
        textViewOverview = findViewById(R.id.textViewMovieOverview) as TextView
        imageView = findViewById(R.id.imageView) as ImageView
        textViewDate = findViewById(R.id.textViewDate) as TextView
        textViewTagline = findViewById(R.id.textViewTagline) as TextView
        textViewScore = findViewById(R.id.textViewScore) as TextView
        textViewRevenue = findViewById(R.id.textViewRevenue) as TextView
        imageViewAdd = findViewById(R.id.imageViewAdd) as ImageView
        recylcerViewCast = findViewById(R.id.recyclerViewCast) as RecyclerView
        val refreshLayout = findViewById(R.id.refreshLayout) as SwipeRefreshLayout


        // Set up cast recycler view
        var adapter = CastAdapter(this)
        recylcerViewCast.adapter = adapter
        val itemAnimator = DefaultItemAnimator()
        val layout = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recylcerViewCast.layoutManager = layout



        val extras: Bundle = intent.extras
        val movieid: Int = extras.getInt("movieid")


        // Initialize realm
        Realm.init(this)
        realm = Realm.getDefaultInstance()


        val queryAdapter = QueryAdapter(this)
        var movie: Movie = queryAdapter.getDetail(movieid)
        movie.addChangeListener(RealmChangeListener {
            updateUI(movie, adapter)
        })
        adapter.addData(movie.cast)
        imageViewAdd.setColorFilter(Color.GRAY)
        updateUI(movie, adapter)


        val uri: Uri = Uri.parse(getString(R.string.image_base_url)
                + "/w1280"
                + movie.backdrop_path)
        Picasso.with(this).load(uri).into(imageView)


        refreshLayout.setOnRefreshListener {
            movie = queryAdapter.getDetail(movieid)
            updateUI(movie, adapter)
            refreshLayout.isRefreshing = false;
        }

    }

    fun updateUI(movie: Movie, adapter: CastAdapter) {
        Log.d("DetailActivity", "MovieChangeListener Trigger")
        with(movie) {
            textViewTitle.text = title
            textViewOverview.text = overview
            textViewDate.text = com.example.leon.kotlinapplication.dateParser(release_date).parse().dateString
            textViewTagline.text = tagline
            textViewRevenue.text = com.example.leon.kotlinapplication.moneyParser(revenue).parse()
            textViewScore.text = popularity.toString()
        }
        adapter.addData(movie.cast)
        if (movie.results.size > 0) {
            for (trailer in movie.results) {
                if (trailer.name == "Official Trailer") {
                    var youtubeFragment = fragmentManager.findFragmentById(R.id.youtubeFragment) as YouTubePlayerFragment
                    youtubeFragment?.initialize(getString(R.string.youtube_key),
                            object : YouTubePlayer.OnInitializedListener {
                                override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, b: Boolean) {
                                    // do any work here to cue video, play video, etc.
                                    youTubePlayer.cueVideo(trailer.key)
                                }

                                override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {
                                    Log.d("DetailActivity", "YoutubePlayer failed")
                                }
                            })

                }
            }
        }
    }


}
