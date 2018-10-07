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
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.DetailActivity
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

open class MovieBinder2 : SelectableBinder<Movie, MovieBinder2.ViewHolder>() {


    override fun bind(holder: ViewHolder?, movie: Movie?, b: Boolean) {

        if (holder != null) {
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
        return ViewHolder(inflater?.inflate(R.layout.movie_item2, parent, false)!!)
    }

    override fun getSpanSize(maxSpanCount: Int): Int {
        return 2
    }

    class ViewHolder(itemView: View) : SelectableViewHolder<Movie>(itemView) {


        val context: Context = itemView.context


        var imageV: ImageView = itemView.findViewById(R.id.imageView) as ImageView
        var cardView: CardView = itemView.findViewById(R.id.cardView) as CardView
        var realm: Realm by Delegates.notNull()

        init {

            cardView.setOnClickListener({
                Log.d("MovieBinder", "CardView ClickListener trigger")
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movieid", item.id)
                context.startActivity(intent)

            })
            cardView.setOnLongClickListener {
                Log.d("MovieBinder", "OnItemLongClick trigger")

                Realm.init(context)
                realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2 as Int).findAll()
                    if (results.size > 0) {
                        Log.d("MovieBinder", "MyList is not empty -> updates List")
                        val List = results[0]
                        val check = List!!.results.any { item!!.id == it.id }

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


                }

                true
            }
        }


    }


}