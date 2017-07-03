package com.example.leon.kotlinapplication.binder

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.ahamed.multiviewadapter.SelectableBinder
import com.ahamed.multiviewadapter.SelectableViewHolder
import com.example.leon.kotlinapplication.Bus
import com.example.leon.kotlinapplication.MovieEvent
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.DetailActivity
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.MovieFlatAdapter
import com.example.leon.kotlinapplication.adapter.QueryAdapter
import com.example.leon.kotlinapplication.model.Movie
import java.util.*
import java.util.concurrent.TimeUnit


open class MovieFlatBinder(adapter2: MovieFlatAdapter, activity: MainActivity) : SelectableBinder<Movie, MovieFlatBinder.ViewHolder>() {
    val TAG: String? = MovieFlatBinder::class.simpleName

    val adapater: MovieFlatAdapter = adapter2
    val mainActivity: MainActivity

    init {
        this.mainActivity = activity
    }


    override fun bind(holder: ViewHolder?, movie: Movie?, p2: Boolean) {
        if (holder != null && movie != null) {
            with(holder) {
                with(movie) {
                    tvMovie.text = title
                    tvTagline.text = tagline
                    var time: Long = Calendar.getInstance().timeInMillis
                    buttonTime.text = "${TimeUnit.MILLISECONDS.toDays(movie.timeAdded - time)} Days"
                }
            }
        }
    }


    override fun canBindData(item: Any?): Boolean {
        return item is Movie
    }

    override fun create(inflater: LayoutInflater?, parent: ViewGroup?): ViewHolder {
        return ViewHolder(inflater?.inflate(R.layout.movie_item_flat, parent, false)!!, adapater, mainActivity)
    }

    override fun getSpanSize(maxSpanCount: Int): Int {
        return 1
    }

    class ViewHolder(itemView: View, adapter: MovieFlatAdapter, activity: MainActivity) : SelectableViewHolder<Movie>(itemView) {

        val mainActivity: MainActivity = activity
        val context: Context = itemView.context
        val adapter: MovieFlatAdapter

        var tvMovie: TextView
        var tvTagline: TextView
        var buttonTime: Button
        var buttonWatched: Button
        var buttonRemove: Button



        init {
            this.adapter = adapter
            tvMovie = itemView.findViewById(R.id.textViewMovie) as TextView
            tvTagline = itemView.findViewById(R.id.textViewTagline) as TextView
            buttonTime = itemView.findViewById(R.id.buttonTime) as Button
            buttonRemove = itemView.findViewById(R.id.buttonRemove) as Button
            buttonWatched = itemView.findViewById(R.id.buttonWatched) as Button

            val queryAdapter = QueryAdapter(context)

            setItemClickListener { view, item ->
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movieid", item.id)
                context.startActivity(intent)
            }

            setItemLongClickListener { view, movie ->
                val check: Boolean = queryAdapter.movieClick(item)
                if (check) {
                    mainActivity.addToFavorite(item)
                } else {
                    mainActivity.removeFromFavorite(item)

                }
                adapter.removeMovie(item)
                Bus.send(MovieEvent(item))
                true
            }
        }


    }

}