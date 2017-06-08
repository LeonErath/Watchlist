package com.example.leon.kotlinapplication.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.leon.kotlinapplication.EventListener
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.MovieAdapter
import com.example.leon.kotlinapplication.model.Movie
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 * Use the [AllMovieList.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllMovieList(var a: MainActivity) : Fragment() {


    var realm: Realm by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView = inflater!!.inflate(R.layout.fragment_all_movie_list, container, false)
        var recyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView

        // Initialize realm
        Realm.init(context)
        realm = Realm.getDefaultInstance()

        // Set up recycler view
        var adapter: MovieAdapter = MovieAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // fill recycler view with data from database
        var query: RealmQuery<Movie> = realm.where(Movie::class.java)
        var results: RealmResults<Movie> = query.findAll()
        adapter.addData(results)

        // updates recycler view if user creates a new movie
        a.setOnEventListener(object : EventListener {
            override fun updateRecyclerView() {
                Log.d("eventListener", "triggered")
                var query: RealmQuery<Movie> = realm.where(Movie::class.java)
                var results: RealmResults<Movie> = query.findAll()
                Log.d("eventListener", " " + results.size)
                adapter.addData(results)

            }

        })
        return rootView
    }


}// Required empty public constructor
