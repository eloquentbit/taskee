package com.eloquentbit.taskee;

import android.app.Application;
import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TaskeeApplication extends Application {

    private static TaskeeApplication application;

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);
        application = this;

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("taskee.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    public static TaskeeApplication getApplication() {
        return application;
    }

    public static Context getContext() {
        return application;
    }
}
