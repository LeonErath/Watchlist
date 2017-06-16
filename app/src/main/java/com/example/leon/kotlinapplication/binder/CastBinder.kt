package com.example.leon.kotlinapplication.binder

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ahamed.multiviewadapter.SelectableBinder
import com.ahamed.multiviewadapter.SelectableViewHolder
import com.example.leon.kotlinapplication.R
import com.example.leon.kotlinapplication.activities.DetailActivity
import com.example.leon.kotlinapplication.model.Casting
import com.squareup.picasso.Picasso


/**
 * Created by Leon on 07.06.17.
 */

open class CastBinder(activity: DetailActivity) : SelectableBinder<Casting, CastBinder.ViewHolder>() {
    val TAG = CastBinder::class.java.simpleName

    val mainActivity: DetailActivity

    init {
        this.mainActivity = activity
    }


    override fun bind(holder: ViewHolder?, cast: Casting?, p2: Boolean) {
        if (holder != null) {
            if (cast != null) {
                holder.textViewName.text = cast.name
                val uri: Uri = Uri
                        .parse(holder.context.getString(R.string.image_base_url)
                                + "/w185"
                                + cast.profile_path)
                Picasso.with(holder.context)
                        .load(uri)
                        .into(holder.imageView)
            }
        }
    }

    override fun canBindData(item: Any?): Boolean {
        return item is Casting
    }

    override fun create(inflater: LayoutInflater?, parent: ViewGroup?): ViewHolder {
        return ViewHolder(inflater?.inflate(R.layout.cast_item, parent, false)!!, mainActivity)
    }

    override fun getSpanSize(maxSpanCount: Int): Int {
        return 1
    }

    class ViewHolder(itemView: View, activity: DetailActivity) : SelectableViewHolder<Casting>(itemView) {

        val activity: DetailActivity
        var textViewName: TextView
        var imageView: ImageView
        val context: Context = itemView.context

        init {
            this.activity = activity
            this.textViewName = itemView.findViewById(R.id.textViewName) as TextView
            this.imageView = itemView.findViewById(R.id.imageView) as ImageView

        }


    }


}