package com.leon.app.watchlist.activities

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.leon.app.watchlist.QueryAdapter
import com.leon.app.watchlist.R
import com.leon.app.watchlist.adapter.CastAdapter
import com.leon.app.watchlist.adapter.MovieAdapter
import com.leon.app.watchlist.model.Movie
import com.leon.app.watchlist.model.Trailers
import com.github.chuross.library.ExpandableLayout
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmConfiguration
import kotlin.properties.Delegates


class DetailActivity : AppCompatActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    val TAG: String = DetailActivity::class.simpleName!!

    var realm: Realm by Delegates.notNull()
    private val textViewTitle: TextView by bind(R.id.textViewMovieTitle)
    private val textViewOverview: TextView by bind(R.id.textViewMovieOverview)
    private val textViewTagline: TextView by bind(R.id.textViewTagline)
    private val textViewGenre1: TextView by bind(R.id.textViewGenre1)
    private val textViewGenre2: TextView by bind(R.id.textViewGenre2)
    private val textViewGenre3: TextView by bind(R.id.textViewGenre3)
    private val imageView: ImageView by bind(R.id.imageView)
    private val recylcerViewCast: RecyclerView by bind(R.id.recyclerViewCast)
    private val recyclerViewRecom: RecyclerView by bind(R.id.recyclerViewRecommendation)
    private val buttonExpand: ImageButton by bind(R.id.button)
    private val expandLayout: ExpandableLayout by bind(R.id.layoutExpand)
    private val mFab: FloatingActionButton by bind(R.id.fab)
    private val collapsingToolbarLayout: CollapsingToolbarLayout by bind(R.id.collapsingToolbarLayout)
    private val buttonAdd: Button by bind(R.id.buttonAdd)
    private val buttonLike: Button by bind(R.id.buttonLike)
    private val temp: LinearLayout by bind(R.id.temp)

    private val toolbar: Toolbar by bind(R.id.toolbar)

    private val recomAdapter: MovieAdapter = MovieAdapter(this)
    private val castAdapter: CastAdapter = CastAdapter(this)

    lateinit var movie: Movie
    private var movieid: Int = 0
    private val queryAdapter: QueryAdapter = QueryAdapter(this)
    private val PERCENTAGE_TO_SHOW_IMAGE = 20
    private var mMaxScrollSize: Int = 0
    private var mIsImageHidden: Boolean = false
    private var liked: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        realm = Realm.getInstance(config)


        // Get movie from database
        val extras: Bundle = intent.extras
        movieid = extras.getInt("movieid")
        movie = queryAdapter.getDetail(movieid)

        movie.addChangeListener(RealmChangeListener {
            updateUI(movie, castAdapter, recomAdapter)
            setUpButtons()
            setupGenre()
        })

        supportActionBar!!.title = movie.title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setUpButtons()
        setupCast()
        setupRecom()
        setupGenre()

        updateUI(movie, castAdapter, recomAdapter)

        buttonExpand.setOnClickListener(this)

        loadImage()


        // smooth animation for toolbar
        val appbar: AppBarLayout = findViewById(R.id.app_bar_layout) as AppBarLayout
        appbar.addOnOffsetChangedListener(this)

        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), "transition")
        collapsingToolbarLayout.title = movie.title
        collapsingToolbarLayout.setExpandedTitleColor(resources.getColor(android.R.color.transparent))


        // to ensure that scrollview is scrolled to the top
        temp.requestFocus()
    }

    private fun setupGenre() {
        when (movie.genres.size) {
            1 -> {
                textViewGenre1.visibility = View.VISIBLE
                textViewGenre1.text = movie.genres.get(0)!!.name
            }
            2 -> {
                textViewGenre1.visibility = View.VISIBLE
                textViewGenre1.text = movie.genres.get(0)!!.name
                textViewGenre2.visibility = View.VISIBLE
                textViewGenre2.text = movie.genres.get(1)!!.name
            }
            in 3..10 -> {
                textViewGenre1.visibility = View.VISIBLE
                textViewGenre1.text = movie.genres.get(0)!!.name
                textViewGenre2.visibility = View.VISIBLE
                textViewGenre2.text = movie.genres.get(1)!!.name
                textViewGenre3.visibility = View.VISIBLE
                textViewGenre3.text = movie.genres.get(2)!!.name

            }
        }
    }

    private fun setUpButtons() {
        when (movie.evolution) {
            0 -> {
                mFab.setImageDrawable(resources.getDrawable(R.drawable.ic_add_white_24dp))
                buttonAdd.text = "ADD"
            }
            1 -> {
                mFab.setImageDrawable(resources.getDrawable(R.drawable.ic_done_white_24dp))
                buttonAdd.text = "WATCHED"
            }
            2 -> {
                mFab.setImageDrawable(resources.getDrawable(R.drawable.ic_eye_white_24dp))

            }
        }
        buttonLike.setOnClickListener {
            if (liked) {
                buttonLike.setTextColor(ContextCompat.getColor(applicationContext, R.color.logoPink))
            } else {
                buttonLike.setTextColor(ContextCompat.getColor(applicationContext, R.color.buttonColor))
            }
        }

        mFab.setOnClickListener {
            queryAdapter.movieClickDetail(movie)
        }
        mFab.setOnLongClickListener {
            queryAdapter.removeClickDetail(movie)
            true
        }
    }




    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        try {
            if (mMaxScrollSize == 0)
                mMaxScrollSize = appBarLayout!!.totalScrollRange

            val currentScrollPercentage: Int = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize

            if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
                if (!mIsImageHidden) {
                    mIsImageHidden = true
                    mFab.animate().scaleY(0f).scaleX(0f).start()
                    toolbar.animate().alpha(1.0f).start()
                }
            }

            if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
                if (mIsImageHidden) {
                    mIsImageHidden = false

                    mFab.animate().scaleY(1.0f).scaleX(1.0f).start()
                }
            }
        } catch (e: ArithmeticException) {
            e.printStackTrace()
        }

    }


    private fun loadImage() {
        val uri: Uri = Uri.parse(getString(R.string.image_base_url)
                + "/w1280"
                + movie.backdrop_path)
        Picasso.with(this).load(uri).into(imageView, object : Callback {
            override fun onSuccess() {
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                Palette.from(bitmap).generate { palette ->
                    if (palette != null) {
                        applyPalette(palette)
                        updateBackground(mFab, palette)
                    }
                }
            }

            override fun onError() {
                Picasso.with(this@DetailActivity).load(R.drawable.ic_poster_detail).into(imageView)
            }

        })
    }

    override fun onRefresh() {
        movie = queryAdapter.getDetail(movieid)
        updateUI(movie, castAdapter, recomAdapter)
        //refreshLayout.isRefreshing = false
    }

    private fun setupRecom() {
        recyclerViewRecom.adapter = recomAdapter
        val layout = GridLayoutManager(this, 2)
        recyclerViewRecom.layoutManager = layout
        recomAdapter.addData(movie.recommendations)
    }

    private fun setupCast() {
        recylcerViewCast.adapter = castAdapter
        val layout = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recylcerViewCast.layoutManager = layout
        castAdapter.addData(movie.cast)
    }


    fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy { findViewById(res) as T }
    }

    override fun onClick(v: View?) {
        if (expandLayout.isCollapsed) {
            buttonExpand.setImageResource(R.drawable.ic_expand_more_black_24dp)
            expandLayout.expand()
        } else {
            expandLayout.collapse()
            buttonExpand.setImageResource(R.drawable.ic_expand_less_black_24dp)
        }
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
                    .filter { it.site == "YouTube" }
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
                    youTubePlayer.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
                        override fun onSeekTo(p0: Int) {

                        }

                        override fun onBuffering(p0: Boolean) {

                        }

                        override fun onStopped() {

                        }

                        override fun onPaused() {

                        }

                        override fun onPlaying() {

                        }

                    })
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
        val primaryDark: Int = ContextCompat.getColor(applicationContext, R.color.colorPrimaryDark)
        val primary: Int = ContextCompat.getColor(applicationContext, R.color.colorPrimary)
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary))
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark))

        supportStartPostponedEnterTransition()

    }

    fun updateBackground(fab: FloatingActionButton, palette: Palette) {
        val lightVibrantColor: Int = palette.getLightVibrantColor(resources.getColor(android.R.color.white))
        var vibrantColor: Int = palette.getVibrantColor(resources.getColor(R.color.colorAccent))

        if (vibrantColor == resources.getColor(R.color.colorAccent)) {
            vibrantColor = palette.getMutedColor(resources.getColor(R.color.colorPrimaryDark))
            Log.i(TAG, "Palette default")
        }

        fab.backgroundTintList = ColorStateList.valueOf(vibrantColor)
        fab.rippleColor = lightVibrantColor

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
