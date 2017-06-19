package com.example.leon.kotlinapplication

import android.util.Log
import org.json.JSONObject

/**
 * Created by Leon on 13.06.17.
 */

class jsonParser(jsonString: String) {
    var json: String = ""

    init {
        json = jsonString
        Log.d("jsonParser", jsonString)
    }


    fun makeArray(): jsonParser {
        json = "[$json]"
        return this
    }

    fun insertValueString(key: String, value: String): jsonParser {
        val position: Int = json.indexOf("{") + 1
        val substringBefore: String = json.substring(0, position)
        val substringEnd: String = json.substring(position)

        json = "$substringBefore\"$key\":\"$value\",$substringEnd"
        return this
    }

    fun parseRecommendation(id: Int): String {
        val jsonObject = JSONObject(json)
        val array = jsonObject.getJSONArray("results")

        return "{id=$id,recommendations=$array }"
    }

    fun insertValueInt(key: String, value: Int): jsonParser {
        val position: Int = json.indexOf("{") + 1
        val substringBefore: String = json.substring(0, position)
        val substringEnd: String = json.substring(position)

        json = "$substringBefore\"$key\":$value,$substringEnd"
        return this
    }
}

