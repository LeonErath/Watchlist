package com.leon.app.watchlist.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Leon on 15.06.17.
 */
open class Casting : RealmObject() {

    var cast_id: Int = 0
    var character: String = ""
    var credit_id: String = ""
    var gender: Int = 0
    @PrimaryKey
    var id: Int = 0
    var name: String = ""
    var order: Int = 0
    var profile_path: String? = ""


}