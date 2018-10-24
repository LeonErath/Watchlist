package com.leon.app.watchlist

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration

class RealmController(context: Context) {

    var  context:Context;
    var realm: Realm;

    init {
        Realm.init(context)
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        realm = Realm.getInstance(config)
        this.context = context
    }

}
