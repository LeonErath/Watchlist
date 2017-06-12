package com.example.leon.kotlinapplication.fragments


import android.content.res.AssetManager
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
import android.databinding.adapters.TextViewBindingAdapter.setText
import android.support.v7.widget.GridLayoutManager
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.RequestQueue
import com.android.volley.Response
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import io.realm.internal.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream


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
        recyclerView.layoutManager = GridLayoutManager(activity, 2)

        // fill recycler view with data from database
        updateRealm(adapter)


        // updates recycler view if user creates a new movie
        a.setOnEventListener(object : EventListener {
            override fun updateRecyclerView() {
                Log.d("eventListener", "triggered")
                updateRealm(adapter)
            }

        })

        val queue = Volley.newRequestQueue(activity)
        val url = getString(R.string.base_url) + "movie/popular?api_key=" + getString(R.string.key) + "&language=en-US&page=1"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, object : Response.Listener<String> {
            override fun onResponse(response: String) {
                // Display the first 500 characters of the response string.
                Log.d("Resonse", response)
                var obj: JSONObject = JSONObject(response)
                var array: JSONArray = obj.getJSONArray("results")

                fetchRequest(array)
            }
        }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                error.printStackTrace()
            }
        })

        queue.add(stringRequest);


        return rootView
    }

    private fun updateRealm(adapter: MovieAdapter) {
        var query: RealmQuery<Movie> = realm.where(Movie::class.java)
        var results: RealmResults<Movie> = query.findAll()
        Log.d("eventListener", " " + results.size)

        adapter.addData(results)
    }

    private fun fetchRequest(jsonArray: JSONArray) {
        realm.executeTransactionAsync(Realm.Transaction() {
            @Override
            fun execute(bgRealm: Realm) {
                try {
                    //val input: InputStream = assets.open("response.json")
                    bgRealm.createOrUpdateAllFromJson(Movie::class.java, jsonArray)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                } catch (e: RealmPrimaryKeyConstraintException) {
                    e.printStackTrace()
                }
            }
        }, Realm.Transaction.OnSuccess() {
            @Override
            fun onSuccess() {
                // Transaction was a success.
            }
        }, Realm.Transaction.OnError() {
            @Override
            fun onError(error: Throwable) {
                // Transaction failed and was automatically canceled.
            }
        });

    }


}// Required empty public constructor
