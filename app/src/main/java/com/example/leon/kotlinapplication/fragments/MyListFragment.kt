package com.example.leon.kotlinapplication.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.leon.kotlinapplication.*
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.MovieFlatAdapter
import com.example.leon.kotlinapplication.adapter.OnLoadedListener
import com.example.leon.kotlinapplication.adapter.QueryAdapter
import com.example.leon.kotlinapplication.model.Movie
import io.realm.Realm
import kotlin.properties.Delegates


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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        userVisibleHint = false
        val rootView = inflater!!.inflate(R.layout.fragment_my_list, container, false)
        adapter = MovieFlatAdapter(activity as MainActivity)
        refreshLayout = SwipeRefreshLayout(activity)
        val recyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView
        refreshLayout = rootView.findViewById(R.id.refreshContainer) as SwipeRefreshLayout
        var queryAdapter = QueryAdapter(activity)
        queryAdapter.setOnLoadedListener2(object : OnLoadedListener {
            override fun complete(movie: Movie) {
                adapter.notifyDataSetChanged()
            }

        })

        // Initialize realm
        Realm.init(context)
        realm = Realm.getDefaultInstance()
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

        Bus.observe<MovieEventAdd>().subscribe {
            queryAdapter.getDetail(it.movie.id)
            adapter.add(it.movie)
        }.registerInBus(this)
        Bus.observe<MovieEventRemove>().subscribe {
            adapter.removeMovie(it.movie)
        }.registerInBus(this)

        // load data from https://api.themoviedb.org/3
        //updateUIfromRealm()
        return rootView
    }

    override fun onDestroy() {
        Bus.unregister(this)
        super.onDestroy()
    }

    /*// updates the recycler view with the data from the realm
    private fun updateUIfromRealm() {
        val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2).findAll()

       results.addChangeListener(RealmChangeListener {
            if (results.size > 0) {
                adapter.addData(results[0].results)
            }
        })

        if (results.size > 0) {
            adapter.addData(results[0].results)
        }
        refreshLayout.isRefreshing = false
    }*/


}
