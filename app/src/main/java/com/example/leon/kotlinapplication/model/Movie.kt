package com.example.leon.kotlinapplication.model

/**
 * Created by Leon on 06.06.17.
 */
import io.realm.RealmObject
import io.realm.annotations.RealmClass

@RealmClass
open class Movie:RealmObject(){
    var name:String = ""
    var year:Int? = null
    var director:String? = null
    var rated:String? = null
    var runtime:Int? = null


}