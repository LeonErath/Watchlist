package com.example.leon.kotlinapplication.binder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ahamed.multiviewadapter.SelectableBinder
import com.ahamed.multiviewadapter.SelectableViewHolder
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.DetailActivity
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.MovieAdapter
import com.example.leon.kotlinapplication.model.List
import com.example.leon.kotlinapplication.model.Movie
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmResults
import kotlin.properties.Delegates


/**
 * Created by Leon on 07.06.17.
 */

open class MovieBinder(activity: MainActivity, movieAdapter: MovieAdapter) : SelectableBinder<Movie, MovieBinder.ViewHolder>() {

    val TAG: String = MovieBinder::class.simpleName!!

    val mainActivity: MainActivity
    val movieAdapter: MovieAdapter
    init {
        this.mainActivity = activity
        this.movieAdapter = movieAdapter
    }

    override fun bind(holder: ViewHolder?, movie: Movie?, b: Boolean) {
        //holder?.tvMovie!!.text = movie?.title
        if (holder != null && holder.context != null) {
            if (movie!!.evolution == 0) {
                holder.imageBage.visibility = View.INVISIBLE
            } else {
                holder.imageBage.visibility = View.VISIBLE
            }


            val uri: Uri = Uri
                    .parse(holder.context.getString(R.string.image_base_url)
                            + "/w342"
                            + movie?.poster_path)

            Picasso.with(holder.context)
                    .load(uri)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.imageV, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError() {
                            //Try again online if cache failed
                            Picasso.with(holder.context)
                                    .load(uri)
                                    .error(R.drawable.image_default)
                                    .into(holder.imageV, object : Callback {
                                        override fun onSuccess() {

                                        }

                                        override fun onError() {
                                            Log.v("Picasso", "Could not fetch image")
                                        }
                                    })
                        }
                    })
        }

    }


    override fun canBindData(item: Any?): Boolean {
        return item is Movie
    }

    override fun create(inflater: LayoutInflater?, parent: ViewGroup?): ViewHolder {
        return ViewHolder(inflater?.inflate(R.layout.movie_item, parent, false)!!, mainActivity, movieAdapter)
    }

    override fun getSpanSize(maxSpanCount: Int): Int {
        return 2
    }

    class ViewHolder(itemView: View, activity: MainActivity, movieAdapter: MovieAdapter) : SelectableViewHolder<Movie>(itemView) {

        val TAG: String = ViewHolder::javaClass.name
        val mainActivity: MainActivity = activity
        val context: Context = itemView.context

        //var tvMovie: TextView
        var imageV: ImageView
        var imageBage: ImageView
        var cardView: CardView
        var realm: Realm by Delegates.notNull()
        var movieAdapter: MovieAdapter = movieAdapter

        init {
            //tvMovie = itemView.findViewById(R.id.textViewMovie) as TextView
            imageV = itemView.findViewById(R.id.imageView) as ImageView
            cardView = itemView.findViewById(R.id.cardView) as CardView
            imageBage = itemView.findViewById(R.id.imageBadge) as ImageView


            cardView.setOnClickListener({
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movieid", item.id)
                context.startActivity(intent)

            })
            cardView.setOnLongClickListener {
                //TODO add Use Case if movie is already in Watchlist!!
                Realm.init(context)
                realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    item.evolution++
                    val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2).findAll()
                    if (results.size > 0) {
                        Log.i(TAG, "MyList is not empty -> updates List")
                        val List = results[0]
                        val check = List.results.any { item!!.id == it.id }

                        if (!check) {
                            List.results.add(item)
                            List.total_results++
                        }

                    } else {
                        Log.i(TAG, "MyList is empty -> creates new List")
                        val List = List()
                        List.id = 2
                        List.name = "MyList"
                        List.results.add(item)
                        List.total_results++
                        realm.copyToRealmOrUpdate(List)
                    }
                    mainActivity.addToFavorite(item, movieAdapter, adapterPosition)
                    httpRequest(item.id)
                }

                movieAdapter.notifyItemChanged(adapterPosition)
                true
            }
        }

        private fun httpRequest(id: Int) {
            val queue = Volley.newRequestQueue(context)
            val url = mainActivity.getString(R.string.base_url) +
                    "movie/$id?api_key=" +
                    mainActivity.getString(R.string.key) +
                    "&language=en-US"


            // Request a string response from the provided URL.
            val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                Log.i(TAG, "Movie Details update:" + response)
                fetchRequest(response)
            }, Response.ErrorListener { error -> error.printStackTrace() })

            queue.add(stringRequest)
            queue.start()
        }

        fun fetchRequest(response: String) {
            realm.executeTransaction {
                realm.createOrUpdateObjectFromJson(Movie::class.java, response)
            }

        }

    }


}