package com.example.leon.kotlinapplication.adapter;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.example.leon.kotlinapplication.binder.PopularMovieBinder;
import com.example.leon.kotlinapplication.model.PopularMovie;

import java.util.List;

/**
 * Created by Leon on 08.06.17.
 */

public class PopularMovieAdapter extends SelectableAdapter {

    private DataListManager<PopularMovie> dataManager;

    public PopularMovieAdapter() {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);

        registerBinder(new PopularMovieBinder());
    }

    public void addData(List<PopularMovie> dataList) {
        dataManager.addAll(dataList);
    }
}
