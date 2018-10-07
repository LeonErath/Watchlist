package com.leon.app.watchlist.fragments


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
import com.leon.app.watchlist.*
import com.leon.app.watchlist.activities.MainActivity
import com.leon.app.watchlist.adapter.MovieFlatAdapter
import com.leon.app.watchlist.model.List
import io.realm.Realm
import io.realm.RealmResults
import kotlin.properties.Delegates
import io.realm.RealmConfiguration




/**
 * A simple [Fragment] subclass.
 */
class MyListFragment : Fragment() {

    val TAG: String = MyListFragment::class.simpleName!!

    var realm: Realm by Delegates.notNull()
    lateinit var adapter: MovieFlatAdapter
    lateinit var refreshLayout: SwipeRefreshLayout


    override fun onResume() {
        super.onResume()

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            //  updateUIfromRealm()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        userVisibleHint = false
        val rootView = inflater!!.inflate(R.layout.fragment_my_list, container, false)
        adapter = MovieFlatAdapter(activity as MainActivity)
        refreshLayout = SwipeRefreshLayout(activity as MainActivity)
        val recyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView
        refreshLayout = rootView.findViewById(R.id.refreshContainer) as SwipeRefreshLayout

        val queryAdapter = QueryAdapter(activity as MainActivity)
        // Initialize realm


        Realm.init(context)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        realm = Realm.getInstance(config)
        realm.refresh()

        // Set up recycler view
        adapter = MovieFlatAdapter(activity as MainActivity)
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Set up refresh listener
        refreshLayout.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            refreshLayout.isRefreshing = false
        }

        Bus.observe<DetailsLoaded>().subscribe {
            Log.i(TAG, "Movie Tagline loaded")
            adapter.notifyItemChanged(adapter.dataManager.indexOf(it.movie))
        }.registerInBus(this)
        Bus.observe<MovieEventAdd>().subscribe {
            if (it.movie.evolution == 1) {
                queryAdapter.getDetail(it.movie.id)
                adapter.add(it.movie)
            } else if (it.movie.evolution == 2) {
                adapter.removeMovie(it.movie)
            } else {

            }
        }.registerInBus(this)
        Bus.observe<MovieEventRemove>().subscribe {
            if (it.movie.evolution == 0) {
                adapter.removeMovie(it.movie)
            } else if (it.movie.evolution == 1) {
                adapter.add(it.movie)
            }

        }.registerInBus(this)

        // load data from https://api.themoviedb.org/3
        updateUIfromRealm()
        return rootView
    }

    override fun onDestroy() {
        Bus.unregister(this)
        super.onDestroy()
    }

    // updates the recycler view with the data from the realm
    private fun updateUIfromRealm() {
       val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2 as Int).findAll()
        if (results.size > 0) {
           adapter.addData(results[0]!!.results)
        }
        refreshLayout.isRefreshing = false
    }


}
