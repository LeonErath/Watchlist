package com.leon.app.watchlist.binder


import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ahamed.multiviewadapter.SelectableBinder
import com.ahamed.multiviewadapter.SelectableViewHolder
import com.leon.app.watchlist.R
import com.leon.app.watchlist.activities.GenreActivity
import com.leon.app.watchlist.activities.GenreDetailActivity
import com.leon.app.watchlist.model.Genre

/**
 * Created by Leon on 05.07.17.
 */
open class GenreBinder(activity: GenreActivity) : SelectableBinder<Genre, GenreBinder.ViewHolder>() {

    val TAG = GenreBinder::class.java.simpleName!!

    val genreActivity: GenreActivity = activity

    override fun bind(holder: ViewHolder?, item: Genre?, isSelected: Boolean) {
        holder?.textView?.text = item?.name
    }


    override fun canBindData(item: Any?): Boolean {
        return item is Genre
    }

    override fun create(inflater: LayoutInflater?, parent: ViewGroup?): ViewHolder {
        return ViewHolder(inflater?.inflate(R.layout.genre_item, parent, false)!!, genreActivity)
    }

    override fun getSpanSize(maxSpanCount: Int): Int {
        return 1
    }

    class ViewHolder(itemView: View, activity: GenreActivity) : SelectableViewHolder<Genre>(itemView) {

        val activity: GenreActivity = activity
        val context: Context = itemView.context
        var textView: TextView
        var cardView: CardView


        init {
            textView = itemView.findViewById(R.id.textViewGenre) as TextView
            cardView = itemView.findViewById(R.id.cardView) as CardView

            cardView.setOnClickListener {
                val intent = Intent(context, GenreDetailActivity::class.java)
                intent.putExtra("id", item.id)
                activity.startActivity(intent)
            }

        }


    }


}