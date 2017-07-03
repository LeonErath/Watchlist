package com.example.leon.kotlinapplication

import com.example.leon.kotlinapplication.model.Movie

/**
 * Created by Leon on 03.07.17.
 */
data class MovieEventAdd(val movie: Movie)

data class MovieEventRemove(val movie: Movie)
