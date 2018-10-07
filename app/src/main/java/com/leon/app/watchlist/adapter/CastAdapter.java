package com.leon.app.watchlist.adapter;

import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.SelectableAdapter;
import com.leon.app.watchlist.activities.DetailActivity;
import com.leon.app.watchlist.binder.CastBinder;
import com.leon.app.watchlist.model.Casting;

import java.util.List;

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
