package com.example.chai.sharetrip;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by chai on 2017/11/01.
 */

//realmの設定処理がアプリケーション起動時に実行されるため -> p188
public class ShareTripApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
