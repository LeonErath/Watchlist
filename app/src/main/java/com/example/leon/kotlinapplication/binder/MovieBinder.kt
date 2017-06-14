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
import android.widget.TextView
import com.ahamed.multiviewadapter.SelectableBinder
import com.ahamed.multiviewadapter.SelectableViewHolder
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.DetailActivity
import com.example.leon.kotlinapplication.activities.MainActivity
import com.example.leon.kotlinapplication.model.List
import com.example.leon.kotlinapplication.model.Movie
import com.squareup.picasso.Picasso
import io.realm.Realm
import io.realm.RealmResults


/**
 * Created by Leon on 07.06.17.
 */

open class MovieBinder(activity: MainActivity) : SelectableBinder<Movie, MovieBinder.ViewHolder>() {

    val mainActivity: MainActivity

    init {
        this.mainActivity = activity
    }

    override fun bind(holder: ViewHolder?, movie: Movie?, b: Boolean) {
        holder?.tvMovie!!.text = movie?.title

        val uri: Uri = Uri
                .parse(holder.context.getString(R.string.image_base_url)
                        + "/w342"
                        + movie?.poster_path)
        Picasso.with(holder.context)
                .load(uri)
                .into(holder.imageV)
    }


    override fun canBindData(item: Any?): Boolean {
        return item is Movie
    }

    override fun create(inflater: LayoutInflater?, parent: ViewGroup?): ViewHolder {
        return ViewHolder(inflater?.inflate(R.layout.movie_item, parent, false)!!, mainActivity)
    }

    override fun getSpanSize(maxSpanCount: Int): Int {
        return 2
    }

    class ViewHolder(itemView: View, activity: MainActivity) : SelectableViewHolder<Movie>(itemView) {

        val mainActivity: MainActivity
        val context: Context = itemView.context

        var tvMovie: TextView
        var imageV: ImageView
        var cardView: CardView

        init {
            this.mainActivity = activity
            tvMovie = itemView.findViewById(R.id.textViewMovie) as TextView
            imageV = itemView.findViewById(R.id.imageView) as ImageView
            cardView = itemView.findViewById(R.id.cardView) as CardView

            cardView.setOnClickListener({
                Log.d("MovieBinder", "CardView ClickListener trigger")
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movieid", item.id)
                context.startActivity(intent)

            })
            cardView.setOnLongClickListener(object : View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    Log.d("MovieBinder", "OnItemLongClick trigger")

                    Realm.init(context)
                    val realm: Realm = Realm.getDefaultInstance()
                    realm.executeTransaction {
                        val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2).findAll()
                        if (results.size > 0) {
                            Log.d("MovieBinder", "MyList is not empty -> updates List")
                            val List = results[0]
                            var check = false
                            for (movie2 in List.results) {
                                if (item!!.id == movie2.id) check = true
                            }

                            if (!check) {
                                List.results.add(item)
                                List.total_results++
                            }

                        } else {
                            Log.d("MovieBinder", "MyList is empty -> creates new List")
                            val List = List()
                            List.id = 2
                            List.name = "MyList"
                            List.results.add(item)
                            List.total_results++
                            realm.copyToRealmOrUpdate(List)
                        }
                        mainActivity.addToFavorite(item)

                    }

                    return true
                }
            })
        }


    }

}