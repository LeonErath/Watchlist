package com.example.leon.kotlinapplication

/**
 * Created by Leon on 13.06.17.
 */

class jsonParser(jsonString: String) {
    var json: String = ""

    init {
        json = jsonString
    }


    fun makeArray(): jsonParser {
        json = "[" + json + "]"
        return this
    }

    fun insertValueString(key: String, value: String): jsonParser {
        val postition: Int = json.indexOf("{") + 1
        val substringBefore: String = json.substring(0, postition)
        val substringEnd: String = json.substring(postition)

        json = substringBefore + "\"" + key + "\":" + "\"" + value + "\"," + substringEnd
        return this
    }

    fun insertValueInt(key: String, value: Int): jsonParser {
        val postition: Int = json.indexOf("{") + 1
        val substringBefore: String = json.substring(0, postition)
        val substringEnd: String = json.substring(postition)

        json = substringBefore + "\"" + key + "\":" + value + "," + substringEnd
        return this
    }
}

