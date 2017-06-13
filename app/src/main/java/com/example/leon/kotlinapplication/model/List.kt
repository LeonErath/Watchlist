package com.example.leon.kotlinapplication.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Leon on 13.06.17.
 */

open class List : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var name: String = ""

    var results: RealmList<Movie> = RealmList<Movie>()
    var total_results: Int = 0
    var total_pages: Int = 0


}