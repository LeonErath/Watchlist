package com.leon.app.watchlist.adapter;

import android.app.Activity;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.leon.app.watchlist.binder.MovieBinder;
import com.leon.app.watchlist.model.Movie;

import java.util.List;

/**
 * Created by Leon on 08.06.17.
 */

public class MovieAdapter extends SelectableAdapter {


    public DataListManager<Movie> dataManager;

    public MovieAdapter(Activity activity) {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);


        registerBinder(new MovieBinder(activity, this));
    }

    /*public MovieAdapter(DetailActivity activity) {
        this.dataManager = new DataListManager<>(this);
        addDataManager(dataManager);

        registerBinder(new MovieBinder2());
    }*/

    public void addData(List<Movie> dataList) {
        for (Movie movie : dataList) {
            if (!dataManager.contains(movie)) {
                dataManager.add(movie);
                notifyItemInserted(dataManager.getCount() - 1);
            }
        }
    }
}
