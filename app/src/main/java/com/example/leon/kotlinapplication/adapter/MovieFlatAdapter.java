package com.example.leon.kotlinapplication.adapter;

import android.util.Log;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.example.leon.kotlinapplication.activities.MainActivity;
import com.example.leon.kotlinapplication.binder.MovieBinder;
import com.example.leon.kotlinapplication.binder.MovieFlatBinder;
import com.example.leon.kotlinapplication.model.Movie;

import java.util.List;

/**
 * Created by Leon on 13.06.17.
 */

public class MovieFlatAdapter extends SelectableAdapter {
    private DataListManager<Movie> dataManager;

    public MovieFlatAdapter(MainActivity activity) {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);

        registerBinder(new MovieFlatBinder(this, activity));
    }

    public void addData(List<Movie> dataList) {
        for (Movie movie : dataList) {
            if (dataManager.getCount() > 0) {
                Boolean check = false;
                for (int i = 0; i < dataManager.getCount(); i++) {
                    Movie movie2 = dataManager.get(i);
                    if (movie.getId() == movie2.getId()) {
                        check = true;
                    }
                }
                if (!check) {
                    add(movie);
                }

            } else {
                add(movie);

            }
        }
    }

    public void add(Movie movie) {
        if (movie.getTagline() != "" && !dataManager.contains(movie)) {
            dataManager.add(movie);
            notifyItemInserted(dataManager.indexOf(movie));
        }
    }

    public void removeMovie(Movie movie) {
        dataManager.remove(movie);
    }
}
