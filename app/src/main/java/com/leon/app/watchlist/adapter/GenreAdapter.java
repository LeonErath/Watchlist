package com.leon.app.watchlist.adapter;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.leon.app.watchlist.activities.GenreActivity;
import com.leon.app.watchlist.binder.GenreBinder;
import com.leon.app.watchlist.model.Genre;

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
