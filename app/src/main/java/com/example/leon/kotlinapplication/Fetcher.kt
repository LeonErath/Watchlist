package com.example.leon.kotlinapplication

import com.example.leon.kotlinapplication.model.Genre
import com.example.leon.kotlinapplication.model.List
import com.example.leon.kotlinapplication.model.Movie
import io.realm.Realm

/**
 * Created by Leon on 05.07.17.
 */
abstract class Fetcher {

    private fun popular(response: String): String {
        return jsonParser(response)
                .insertValueInt("id", 0)
                .insertValueString("name", "popularMovies")
                .makeArray().json
    }

    private fun cinema(response: String): String {
        return jsonParser(response)
                .insertValueInt("id", 1)
                .insertValueString("name", "cinemaMovies")
                .makeArray().json
    }

    private fun genre(response: String): String {
        return jsonParser(response).makeGenreArray().json
    }

    private fun genreMovies(response: String): String {
        return jsonParser(response)
                .makeArray().json
    }

    fun recommendation(response: String, id: Int): String {
        return jsonParser(response).parseRecommendation(id)
    }

    fun fetch(realm: Realm, type: Int, reposonse: String) {
        realm.executeTransactionAsync({ bgRealm ->
            with(bgRealm) {
                when (type) {
                // Updates Popular Movie List
                    0 -> {
                        createOrUpdateAllFromJson(List::class.java, popular(reposonse))
                    }
                // Updates Cinema Movie List
                    1 -> {
                        createOrUpdateAllFromJson(List::class.java, cinema(reposonse))
                    }
                    2 -> {

                    }
                    3 -> {

                    }
                    4 -> {

                    }
                // Updates Genre List
                    5 -> {
                        createOrUpdateAllFromJson(Genre::class.java, genre(reposonse))
                    }
                // Updates Movies-from-Genre PList
                    6 -> {
                        createOrUpdateAllFromJson(List::class.java, genreMovies(reposonse))
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