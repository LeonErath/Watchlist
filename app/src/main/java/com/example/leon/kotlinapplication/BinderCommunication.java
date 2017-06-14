package com.example.leon.kotlinapplication;

import com.example.leon.kotlinapplication.model.Movie;

/**
 * Created by Leon on 14.06.17.
 */

public interface BinderCommunication {
    void addToFavorites(Movie movie);

    void removeFromFavorites(Movie movie);
}
