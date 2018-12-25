package com.biabiabia.effect;

import android.app.Application;
import android.content.Context;

public class LibApp extends Application {

    public static final String TAG = "LibApp";
    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }
}
