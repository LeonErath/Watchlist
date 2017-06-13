package com.example.leon.kotlinapplication.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.PopularMovieAdapter
import com.example.leon.kotlinapplication.model.PopularMovie
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import kotlin.properties.Delegates
import android.support.v7.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import io.realm.Sort
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import io.realm.internal.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException


/**
 * A simple [Fragment] subclass.
 * Use the [PopularFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PopularFragment(var a: MainActivity) : Fragment() {


    var realm: Realm by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView = inflater!!.inflate(R.layout.fragment_popular, container, false)
        var recyclerView = rootView.findViewById(R.id.recyclerView2) as RecyclerView
        var refreshLayout = rootView.findViewById(R.id.refreshContainer) as SwipeRefreshLayout


        // Initialize realm
        Realm.init(context)
        realm = Realm.getDefaultInstance()
        realm.refresh()

        deleteRealm()


        // Set up recycler view
        var adapter: PopularMovieAdapter = PopularMovieAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(activity, 2)

        refreshLayout.setOnRefreshListener {
            httpRequest(adapter, refreshLayout)

        }

        httpRequest(adapter, refreshLayout)
        return rootView
    }

    private fun httpRequest(adapter: PopularMovieAdapter, refreshLayout: SwipeRefreshLayout) {
        val queue = Volley.newRequestQueue(activity)
        val url = getString(R.string.base_url) + "movie/popular?api_key=" + getString(R.string.key) + "&language=en-US&page=1"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
            override fun onResponse(response: String) {
                // Display the first 500 characters of the response string.
                Log.d("Resonse", response)

                var obj: JSONObject = JSONObject(response)
                var array: JSONArray = obj.getJSONArray("results")


                fetchRequest(array, adapter, refreshLayout)
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                error.printStackTrace()
                updateRealm(adapter, refreshLayout)
            }
        })

        queue.add(stringRequest)
        queue.start()
    }

    private fun updateRealm(adapter: PopularMovieAdapter, refreshLayout: SwipeRefreshLayout) {
        var query: RealmQuery<PopularMovie> = realm.where(PopularMovie::class.java)
        var results: RealmResults<PopularMovie> = query.findAllSorted("popularity",Sort.DESCENDING)
        Log.d("eventListener", " " + results.size)

        adapter.addData(results)
        refreshLayout.isRefreshing = false
    }

    fun deleteRealm(){
        Realm.init(activity)
        var realm:Realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.deleteAll()
        }

    }

    private fun fetchRequest(jsonArray: JSONArray, adapter: PopularMovieAdapter, refreshLayout: SwipeRefreshLayout) {
        try {
            realm.executeTransaction {
                //val input: InputStream = assets.open("response.json")
                Log.d("fetchRequest", " " + jsonArray + " ")
                realm.createOrUpdateAllFromJson(PopularMovie::class.java, jsonArray)
                updateRealm(adapter, refreshLayout)
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