package com.example.leon.kotlinapplication.adapter;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.example.leon.kotlinapplication.activities.DetailActivity;
import com.example.leon.kotlinapplication.activities.MainActivity;
import com.example.leon.kotlinapplication.binder.CastBinder;
import com.example.leon.kotlinapplication.binder.MovieBinder;
import com.example.leon.kotlinapplication.model.Casting;
import com.example.leon.kotlinapplication.model.Movie;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmConfiguration;

/**
 * Created by Leon on 08.06.17.
 */

public class CastAdapter extends SelectableAdapter {


    private DataListManager<Casting> dataManager;

    public CastAdapter(DetailActivity activity) {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);


        registerBinder(new CastBinder(activity));
    }

    public void addData(List<Casting> dataList) {
        for (Casting cast : dataList) {
            if (!dataManager.contains(cast)) {
                dataManager.add(cast);
            }
        }
    }
}
