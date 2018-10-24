package com.leon.app.watchlist.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.leon.app.watchlist.LoadData
import com.leon.app.watchlist.QueryAdapter
import com.leon.app.watchlist.R
import com.leon.app.watchlist.RealmController
import com.leon.app.watchlist.activities.DetailActivity
import com.leon.app.watchlist.activities.GenreActivity
import com.leon.app.watchlist.activities.MainActivity
import com.leon.app.watchlist.adapter.GenreAdapter
import com.leon.app.watchlist.model.Genre
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults


/**
 * A simple [Fragment] subclass.
 */
class GenreFragment : Fragment() {

    lateinit var realm: Realm
    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var adapter: GenreAdapter
    lateinit var recyclerView: RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater!!.inflate(R.layout.fragment_genre, container, false)


        realm = RealmController(activity as GenreActivity).realm
        realm.refresh()

        adapter = GenreAdapter(activity as GenreActivity)
        refreshLayout = root.findViewById(R.id.refreshContainer) as SwipeRefreshLayout
        recyclerView = root.findViewById(R.id.recyclerView) as RecyclerView
        setUpRecyclerView();


        val queryAdapter = QueryAdapter(activity as GenreActivity)

        queryAdapter.getGenre(object : LoadData {
            override fun update(type: Int) {
                val results: RealmResults<Genre> = realm.where(Genre::class.java).findAll()
                if (results.size > 0) {
                    adapter.addData(results)
                }
            }

        })


        refreshLayout.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            refreshLayout.isRefreshing = false
        }



        return root
    }


    fun setUpRecyclerView(){
        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(activity as GenreActivity)
    }

}
