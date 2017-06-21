package com.example.leon.kotlinapplication.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.MovieFlatAdapter
import com.example.leon.kotlinapplication.model.List
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.Sort
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 */
class MyListFragment(var a: MainActivity) : Fragment() {

    val TAG: String = MyListFragment::class.simpleName!!

    var realm: Realm by Delegates.notNull()
    var adapter = MovieFlatAdapter(a)
    var refreshLayout = SwipeRefreshLayout(a)

    override fun onResume() {
        super.onResume()
        updateUIfromRealm()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_my_list, container, false)
        val recyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView
        refreshLayout = rootView.findViewById(R.id.refreshContainer) as SwipeRefreshLayout


        // Initialize realm
        Realm.init(context)
        realm = Realm.getDefaultInstance()
        realm.refresh()

        // Set up recycler view
        adapter = MovieFlatAdapter(a)
        recyclerView.adapter = adapter
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 300
        itemAnimator.removeDuration = 300
        recyclerView.itemAnimator = itemAnimator
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Set up refresh listener
        refreshLayout.setOnRefreshListener { updateUIfromRealm() }

        // load data from https://api.themoviedb.org/3
        updateUIfromRealm()
        return rootView
    }

    // updates the recycler view with the data from the realm
    private fun updateUIfromRealm() {
        val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2).findAll()
        Log.i(TAG, " updateRealm(): Size of Popular Movie Lists:" + results.size)

        results.addChangeListener(RealmChangeListener {
            Log.i(TAG, "Change in List")
            adapter.addData(results[0].results)

        })
        if (results.size > 0) adapter.addData(results[0].results.sort("popularity", Sort.DESCENDING))
        refreshLayout.isRefreshing = false
    }

}
