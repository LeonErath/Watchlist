package com.example.leon.kotlinapplication.activities

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.Element
import android.support.v8.renderscript.RenderScript
import android.support.v8.renderscript.ScriptIntrinsicBlur
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.leon.kotlinapplication.LoadData
import com.example.leon.kotlinapplication.QueryAdapter
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.MovieAdapter
import com.example.leon.kotlinapplication.model.Person
import com.github.chuross.library.ExpandableLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.realm.Realm


class PersonActivity : AppCompatActivity(), AppBarLayout.OnOffsetChangedListener, View.OnClickListener {


    private val textViewName: TextView by bind(R.id.textViewName)
    private val textViewPlace: TextView by bind(R.id.textViewPlace)
    private val textViewBiography: TextView by bind(R.id.textViewBiography)
    private val imageView: ImageView by bind(R.id.imageView)
    private val imagePerson: ImageView by bind(R.id.imagePerson)
    private val recyclerViewMovies: RecyclerView by bind(R.id.recyclerViewMovies)
    private val buttonExpand: ImageButton by bind(R.id.button)
    private val expandLayout: ExpandableLayout by bind(R.id.layoutExpand)
    private val mFab: FloatingActionButton by bind(R.id.fab)
    private val collapsingToolbarLayout: CollapsingToolbarLayout by bind(R.id.collapsingToolbarLayout)
    private val buttonLike: Button by bind(R.id.buttonLike)
    private val toolbar: Toolbar by bind(R.id.toolbar)
    private val movieAdapter: MovieAdapter = MovieAdapter(this)
    private val queryAdapter: QueryAdapter = QueryAdapter(this)
    private val PERCENTAGE_TO_SHOW_IMAGE = 20
    private val SHRINK_FACTOR = 2
    private var mMaxScrollSize: Int = 0
    private var mIsImageHidden: Boolean = false
    private var liked: Boolean = false
    lateinit var realm: Realm
    lateinit var person: Person
    private var personid: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setUpToolbar()
        // Initialize realm
        Realm.init(this)
        realm = Realm.getDefaultInstance()

        // Get movie from database
        val extras: Bundle = intent.extras
        personid = extras.getInt("personid")
        queryAdapter.getPerson(personid, object : LoadData {
            override fun update(type: Int) {
                person = queryAdapter.findPerson(personid)
                refreshUI(person)
            }

        })


    }

    private fun refreshUI(person: Person) {
        supportActionBar!!.title = person.name
        textViewName.text = person.name
        textViewPlace.text = person.place_of_birth
        textViewBiography.text = person.biography
        loadImage()

        buttonExpand.setOnClickListener(this)

        if (person.movies.size > 0) {
            // add to recycler view
        }
    }

    private fun loadImage() {
        val uri: Uri = Uri.parse(getString(R.string.image_base_url)
                + "/w300"
                + person.profile_path)
        Picasso.with(this).load(uri).into(imageView, object : Callback {
            override fun onSuccess() {
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                var bitmap2 = bitmap.copy(bitmap.getConfig(), true);
                imageView.setImageBitmap(blurBitmap(bitmap))
                imageView.setColorFilter(ContextCompat.getColor(applicationContext, R.color.colorTransparent))

            }

            override fun onError() {
                Picasso.with(this@PersonActivity)
                        .load(R.drawable.ic_poster_detail)
                        .into(imageView)
            }

        })
    }

    private fun setUpToolbar() {
        // smooth animation for toolbar
        val appbar: AppBarLayout = findViewById(R.id.app_bar_layout) as AppBarLayout
        appbar.addOnOffsetChangedListener(this)

        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), "transition")
        collapsingToolbarLayout.setExpandedTitleColor(resources.getColor(android.R.color.transparent))

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

    fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy { findViewById(res) as T }
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


            val floatScroll: Float = SHRINK_FACTOR * (currentScrollPercentage.toFloat() / 100)
            if (1 - floatScroll >= 0) {
                imagePerson.scaleY = 1 - floatScroll
                imagePerson.scaleX = 1 - floatScroll
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

    fun blurBitmap(bitmap: Bitmap): Bitmap {

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        val outBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)

        //Instantiate a new Renderscript
        val rs = RenderScript.create(getApplicationContext())

        //Create an Intrinsic Blur Script using the Renderscript
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        val allIn = Allocation.createFromBitmap(rs, bitmap)
        val allOut = Allocation.createFromBitmap(rs, outBitmap)

        //Set the radius of the blur
        blurScript.setRadius(25f)

        //Perform the Renderscript
        blurScript.setInput(allIn)
        blurScript.forEach(allOut)

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap)

        //recycle the original bitmap
        //bitmap.recycle()

        //After finishing everything, we destroy the Renderscript.
        rs.destroy()

        return outBitmap


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
