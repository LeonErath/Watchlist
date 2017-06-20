package com.example.leon.kotlinapplication.activities

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.CastAdapter
import com.example.leon.kotlinapplication.adapter.MovieAdapter
import com.example.leon.kotlinapplication.adapter.QueryAdapter
import com.example.leon.kotlinapplication.model.Movie
import com.example.leon.kotlinapplication.model.Trailers
import com.github.chuross.library.ExpandableLayout
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmChangeListener
import kotlin.properties.Delegates


class DetailActivity : AppCompatActivity() {

    val TAG: String = DetailActivity::class.simpleName!!

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
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    lateinit var recyclverViewRecom: RecyclerView
    lateinit var recomAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)



        textViewTitle = findViewById(R.id.textViewMovieTitle) as TextView
        textViewOverview = findViewById(R.id.textViewMovieOverview) as TextView
        imageView = findViewById(R.id.imageView) as ImageView
        textViewTagline = findViewById(R.id.textViewTagline) as TextView
        val buttonExpand = findViewById(R.id.button) as ImageButton
        val expandLayout = findViewById(R.id.layoutExpand) as ExpandableLayout


        buttonExpand.setOnClickListener {
            if (expandLayout.isCollapsed){
                buttonExpand.setImageResource(R.drawable.ic_expand_more_black_24dp)
                expandLayout.expand()
            }else{
                expandLayout.collapse()
                buttonExpand.setImageResource(R.drawable.ic_expand_less_black_24dp)
            }
        }


        recylcerViewCast = findViewById(R.id.recyclerViewCast) as RecyclerView
        val refreshLayout = findViewById(R.id.refreshContainer) as SwipeRefreshLayout


        // Set up cast recycler view
        val adapter = CastAdapter(this)
        recylcerViewCast.adapter = adapter
        val layout = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recylcerViewCast.layoutManager = layout

        recyclverViewRecom = findViewById(R.id.recyclerViewRecommendation) as RecyclerView
        recomAdapter = MovieAdapter(this)
        recyclverViewRecom.adapter = recomAdapter
        val layout2 = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recyclverViewRecom.layoutManager = layout2


        val extras: Bundle = intent.extras
        val movieid: Int = extras.getInt("movieid")


        // Initialize realm
        Realm.init(this)
        realm = Realm.getDefaultInstance()


        val queryAdapter = QueryAdapter(this)
        var movie: Movie = queryAdapter.getDetail(movieid)
        movie.addChangeListener(RealmChangeListener {
            updateUI(movie, adapter, recomAdapter)
        })
        adapter.addData(movie.cast)
        recomAdapter.addData(movie.recommendations)

        updateUI(movie, adapter, recomAdapter)

        //set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        supportActionBar!!.subtitle = movie.tagline
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        refreshLayout.setOnRefreshListener {
            movie = queryAdapter.getDetail(movieid)
            updateUI(movie, adapter, recomAdapter)
            refreshLayout.isRefreshing = false
        }

        val uri: Uri = Uri.parse(getString(R.string.image_base_url)
                + "/w1280"
                + movie.backdrop_path)
        Picasso.with(this).load(uri).into(imageView, object : Callback {
            override fun onSuccess() {
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                Palette.from(bitmap).generate { palette ->
                    if (palette != null) {
                        applyPalette(palette)
                    }
                }
            }

            override fun onError() {
                //TODO search for right picture
                Picasso.with(this@DetailActivity).load(R.drawable.cover).into(imageView)
            }

        })




    }

    fun updateUI(movie: Movie, adapter: CastAdapter, recomAdapter: MovieAdapter) {
        Log.i(TAG, "MovieChangeListener Trigger")
        with(movie) {
            textViewTitle.text = title
            textViewOverview.text = overview
            textViewTagline.text = tagline

        }
        adapter.addData(movie.cast)
        recomAdapter.addData(movie.recommendations)
        if (movie.results.size > 0) {
            movie.results
                    .filter { it.name == "Official Trailer" }
                    .forEach { initializeYoutubeFragment(it) }
        }
    }

    private fun initializeYoutubeFragment(trailer: Trailers) {
        try {
            val youtubeFragment = fragmentManager.findFragmentById(R.id.youtubeFragment) as YouTubePlayerFragment
            youtubeFragment.initialize(getString(R.string.youtube_key), object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, b: Boolean) {
                    // do any work here to cue video, play video, etc.
                    youTubePlayer.cueVideo(trailer.key)
                }

                override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {
                    Log.i(TAG, "YoutubePlayer failed")
                }
            })

        } catch (e: TypeCastException) {
            e.printStackTrace()
        }
    }


    fun applyPalette(palette: Palette) {
        var primaryDark: Int = ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark)
        var primary: Int = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        //collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        //collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));

        supportStartPostponedEnterTransition()

    }

    fun updateBackground(fab: FloatingActionButton, palette: Palette) {
        var lightVibrantColor: Int = palette.getLightVibrantColor(resources.getColor(android.R.color.white))
        var vibrantColor: Int = palette.getVibrantColor(resources.getColor(R.color.colorAccent))

        if (vibrantColor == resources.getColor(R.color.colorAccent)) {
            vibrantColor = palette.getMutedColor(resources.getColor(R.color.colorPrimaryDark))
            Log.i(TAG, "Palette default")
        }

    }


}
