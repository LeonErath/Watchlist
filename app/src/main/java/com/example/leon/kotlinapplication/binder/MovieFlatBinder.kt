package com.example.leon.kotlinapplication.binder

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ahamed.multiviewadapter.SelectableBinder
import com.ahamed.multiviewadapter.SelectableViewHolder
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.DetailActivity
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.adapter.MovieFlatAdapter
import com.example.leon.kotlinapplication.dateParser
import com.example.leon.kotlinapplication.model.List
import com.example.leon.kotlinapplication.model.Movie
import com.example.leon.kotlinapplication.moneyParser
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmResults


open class MovieFlatBinder(adapter2: MovieFlatAdapter, activity: MainActivity) : SelectableBinder<Movie, MovieFlatBinder.ViewHolder>() {
    val adapater: MovieFlatAdapter
    val mainActivity: MainActivity

    init {
        this.adapater = adapter2
        this.mainActivity = activity
    }


    override fun bind(holder: ViewHolder?, movie: Movie?, p2: Boolean) {
        if (holder != null && movie != null) {
            with(holder) {
                with(movie) {
                    tvMovie.text = title
                    tvOverview.text = overview
                    tvRevenue.text = moneyParser(revenue).parse()
                    tvScore.text = popularity.toString()
                    tvDate.text = dateParser(release_date).parse().dateString
                }

            }


            val uri: Uri = Uri
                    .parse(holder.context.getString(R.string.image_base_url)
                            + "/w342"
                            + movie?.poster_path)
            Picasso.with(holder.context)
                    .load(uri)
                    .into(holder.imageMovie)
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

        val mainActivity: MainActivity
        val context: Context = itemView.context
        val adapter: MovieFlatAdapter

        var tvMovie: TextView
        var tvOverview: TextView
        var tvRevenue: TextView
        var tvScore: TextView
        var tvDate: TextView


        var imageMovie: ImageView

        override fun getSwipeDirections(): Int {
            return ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        }


        init {
            this.mainActivity = activity
            this.adapter = adapter
            tvMovie = itemView.findViewById(R.id.textViewMovie) as TextView
            tvOverview = itemView.findViewById(R.id.textViewOverview) as TextView
            tvRevenue = itemView.findViewById(R.id.textViewRevenue) as TextView
            tvScore = itemView.findViewById(R.id.textViewScore) as TextView
            tvDate = itemView.findViewById(R.id.textViewDate) as TextView

            imageMovie = itemView.findViewById(R.id.imageMovie) as ImageView
            Realm.init(context)
            val realm: Realm = Realm.getDefaultInstance()

            setItemClickListener { view, item ->
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movieid", item.id)
                context.startActivity(intent)
            }

            setItemLongClickListener(object : OnItemLongClickListener<Movie> {
                override fun onItemLongClick(view: View?, movie: Movie?): Boolean {
                    Log.d("MovieFlatBinder", "OnItemLongClick trigger")

                    realm.executeTransaction {
                        val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2).findAll()
                        if (results.size > 0) {
                            Log.d("MovieFlatBinder", "MyList is not empty -> updates List")
                            val List = results[0]
                            List.results.remove(movie)
                            List.total_results--
                            adapter.removeMovie(movie)
                        }
                    }
                    if (movie != null) {
                        mainActivity.removeFromFavorite(movie)
                    }
                    return true
                }
            })
        }


    }

}