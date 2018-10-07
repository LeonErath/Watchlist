package com.leon.app.watchlist

/**
 * Created by Leon on 14.06.17.
 */
class dateParser(dateString: String) {

    var dateString: String

    init {
        this.dateString = dateString
    }

    fun parse(): dateParser {
        // 2015-06-18
        val year: String = dateString.substring(2, 4)
        val month: String = dateString.substring(5, 7)
        val day: String = dateString.substring(8)
        dateString = "$day.$month.$year"
        return this
    }


}