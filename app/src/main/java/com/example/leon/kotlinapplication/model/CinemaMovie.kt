package com.example.leon.kotlinapplication.model

/**
 * Created by Leon on 06.06.17.
 */
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class CinemaMovie :RealmObject(){

    @PrimaryKey
    var id: Int = 0
    var video: Boolean = false
    var vote_average: Double = 0.0
    var vote_count: Int = 0
    var title: String = ""
    var popularity: Double = 0.0
    var poster_path: String =""
    var original_language:String = "en"
    var original_title:String = ""
    var backdrop_path: String = ""
    var adult:Boolean = false
    var overview: String = ""
    var budget: Int=0
    var homepage: String = ""
    var imdb_id: String = ""
    var release_date: String = ""
    var revenue: Int = 0
    var runtime: Int = 0
    var status: String = ""
    var tagline: String = ""

}