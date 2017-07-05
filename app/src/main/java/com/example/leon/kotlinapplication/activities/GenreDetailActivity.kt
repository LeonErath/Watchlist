package com.example.leon.kotlinapplication.activities

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.example.leon.kotlinapplication.LoadData
import com.example.leon.kotlinapplication.QueryAdapter
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.adapter.MovieAdapter
import com.example.leon.kotlinapplication.model.Genre
import com.example.leon.kotlinapplication.model.List
import io.realm.Realm
import io.realm.RealmResults

class GenreDetailActivity : AppCompatActivity() {

    private val TAG = GenreDetailActivity::class.java.simpleName
    var genreid: Int = 0
    private val toolbar: Toolbar by bind(R.id.toolbar)
    private val recyclerView: RecyclerView by bind(R.id.recyclerView)
    lateinit var realm: Realm
    lateinit var queryAdapter: QueryAdapter
    lateinit var genre: Genre
    lateinit var adapter: MovieAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genre_detail)


        // Get movies from database
        val extras: Bundle = intent.extras
        genreid = extras.getInt("id")
        // set up Relam
        Realm.init(this)
        realm = Realm.getDefaultInstance()
        var results: RealmResults<Genre> = realm.where(Genre::class.java).equalTo("id", genreid).findAll()
        if (results.size > 0) {
            genre = results.first()

            setSupportActionBar(toolbar)
            supportActionBar!!.title = genre.name
            toolbar.setTitleTextColor(Color.WHITE)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)


            adapter = MovieAdapter(this)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(this, 2)

            queryAdapter = QueryAdapter(this)
            queryAdapter.getGenreMovie(genreid, object : LoadData {
                override fun update(type: Int) {
                    var results: RealmResults<List> = realm.where(List::class.java).equalTo("id", genreid).findAll()
                    if (results.size > 0) {
                        adapter.addData(results.first().results)
                    }
                }

            })


        }
    }

    fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
        @Suppress("UNCHECKED_CAST")
        return lazy { findViewById(res) as T }
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
