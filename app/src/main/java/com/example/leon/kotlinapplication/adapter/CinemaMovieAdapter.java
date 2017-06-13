package com.example.leon.kotlinapplication.adapter;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.example.leon.kotlinapplication.binder.CinemaMovieBinder;
import com.example.leon.kotlinapplication.binder.PopularMovieBinder;
import com.example.leon.kotlinapplication.model.CinemaMovie;
import com.example.leon.kotlinapplication.model.PopularMovie;

import java.util.List;

/**
 * Created by Leon on 13.06.17.
 */

public class CinemaMovieAdapter extends SelectableAdapter {

    private DataListManager<CinemaMovie> dataManager;

    public CinemaMovieAdapter() {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);

        registerBinder(new CinemaMovieBinder());
    }

    public void addData(List<CinemaMovie> dataList) {
        dataManager.addAll(dataList);
    }
}
