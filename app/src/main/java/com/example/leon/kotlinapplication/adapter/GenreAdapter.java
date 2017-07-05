package com.example.leon.kotlinapplication.adapter;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.example.leon.kotlinapplication.activities.DetailActivity;
import com.example.leon.kotlinapplication.activities.GenreActivity;
import com.example.leon.kotlinapplication.binder.CastBinder;
import com.example.leon.kotlinapplication.binder.GenreBinder;
import com.example.leon.kotlinapplication.model.Casting;
import com.example.leon.kotlinapplication.model.Genre;

import java.util.List;

/**
 * Created by Leon on 05.07.17.
 */

public class GenreAdapter extends SelectableAdapter {
    private DataListManager<Genre> dataManager;

    public GenreAdapter(GenreActivity activity) {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);


        registerBinder(new GenreBinder(activity));
    }

    public void addData(List<Genre> dataList) {
        for (Genre genre : dataList) {
            if (!dataManager.contains(genre)) {
                dataManager.add(genre);
            }
        }
    }
}
