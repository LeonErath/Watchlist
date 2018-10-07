package com.leon.app.watchlist

import android.util.Log
import org.json.JSONObject

/**
 * Created by Leon on 13.06.17.
 */

abstract class MyJsonParser {

    fun String.makeArray(): String {
        return "[$this]"
    }

    fun String.makeGenreArray(): String {
        val positionStart: Int = this.indexOf("[")
        val positionEnd: Int = this.indexOf("]") + 1
        val substring: String = this.substring(positionStart, positionEnd)
        Log.i("MyJsonParser", this)
        return substring
    }

    fun String.insertValueString(key: String, value: String): String {
        val position: Int = this.indexOf("{") + 1
        val substringBefore: String = this.substring(0, position)
        val substringEnd: String = this.substring(position)
        return "$substringBefore\"$key\":\"$value\",$substringEnd"
    }

    fun String.parseRecommendation(id: Int): String {
        val jsonObject = JSONObject(this)
        val array = jsonObject.getJSONArray("results")

        return "{id:$id,recommendations:$array }"
    }

    fun String.parsePersonMovies(id: Int): String {
        val jsonObject = JSONObject(this)
        val array = jsonObject.getJSONArray("cast")

        return "{id:$id,movies:$array }"
    }



    fun String.parseGenreMovies(): String {
        val jsonObject = JSONObject(this)
        jsonObject.put("id", 6)
        return "$jsonObject"
    }

    fun String.insertValueInt(key: String, value: Int): String {
        val position: Int = this.indexOf("{") + 1
        val substringBefore: String = this.substring(0, position)
        val substringEnd: String = this.substring(position)

        return "$substringBefore\"$key\":$value,$substringEnd"
    }

}

