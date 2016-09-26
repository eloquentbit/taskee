package com.eloquentbit.taskee.utils;


import android.support.annotation.NonNull;
import android.util.Log;

import com.eloquentbit.taskee.TaskeeApplication;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public final class RealmAutoIncrement {

    private static RealmAutoIncrement autoIncrementMap;

    private RealmAutoIncrement() {

        createAutoIncrementEntityIfNotExist();
    }

    /**
     * Search in AutoIncrementEntity for the last saved id from model passed and return the next one
     *
     * @param clazz Model which should get the next id
     * @return The next id which can be saved in database
     */
    public Integer getNextIdFromModel(Class<? extends RealmObject> clazz) {

        if (isValidMethodCall()) {

            Integer id = updateIdByClassName(clazz);
            Log.i("RealmAutoIncrement", "getNextIdFromModel: " + id);
            return id;
        }
        Log.e("RealmAutoIncrement", "getNextIdFromModel is called by a not valid method");
        return null;
    }

    private Integer updateIdByClassName(final Class<? extends RealmObject> clazz) {

        Realm realm = getRealm();
        final AutoIncrementEntity autoIncrementEntity = realm.where(AutoIncrementEntity.class).findFirst();

        if (realm.isInTransaction()) {

            autoIncrementEntity.incrementByClassName(clazz.getSimpleName());
            realm.copyToRealmOrUpdate(autoIncrementEntity);
        } else {

            realm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {

                    autoIncrementEntity.incrementByClassName(clazz.getSimpleName());
                    realm.copyToRealmOrUpdate(autoIncrementEntity);
                }
            });
        }
        return autoIncrementEntity.findByClassName(clazz.getSimpleName());
    }

    /**
     * Utility method to validate if the method is called from reflection,
     * in this case is considered a not valid call otherwise is a valid call
     *
     * @return The boolean which define if the method call is valid or not
     */
    private boolean isValidMethodCall() {

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        for (StackTraceElement stackTraceElement : stackTraceElements) {

            if (stackTraceElement.getMethodName().equals("newInstance")) {
                return false;
            }
        }
        return true;
    }

    private void createAutoIncrementEntityIfNotExist() {

        AutoIncrementEntity autoIncrementEntity = getRealm().where(AutoIncrementEntity.class).findFirst();

        if (autoIncrementEntity == null) {

            createAutoIncrementEntity();
        }
    }

    private void createAutoIncrementEntity() {

        getRealm().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                AutoIncrementEntity autoIncrementEntity = new AutoIncrementEntity();
                realm.copyToRealm(autoIncrementEntity);
            }
        });
    }

    private Realm getRealm() {
        return Realm.getInstance(getRealmConfiguration());
    }

    @NonNull
    private RealmConfiguration getRealmConfiguration() {

        //TODO Change for your current RealmConfiguration
        return new RealmConfiguration.Builder(TaskeeApplication.getContext())
                .deleteRealmIfMigrationNeeded()
                .build();

    }

    public static RealmAutoIncrement getInstance() {

        if (autoIncrementMap == null) {

            autoIncrementMap = new RealmAutoIncrement();
        }
        return autoIncrementMap;
    }
}