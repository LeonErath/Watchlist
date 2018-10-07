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
import android.widget.ProgressBar
import com.example.leon.kotlinapplication.LoadData
import com.example.leon.kotlinapplication.QueryAdapter
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.GenreActivity
import com.example.leon.kotlinapplication.adapter.GenreAdapter
import com.example.leon.kotlinapplication.model.Genre
import io.realm.Realm
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
        // Inflate the layout for this fragment
        var root = inflater!!.inflate(R.layout.fragment_genre, container, false)

        Realm.init(context)
        realm = Realm.getDefaultInstance()
        adapter = GenreAdapter(activity as GenreActivity)
        refreshLayout = root.findViewById(R.id.refreshContainer) as SwipeRefreshLayout
        recyclerView = root.findViewById(R.id.recyclerView) as RecyclerView
        val progressbar: ProgressBar = root.findViewById(R.id.progressBar) as ProgressBar

        val queryAdapter = QueryAdapter(activity as GenreActivity)

        queryAdapter.getGenre(object : LoadData {
            override fun update(type: Int) {
                var results: RealmResults<Genre> = realm.where(Genre::class.java).findAll()
                if (results.size > 0) {
                    adapter.addData(results)
                }
            }

        })

        recyclerView.adapter = adapter
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(context)

        refreshLayout.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            refreshLayout.isRefreshing = false
        }



        return root
    }

}// Required empty public constructor
