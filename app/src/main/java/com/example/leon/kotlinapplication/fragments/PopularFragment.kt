package com.example.leon.kotlinapplication.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.EndlessRecylcerViewScrollListener
import com.example.leon.kotlinapplication.adapter.MovieAdapter
import com.example.leon.kotlinapplication.jsonParser
import com.example.leon.kotlinapplication.model.List
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import io.realm.internal.IOException
import java.io.FileNotFoundException
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 * Use the [PopularFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PopularFragment(val a: MainActivity) : Fragment() {

    val TAG: String = PopularFragment::class.simpleName!!
    var realm: Realm by Delegates.notNull()
    var adapter = MovieAdapter(a)
    var refreshLayout = SwipeRefreshLayout(a)
    var pageCounter: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_popular, container, false)
        val recyclerView = rootView.findViewById(R.id.recyclerView2) as RecyclerView
        refreshLayout = rootView.findViewById(R.id.refreshContainer) as SwipeRefreshLayout

        Realm.init(context)
        // Initialize realm
        realm = Realm.getDefaultInstance()
        realm.refresh()


        // Set up recycler view
        adapter = MovieAdapter(a)
        recyclerView.adapter = adapter
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 300
        itemAnimator.removeDuration = 300
        recyclerView.itemAnimator = itemAnimator
        var layout = GridLayoutManager(activity, 2)
        recyclerView.layoutManager = layout
        recyclerView.setOnScrollListener(object : EndlessRecylcerViewScrollListener(layout) {
            override fun onLoadMore(current_page: Int) {
                pageCounter++
                httpRequest(pageCounter)
            }
        })

        // Set up refresh listener
        refreshLayout.setOnRefreshListener { httpRequest(1) }

        // load data from https://api.themoviedb.org/3
        httpRequest(1)
        return rootView
    }

    // httpRequest() makes a GET Request to https://api.themoviedb.org/3
    // The response is a json String
    private fun httpRequest(page: Int) {
        //TODO Substitute this methode by QueryAdapter
        val queue = Volley.newRequestQueue(activity)
        val url = getString(R.string.base_url) +
                "movie/popular?api_key=" +
                getString(R.string.key) +
                "&language=en-US&page=$page"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            Log.i(TAG, "Response:" + response)
            fetchRequest(response)
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            updateUIfromRealm()
        })

        queue.add(stringRequest)
        queue.start()
    }


    // updates the recycler view with the data from the realm
    private fun updateUIfromRealm() {
        val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 0).findAll()
        Log.i(TAG, " updateRealm(): Size of Popular Movie Lists:" + results.size)

        if (results.size > 0) adapter.addData(results[0].results.sort("popularity", Sort.DESCENDING))


        refreshLayout.isRefreshing = false

    }

    fun deleteRealm() {
        Realm.init(activity)
        val realm: Realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.deleteAll()
        }

    }

    // fetches the json string response to the realm database
    // calls updateUIfromRealm afterwards to notify the user about the new data
    private fun fetchRequest(response: String) {
        try {
            realm.executeTransaction {
                val modedResponse: String = jsonParser(response)
                        .insertValueInt("id", 0)
                        .insertValueString("name", "popularMovies")
                        .makeArray().json

                Log.i(TAG, "moded Response: " + modedResponse)
                realm.createOrUpdateAllFromJson(List::class.java, modedResponse)
                updateUIfromRealm()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            throw RuntimeException(e)
        } catch (e: RealmPrimaryKeyConstraintException) {
            e.printStackTrace()
        }

    }

}




