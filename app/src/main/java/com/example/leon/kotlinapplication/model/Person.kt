package com.example.leon.kotlinapplication.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Leon on 07.07.17.
 */
open class Person : RealmObject() {

    @PrimaryKey
    var id: Int = 0
    var adult: Boolean = false
    var biography: String = ""
    var birthday: String = ""
    var deathday: String? = ""
    // 0 männlich 1 weiblich
    var gender: Int = 0
    var homepage: String? = ""
    var name: String = ""
    var place_of_birth: String = ""
    var popularity: Int = 0
    var profile_path: String? = ""
    var movies: RealmList<Movie> = RealmList()
}