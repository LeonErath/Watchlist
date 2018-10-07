package com.leon.app.watchlist

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.leon.app.watchlist.model.List
import com.leon.app.watchlist.model.Movie
import com.leon.app.watchlist.model.Person
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmQuery
import io.realm.RealmResults
import java.util.*


/**
 * Created by Leon on 15.06.17.
 */
class QueryAdapter(context: Context) : com.leon.app.watchlist.Fetcher() {

    var realm: Realm
    val TAG = QueryAdapter::class.java.simpleName!!
    var detailLoaded: DetailLoadedListener? = null
    var loadData: LoadData? = null

    var c: Context

    init {
        Realm.init(context)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        realm = Realm.getInstance(config)
        this.c = context
    }


    fun setOnLoadedListener2(detailLoadedListener: DetailLoadedListener) {
        this.detailLoaded = detailLoaded
    }

    fun getPerson(id: Int, loadData: LoadData) {
        this.loadData = loadData
        val queue = Volley.newRequestQueue(c)
        val url = c.getString(R.string.base_url) +
                "person/$id?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US"

        // Fetcher a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            Log.i(TAG, "Response:" + response)
            fetch(realm, 3, response)

        }, Response.ErrorListener { error ->
            error.printStackTrace()
        })

        val url2 = c.getString(R.string.base_url) +
                "person/$id/movie_credits?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US"

        // Fetcher a string response from the provided URL.
        val stringRequest2 = StringRequest(Request.Method.GET, url2, Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            Log.i(TAG, "Response:" + response)
            fetch(realm, 4, response, id = id)

        }, Response.ErrorListener { error ->
            error.printStackTrace()
        })

        queue.add(stringRequest)
        queue.add(stringRequest2)
    }

    fun movieClickDetail(movie: Movie) {
        when (movie.evolution) {
            0 -> addToWatchlist(movie)
            1 -> addToWatched(movie)
        }
        Bus.send(MovieEventAdd(movie))
    }

    fun removeClickDetail(movie: Movie) {
        when (movie.evolution) {
            1 -> removeFromWatchlist(movie)
            2 -> removeWatched(movie)
        }
        Bus.send(MovieEventRemove(movie))
    }

    private fun addToWatched(movie: Movie) {
        realm.executeTransaction {
            if (movie.evolution == 1) {
                movie.evolution++
                val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 3 as Int).findAll()


                if (results.size > 0) {
                    Log.d(TAG, "WatchedList is not empty -> updates List")
                    val List = results[0]
                    if (List != null){
                        val check = List.results.any { movie!!.id == it.id }

                        if (!check) {
                            List.results.add(movie)
                            List.total_results++
                        }
                    }


                } else {
                    Log.d(TAG, "WatchedList is empty -> creates new List")
                    val List = List()
                    List.id = 3
                    List.name = "WatchedList"
                    List.results.add(movie)
                    List.total_results++
                    realm.copyToRealmOrUpdate(List)
                }
            }
        }
    }

    private fun removeWatched(movie: Movie) {
        realm.executeTransaction {
            movie.evolution--
            val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 3 as Int).findAll()
            if (results.size > 0) {
                Log.d(TAG, "WatchedList is not empty -> updates List")
                val List = results[0]
                if(List != null){
                    List.results.remove(movie)
                    List.total_results--
                }

            }
        }
    }

    fun getDetail(id: Int): Movie {
        Log.i(TAG, "Trying to get movie details..")
        val url = c.getString(R.string.base_url) +
                "movie/$id?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US"

        val urlCast = c.getString(R.string.base_url) +
                "movie/$id/credits?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US"

        val urlTrailer = c.getString(R.string.base_url) +
                "movie/$id/videos?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US"

        val urlRecom = c.getString(R.string.base_url) +
                "movie/$id/recommendations?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US"

        httpRequestDetail(url, urlCast, urlTrailer, urlRecom, id)

        return findMovie(id)
    }

    fun getCinema(page: Int, loadData: LoadData) {
        this.loadData = loadData
        val queue = Volley.newRequestQueue(c)
        val url = c.getString(R.string.base_url) +
                "movie/now_playing?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US&page=$page"

        // Fetcher a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            Log.i(TAG, "Response:" + response)
            fetch(realm, 1, response)
            //fetchRequestCinema(response, loadData)
        }, Response.ErrorListener { error ->
            error.printStackTrace()
        })

        queue.add(stringRequest)
    }

    fun getPopular(page: Int, loadData: LoadData) {
        this.loadData = loadData
        val queue = Volley.newRequestQueue(c)
        val url = c.getString(R.string.base_url) +
                "movie/popular?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US&page=$page"

        // Fetcher a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            Log.i(TAG, "Response:" + response)
            fetch(realm, 0, response)

        }, Response.ErrorListener { error ->
            error.printStackTrace()
        })

        queue.add(stringRequest)
    }

    fun getGenre(loadData: LoadData) {
        this.loadData = loadData
        // https://api.themoviedb.org/3/genre/movie/list?api_key=df38fba2447615a58400c89be4c98032&language=en-US
        val queue = Volley.newRequestQueue(c)
        val url = c.getString(R.string.base_url) +
                "genre/movie/list?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US"

        // Fetcher a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            Log.i(TAG, "Response:" + response)
            fetch(realm, 5, response)
        }, Response.ErrorListener { error ->
            error.printStackTrace()
        })

        queue.add(stringRequest)
    }

    fun getGenreMovie(genreid: Int, loadData: LoadData) {
        this.loadData = loadData
        // https://api.themoviedb.org/3/genre/movie/list?api_key=df38fba2447615a58400c89be4c98032&language=en-US
        val queue = Volley.newRequestQueue(c)
        val url = c.getString(R.string.base_url) +
                "genre/$genreid/movies?api_key=" +
                c.getString(R.string.key) +
                "&language=en-US"

        // Fetcher a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            Log.i(TAG, "Response:" + response)
            fetch(realm, 6, response)
        }, Response.ErrorListener { error ->
            error.printStackTrace()
        })

        queue.add(stringRequest)
    }

    private fun removeFromWatchlist(movie: Movie) {
        realm.executeTransaction {
            movie.evolution--
            val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2 as Int).findAll()
            if (results.size > 0) {
                Log.d(TAG, "MyList is not empty -> updates List")
                val List = results[0]
                List!!.results.remove(movie)
                List!!.total_results--
            }
        }
    }

    private fun addToWatchlist(movie: Movie) {
        Log.i(TAG, "${movie.title} added to Watchlist")
        realm.executeTransaction {
            val time: Long = Calendar.getInstance().timeInMillis
            movie.evolution++
            movie.timeAdded = time
            val results: RealmResults<List> = realm.where(List::class.java).equalTo("id", 2 as Int).findAll()
            if (results.size > 0) {

                val List = results[0]
                val check = List!!.results.any { movie!!.id == it.id }

                if (!check) {
                    List.results.add(movie)
                    List.total_results++
                }

            } else {

                val List = List()
                List.id = 2
                List.name = "MyList"
                List.results.add(movie)
                List.total_results++
                realm.copyToRealmOrUpdate(List)
            }

            getDetail(movie.id)
        }

    }

    private fun httpRequestDetail(url: String, urlCast: String, urlTrailer: String, urlRecom: String, id: Int) {

        val queue = Volley.newRequestQueue(c)

        val stringRequestRecom = StringRequest(Request.Method.GET, urlRecom, Response.Listener<String> { response ->
            Log.d(TAG, "Recommendation " + response)

            fetchRequestRecom(realm, response.toRecommendation(id), id)
        }, Response.ErrorListener { error -> error.printStackTrace() })


        queue.add(makeRequest(url))
        queue.add(makeRequest(urlCast))
        queue.add(makeRequest(urlTrailer))
        queue.add(stringRequestRecom)
    }

    private fun makeRequest(url: String): StringRequest {
        val stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            Log.d(TAG, "URL/URLCast/URLTrailer " + response)

            fetchRequest(realm, response)
        }, Response.ErrorListener { error -> error.printStackTrace() })
        return stringRequest
    }

    private fun findMovie(movieid: Int): Movie {
        val query: RealmQuery<Movie> = realm.where(Movie::class.java)
        val results: RealmResults<Movie> = query.equalTo("id", movieid).findAll()
        return results.first()!!
    }

    fun findPerson(personid: Int): Person {
        val query: RealmQuery<(Person)> = realm.where(Person::class.java)
        val results: RealmResults<Person> = query.equalTo("id", personid).findAll()
        return results.first()!!
    }

    override fun complete(type: Int) {
        if (loadData != null) {
            loadData?.update(type)
        }

    }

    override fun completeDetail(id: Int) {
        Bus.send(DetailsLoaded(findMovie(id)))
    }


}

interface LoadData {
    fun update(type: Int)
}

interface DetailLoadedListener {
    fun complete(movie: Movie)
}