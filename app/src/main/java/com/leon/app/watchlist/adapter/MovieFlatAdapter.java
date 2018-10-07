package com.leon.app.watchlist.adapter;

import android.util.Log;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.leon.app.watchlist.activities.MainActivity;
import com.leon.app.watchlist.binder.MovieFlatBinder;
import com.leon.app.watchlist.model.Movie;

import java.util.List;

/**
 * Created by Leon on 13.06.17.
 */

public class MovieFlatAdapter extends SelectableAdapter {
    public DataListManager<Movie> dataManager;

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
        if (!dataManager.contains(movie)) {
            Log.i("MovieFlatAdapter", "Added");
            dataManager.add(movie);
            notifyItemInserted(dataManager.indexOf(movie));
        } else {
            Log.i("MovieFlatAdapter", "not added");
        }
    }

    public void removeMovie(Movie movie) {
        dataManager.remove(movie);
    }
}
