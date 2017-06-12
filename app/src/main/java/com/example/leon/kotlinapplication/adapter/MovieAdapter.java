package com.example.leon.kotlinapplication.adapter;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.example.leon.kotlinapplication.binder.MovieBinder;
import com.example.leon.kotlinapplication.model.Movie;

import java.util.List;

/**
 * Created by Leon on 08.06.17.
 */

public class MovieAdapter extends SelectableAdapter {

    private DataListManager<Movie> dataManager;

    public MovieAdapter() {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);

        registerBinder(new MovieBinder());
    }

    public void addData(List<Movie> dataList) {
        dataManager.addAll(dataList);
    }
}
