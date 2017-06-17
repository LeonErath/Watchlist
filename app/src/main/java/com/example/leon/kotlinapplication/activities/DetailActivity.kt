package com.example.leon.kotlinapplication.activities

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Window
import android.view.WindowManager
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
import com.squareup.picasso.Callback
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
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

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

        recylcerViewCast = findViewById(R.id.recyclerViewCast) as RecyclerView
        val refreshLayout = findViewById(R.id.refreshContainer) as SwipeRefreshLayout
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

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

        updateUI(movie, adapter)

        //set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        supportActionBar!!.subtitle = movie.tagline
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout

        collapsingToolbarLayout.setExpandedTitleColor(resources.getColor(android.R.color.transparent))

        collapsingToolbarLayout.title = movie.title

        refreshLayout.setOnRefreshListener {
            movie = queryAdapter.getDetail(movieid)
            updateUI(movie, adapter)
            refreshLayout.isRefreshing = false
        }

        val uri: Uri = Uri.parse(getString(R.string.image_base_url)
                + "/w1280"
                + movie.backdrop_path)
        Picasso.with(this).load(uri).into(imageView, object : Callback {
            override fun onSuccess() {
                var bitmap: Bitmap = (imageView.getDrawable() as BitmapDrawable).getBitmap()
                Palette.from(bitmap).generate(object : Palette.PaletteAsyncListener {
                    override fun onGenerated(palette: Palette?) {
                        if (palette != null) {
                            applyPalette(palette)
                        }
                    }

                });
            }

            override fun onError() {
                //TODO search for right picture
                Picasso.with(this@DetailActivity).load(R.drawable.cover).into(imageView)
            }

        })




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
                    try {
                        val youtubeFragment = fragmentManager.findFragmentById(R.id.youtubeFragment) as YouTubePlayerFragment
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

                    } catch (e: TypeCastException) {
                        e.printStackTrace()
                    }

                }
            }
        }
    }


    fun applyPalette(palette: Palette) {
        var primaryDark: Int = resources.getColor(R.color.colorPrimaryDark)
        var primary: Int = resources.getColor(R.color.colorPrimary)
        //collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        //collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        val window: Window = this.window

        // finally change the color
        window.setStatusBarColor(primaryDark)

        updateBackground(findViewById(R.id.fab) as FloatingActionButton, palette);
        supportStartPostponedEnterTransition();

    }

    fun updateBackground(fab: FloatingActionButton, palette: Palette) {
        var lightVibrantColor: Int = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        var vibrantColor: Int = palette.getVibrantColor(getResources().getColor(R.color.colorAccent));

        if (vibrantColor == resources.getColor(R.color.colorAccent)) {
            vibrantColor = palette.getMutedColor(resources.getColor(R.color.colorPrimaryDark))
            Log.d("DetailActivity", "Palette default")
        }

        fab.rippleColor = lightVibrantColor
        fab.backgroundTintList = ColorStateList.valueOf(vibrantColor)
    }


}
