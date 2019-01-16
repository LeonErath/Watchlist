package com.leon.app.watchlist

import android.util.Log
import com.leon.app.watchlist.model.Genre
import com.leon.app.watchlist.model.List
import com.leon.app.watchlist.model.Movie
import com.leon.app.watchlist.model.Person
import io.realm.Realm

/**
 * Created by Leon on 05.07.17.
 */
abstract class Fetcher : MyJsonParser() {

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

    private fun String.toPerson(): String {
        return this.makeArray()
    }

    private fun String.toPersonMovies(id: Int): String {
        return this.parsePersonMovies(id).makeArray()
    }

    private fun String.toGenreMovies(): String {
        return this.makeArray()
    }

    fun String.toRecommendation(id: Int): String {
        return this.parseRecommendation(id)
    }


    fun fetch(realm: Realm, type: Int, response: String, id: Int = 0) {
        realm.executeTransactionAsync({ bgRealm ->
            with(bgRealm) {
                when (type) {
                    // Updates Popular Movie List
                    0 -> createOrUpdateAllFromJson(List::class.java, response.toPopular())
                    // Updates Cinema Movie List
                    1 -> createOrUpdateAllFromJson(List::class.java, response.toCinema())
                    // Updates Recommendations
                    2 -> createOrUpdateObjectFromJson(Movie::class.java,response.toRecommendation(id))
                    // Updates Person
                    3 -> createOrUpdateAllFromJson(Person::class.java, response.toPerson())
                    // Updates PersonMovies
                    4 -> createOrUpdateAllFromJson(Person::class.java, response.toPersonMovies(id))
                    // Updates Genre List
                    5 -> createOrUpdateAllFromJson(Genre::class.java, response.toGenre())
                    // Updates Movies-from-Genre PList
                    6 -> createOrUpdateAllFromJson(List::class.java, response.toGenreMovies())
                    else -> Log.e("Fetcher.kt fetch()", "Nothing applied")

                }
            }
        }, {
            // Transaction was a success.
            Log.i("Fetcher.kt","fetch successfull")
            if(type == 2){
                completeDetail(id)
            }else{
                complete(type)
            }

        }) { e ->
            e.printStackTrace()
        }
    }

    abstract fun complete(type: Int)

    abstract fun completeDetail(id: Int)




    fun fetchRequest(realm: Realm, response: String) {
        realm.executeTransactionAsync({ bgRealm ->
            bgRealm.createOrUpdateObjectFromJson(Movie::class.java, response)
        }, {
            // Transaction was a success.
        }) { e ->
            e.printStackTrace()
        }
    }

}