package com.leon.app.watchlist

import com.leon.app.watchlist.model.Movie

/**
 * Created by Leon on 03.07.17.
 */
data class MovieEventAdd(val movie: Movie)

data class MovieEventRemove(val movie: Movie)

data class DetailsLoaded(val movie: Movie)
