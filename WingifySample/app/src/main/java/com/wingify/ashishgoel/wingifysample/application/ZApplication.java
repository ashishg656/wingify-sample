package com.wingify.ashishgoel.wingifysample.application;

import android.app.Application;

/**
 * Created by Ashish Goel on 11/27/2015.
 */
public class ZApplication extends Application {


    static ZApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static ZApplication getInstance() {
        if (sInstance == null)
            sInstance = new ZApplication();
        return sInstance;
    }
}
