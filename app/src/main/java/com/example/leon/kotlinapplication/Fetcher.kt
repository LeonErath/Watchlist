package com.example.leon.kotlinapplication

import com.example.leon.kotlinapplication.model.Genre
import com.example.leon.kotlinapplication.model.List
import com.example.leon.kotlinapplication.model.Movie
import io.realm.Realm

/**
 * Created by Leon on 05.07.17.
 */
abstract class Fetcher : jsonParser() {

    private fun String.toPopular(): String {
        return this.insertValueInt("id", 0)
                .insertValueString("name", "popularMovies")
                .makeArray()
    }

    private fun String.toCinema(): String {
        return this
                .insertValueInt("id", 1)
                .insertValueString("name", "cinemaMovies")
                .makeArray()
    }

    private fun String.toGenre(): String {
        return this.makeGenreArray()
    }

    private fun String.toGenreMovies(): String {
        return this.makeArray()
    }

    public fun String.toRecommendation(id: Int): String {
        return this.parseRecommendation(id)
    }


    fun fetch(realm: Realm, type: Int, response: String) {
        realm.executeTransactionAsync({ bgRealm ->
            with(bgRealm) {
                when (type) {
                // Updates Popular Movie List
                    0 -> {
                        createOrUpdateAllFromJson(List::class.java, response.toPopular())
                    }
                // Updates Cinema Movie List
                    1 -> {
                        createOrUpdateAllFromJson(List::class.java, response.toCinema())
                    }
                    2 -> {

                    }
                    3 -> {

                    }
                    4 -> {

                    }
                // Updates Genre List
                    5 -> {
                        createOrUpdateAllFromJson(Genre::class.java, response.toGenre())
                    }
                // Updates Movies-from-Genre PList
                    6 -> {
                        createOrUpdateAllFromJson(List::class.java, response.toGenreMovies())
                    }


                }
            }

        }, {
            // Transaction was a success.
            complete(type)
        }) {
            e ->
            e.printStackTrace()
        }


    }

    abstract fun complete(type: Int)


    fun fetchRequest(realm: Realm, response: String) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.createOrUpdateObjectFromJson(Movie::class.java, response)
        }
    }

}