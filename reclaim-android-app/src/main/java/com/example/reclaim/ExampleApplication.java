package com.example.reclaim;

import android.app.Application;

import com.isalldigital.reclaim.AdapterDelegatesManager;


public class ExampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AdapterDelegatesManager.init();
    }
}
