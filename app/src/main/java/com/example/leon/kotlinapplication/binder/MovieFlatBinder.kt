package com.example.leon.kotlinapplication.binder

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.leon.kotlinapplication.model.List
import com.example.leon.kotlinapplication.model.Movie
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmResults


open class MovieFlatBinder : SelectableBinder<Movie, MovieFlatBinder.ViewHolder>() {

    override fun bind(holder: ViewHolder?, movie: Movie?, p2: Boolean) {
        if (holder != null && movie != null) {
            holder.tvMovie.text = movie.title
            holder.tvOverview.text = movie.overview
            holder.tvRevenue.text = movie.revenue.toString()
            holder.tvScore.text = movie.popularity.toString()
            holder.tvDate.text = movie.release_date

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
        return ViewHolder(inflater?.inflate(R.layout.movie_item_flat, parent, false)!!)
    }

    override fun getSpanSize(maxSpanCount: Int): Int {
        return 1
    }

    class ViewHolder(itemView: View) : SelectableViewHolder<Movie>(itemView) {

        val context: Context = itemView.context

        var tvMovie: TextView
        var tvOverview: TextView
        var tvRevenue: TextView
        var tvScore: TextView
        var tvDate: TextView


        var imageMovie: ImageView


        init {
            tvMovie = itemView.findViewById(R.id.textViewMovie) as TextView
            tvOverview = itemView.findViewById(R.id.textViewOverview) as TextView
            tvRevenue = itemView.findViewById(R.id.textViewRevenue) as TextView
            tvScore = itemView.findViewById(R.id.textViewScore) as TextView
            tvDate = itemView.findViewById(R.id.textViewDate) as TextView

            imageMovie = itemView.findViewById(R.id.imageMovie) as ImageView


            setItemClickListener { view, item ->
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movieid", item.id)
                context.startActivity(intent)
            }

            setItemLongClickListener(object : OnItemLongClickListener<Movie> {
                override fun onItemLongClick(view: View?, movie: Movie?): Boolean {
                    Log.d("MovieFlatBinder", "OnItemLongClick trigger")

                    Realm.init(context)
                    val realm: Realm = Realm.getDefaultInstance()
                    realm.executeTransaction {
                        val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2).findAll()
                        if (results.size > 0) {
                            Log.d("MovieFlatBinder", "MyList is not empty -> updates List")
                            val List = results[0]
                            List.results.remove(movie)
                            List.total_results--
                        }
                    }

                    return false
                }
            })
        }


    }

}