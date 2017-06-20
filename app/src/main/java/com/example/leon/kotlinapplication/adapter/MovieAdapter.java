package com.example.leon.kotlinapplication.adapter;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.example.leon.kotlinapplication.activities.DetailActivity;
import com.example.leon.kotlinapplication.activities.MainActivity;
import com.example.leon.kotlinapplication.binder.MovieBinder;
import com.example.leon.kotlinapplication.binder.MovieBinder2;
import com.example.leon.kotlinapplication.model.Movie;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmConfiguration;

/**
 * Created by Leon on 08.06.17.
 */

public class MovieAdapter extends SelectableAdapter {


    private DataListManager<Movie> dataManager;

    public MovieAdapter(MainActivity activity) {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);


        registerBinder(new MovieBinder(activity));
    }

    public MovieAdapter(DetailActivity activity) {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);

        registerBinder(new MovieBinder2());
    }

    public void addData(List<Movie> dataList) {
        for (Movie movie : dataList) {
            if (!dataManager.contains(movie)) {
                dataManager.add(movie);
                notifyItemInserted(dataManager.getCount() - 1);
            }
        }
    }
}
