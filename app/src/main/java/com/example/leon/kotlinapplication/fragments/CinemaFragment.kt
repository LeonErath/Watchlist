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
import com.example.leon.kotlinapplication.*
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.EndlessRecylcerViewScrollListener
import com.example.leon.kotlinapplication.adapter.LoadData
import com.example.leon.kotlinapplication.adapter.MovieAdapter
import com.example.leon.kotlinapplication.adapter.QueryAdapter
import com.example.leon.kotlinapplication.model.List
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlin.properties.Delegates



/**
 * A simple [Fragment] subclass.
 * Use the [CinemaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CinemaFragment : Fragment() {


    val TAG: String = CinemaFragment::class.simpleName!!
    var realm: Realm by Delegates.notNull()
    lateinit var adapter: MovieAdapter
    lateinit var refreshLayout: SwipeRefreshLayout
    var pageCounter: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.fragment_cinema, container, false)

        adapter = MovieAdapter(activity as MainActivity)
        refreshLayout = SwipeRefreshLayout(activity)

        val recyclerView = rootView.findViewById(R.id.recyclerView) as RecyclerView
        refreshLayout = rootView.findViewById(R.id.refreshContainer) as SwipeRefreshLayout
        val query = QueryAdapter(context)

        val load = object : LoadData {
            override fun update(type: Int) {
                val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", type).findAll()
                Log.i(TAG, " updateRealm(): Size of Cinema Movie Lists:" + results.size)
                if (results.size > 0) adapter.addData(results[0].results.sort("popularity", Sort.DESCENDING))
                refreshLayout.isRefreshing = false
            }
        }

        // Initialize realm
        Realm.init(context)
        realm = Realm.getDefaultInstance()
        realm.refresh()


        // Set up recycler view
        adapter = MovieAdapter(activity as MainActivity)
        recyclerView.adapter = adapter
        val itemAnimator = DefaultItemAnimator()
        itemAnimator.addDuration = 300
        itemAnimator.removeDuration = 300
        recyclerView.itemAnimator = itemAnimator
        val layout = GridLayoutManager(activity, 2)
        recyclerView.layoutManager = layout
        recyclerView.setOnScrollListener(object : EndlessRecylcerViewScrollListener(layout) {
            override fun onLoadMore(current_page: Int) {
                pageCounter++
                query.getCinema(pageCounter, load)
            }
        })
        // Set up refresh listener
        refreshLayout.setOnRefreshListener {
            query.getCinema(pageCounter, load)
        }

        Bus.observe<MovieEventAdd>()
                .subscribe {
                    adapter.notifyItemChanged(adapter.dataManager.indexOf(it.movie))
                }
                .registerInBus(this)
        Bus.observe<MovieEventRemove>()
                .subscribe {
                    adapter.notifyItemChanged(adapter.dataManager.indexOf(it.movie))
                }
                .registerInBus(this)
        // load data from https://api.themoviedb.org/3
        query.getCinema(pageCounter, load)
        return rootView
    }

    override fun onDestroy() {
        Bus.unregister(this)
        super.onDestroy()
    }


    fun deleteRealm() {
        Realm.init(activity)
        val realm: Realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.deleteAll()
        }

    }


}
