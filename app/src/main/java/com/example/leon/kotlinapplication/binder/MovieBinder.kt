package com.example.leon.kotlinapplication.binder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.Log
import android.view.*
import android.widget.ImageView
import com.ahamed.multiviewadapter.SelectableBinder
import com.ahamed.multiviewadapter.SelectableViewHolder
import com.example.leon.kotlinapplication.Bus
import com.example.leon.kotlinapplication.DoubleTapEvent
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.DetailActivity
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.MovieAdapter
import com.example.leon.kotlinapplication.adapter.QueryAdapter
import com.example.leon.kotlinapplication.model.Movie
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlin.properties.Delegates


/**
 * Created by Leon on 07.06.17.
 */

open class MovieBinder(activity: MainActivity, movieAdapter: MovieAdapter) : SelectableBinder<Movie, MovieBinder.ViewHolder>() {

    val TAG: String = MovieBinder::class.simpleName!!

    val mainActivity: MainActivity = activity
    val movieAdapter: MovieAdapter

    init {
        this.movieAdapter = movieAdapter
    }

    override fun bind(holder: ViewHolder?, movie: Movie?, b: Boolean) {

        //holder?.tvMovie!!.text = movie?.title
        if (holder != null) {
            with(holder) {
                when (movie!!.evolution) {
                    0 -> imageBage.visibility = View.INVISIBLE
                    1 -> {
                        val color: Int = ContextCompat.getColor(context, R.color.logoPink)
                        imageBage.setColorFilter(color)
                        imageBage.visibility = View.VISIBLE
                    }
                    2 -> {
                        val color: Int = ContextCompat.getColor(context, R.color.logoBlue)
                        imageBage.setColorFilter(color)
                        imageBage.visibility = View.VISIBLE
                    }

                }
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
        var imageV: ImageView = itemView.findViewById(R.id.imageView) as ImageView
        var imageBage: ImageView
        var cardView: CardView
        var realm: Realm by Delegates.notNull()
        var movieAdapter: MovieAdapter = movieAdapter

        init {
            //tvMovie = itemView.findViewById(R.id.textViewMovie) as TextView
            cardView = itemView.findViewById(R.id.cardView) as CardView
            imageBage = itemView.findViewById(R.id.imageBadge) as ImageView

            val queryAdapter = QueryAdapter(mainActivity)

            val gd = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

                override fun onDoubleTap(e: MotionEvent): Boolean {
                    Bus.send(DoubleTapEvent(item))
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    when (item.evolution) {
                        0 -> {
                            queryAdapter.movieClickDetail(movie = item)
                            mainActivity.addToFavorite(item)
                        }
                        1 -> {
                            queryAdapter.removeClickDetail(movie = item)
                            mainActivity.removeFromFavorite(item)
                        }
                    }
                    movieAdapter.notifyItemChanged(adapterPosition)

                    super.onLongPress(e)

                }

                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("movieid", item.id)
                    context.startActivity(intent)
                    return super.onSingleTapConfirmed(e)
                }

            })


            cardView.setOnTouchListener { v, event ->
                gd.onTouchEvent(event)
            }


        }


    }


}