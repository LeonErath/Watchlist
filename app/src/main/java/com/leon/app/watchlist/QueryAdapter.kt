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

    var realm: Realm;
    val TAG = QueryAdapter::class.java.simpleName!!
    var detailLoaded: DetailLoadedListener? = null
    var loadData: LoadData? = null

    var c: Context

    init {
        realm = RealmController(context).realm;
        this.c = context
    }


    fun setOnLoadedListener2(detailLoadedListener: DetailLoadedListener) {
        this.detailLoaded = detailLoadedListener
    }

    fun getURL(path: String, page: String = ""): String {
        val domain = c.getString(R.string.base_url)
        val API_KEY = c.getString(R.string.key)
        val language = "&language=en-US"
        return domain + path + API_KEY + "&language=en-US" + page
    }

    fun makeRequest(url: String, type: Int,id: Int = 0): StringRequest {
        return StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->
            // Display the first 500 characters of the response string.
            Log.i(TAG, "Response:" + response)
            fetch(realm, type, response,id)

        }, Response.ErrorListener { error ->
            Log.e("QueryAdapter.kt",""+error.printStackTrace())
        })
    }

    fun getPerson(id: Int, loadData: LoadData) {
        this.loadData = loadData
        val queue = Volley.newRequestQueue(c)

        val personDetail = getURL("person/$id?api_key=")
        val moviesFromPerson = getURL("person/$id/movie_credits?api_key=")

        val personDetailRequest = makeRequest(personDetail, 3)
        val movieFromPersonRequest = makeRequest(moviesFromPerson, 4)

        queue.add(personDetailRequest)
        queue.add(movieFromPersonRequest)
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
                    if (List != null) {
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
                if (List != null) {
                    List.results.remove(movie)
                    List.total_results--
                }

            }
        }
    }

    fun getDetail(id: Int): Movie {
        Log.i(TAG, "Trying to get movie details..")

        val url = getURL("movie/$id?api_key=")
        val urlCast = getURL("movie/$id/credits?api_key=")
        val urlTrailer = getURL("movie/$id/videos?api_key=")
        val urlRecom = getURL("movie/$id/recommendations?api_key=")

        httpRequestDetail(url, urlCast, urlTrailer, urlRecom, id)

        return findMovie(id)
    }

    fun getCinema(page: Int, loadData: LoadData) {
        this.loadData = loadData
        val queue = Volley.newRequestQueue(c)

        val url = getURL("movie/now_playing?api_key=", "page=$page")

        val stringRequest = makeRequest(url,1)

        queue.add(stringRequest)
    }

    fun getPopular(page: Int, loadData: LoadData) {
        this.loadData = loadData
        val queue = Volley.newRequestQueue(c)
        val url = getURL("movie/popular?api_key=", "page=$page")

        val stringRequest = makeRequest(url, 0)

        queue.add(stringRequest)
    }

    fun getGenre(loadData: LoadData) {
        this.loadData = loadData
        // https://api.themoviedb.org/3/genre/movie/list?api_key=df38fba2447615a58400c89be4c98032&language=en-US
        val queue = Volley.newRequestQueue(c)
        val url = getURL("genre/movie/list?api_key=")

        // Fetcher a string response from the provided URL.
        val stringRequest = makeRequest(url, 5)

        queue.add(stringRequest)
    }

    fun getGenreMovie(genreid: Int, loadData: LoadData) {
        this.loadData = loadData
        // https://api.themoviedb.org/3/genre/movie/list?api_key=df38fba2447615a58400c89be4c98032&language=en-US
        val queue = Volley.newRequestQueue(c)
        val url = getURL("genre/$genreid/movies?api_key=")

        // Fetcher a string response from the provided URL.
        val stringRequest = makeRequest(url, 6)

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

        queue.add(makeRequest(url))
        queue.add(makeRequest(urlCast))
        queue.add(makeRequest(urlTrailer))
        queue.add(makeRequest(urlRecom,2,id))
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