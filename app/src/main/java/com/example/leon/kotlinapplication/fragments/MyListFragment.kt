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
import com.example.leon.kotlinapplication.Bus
import com.example.leon.kotlinapplication.MovieEvent
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.MovieFlatAdapter
import com.example.leon.kotlinapplication.adapter.OnLoadedListener
import com.example.leon.kotlinapplication.adapter.QueryAdapter
import com.example.leon.kotlinapplication.model.List
import com.example.leon.kotlinapplication.model.Movie
import com.example.leon.kotlinapplication.registerInBus
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
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
        updateUIfromRealm()
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
        refreshLayout.setOnRefreshListener { updateUIfromRealm() }

        Bus.observe<MovieEvent>().subscribe {
            updateUIfromRealm()
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
        val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2).findAll()

        /*  results.addChangeListener(RealmChangeListener {
              updateAdaper(results)
          })*/

        updateAdaper(results = results)
        refreshLayout.isRefreshing = false
    }

    private fun updateAdaper(results: RealmResults<List>) {
        var queryAdapter = QueryAdapter(activity)
        var mylist = RealmList<Movie>()

        queryAdapter.setOnLoadedListener2(object : OnLoadedListener {
            override fun complete(movie: Movie) {
                mylist.add(movie)
                adapter.addData(mylist)
            }

        })
        if (results.size > 0) {
            for (movie in results[0].results) {
                queryAdapter.getDetail(movie.id)
            }

        }
    }

}
