package com.example.leon.kotlinapplication.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Leon on 03.07.17.
 */
open class Genre : RealmObject() {
    @PrimaryKey
    var id: Int = 0
    var name: String = ""


}