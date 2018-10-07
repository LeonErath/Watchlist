package com.leon.app.watchlist.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Leon on 16.06.17.
 */
open class Trailers : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var key: String = ""
    var site: String = ""
    var name: String = ""
    var size: Int = 0
    var type: String = ""
}