package com.example.leon.kotlinapplication.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ahamed.multiviewadapter.BaseViewHolder
import com.ahamed.multiviewadapter.ItemBinder
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.model.Movie
import android.widget.TextView



/**
 * Created by Leon on 07.06.17.
 */

open class MovieBinder: ItemBinder<Movie, MovieBinder.ViewHolder>() {


    override fun bind(holder: ViewHolder?, movie: Movie?) {
        holder?.tvMovie!!.text = movie?.name
        holder?.tvYear!!.text = movie?.year.toString()

    }


    override fun canBindData(item: Any?): Boolean {
        return item is Movie;
    }

    override fun create(inflater: LayoutInflater?, parent: ViewGroup?): ViewHolder {
        return ViewHolder(inflater?.inflate(R.layout.movie_item, parent, false)!!);
    }

    class ViewHolder(itemView: View) : BaseViewHolder<Movie>(itemView) {

         var tvMovie: TextView? = null
         var tvYear: TextView? = null

        init {
            tvMovie = itemView.findViewById(R.id.textViewMovie) as TextView
            tvYear = itemView.findViewById(R.id.textViewYear) as TextView

        }
    }

}