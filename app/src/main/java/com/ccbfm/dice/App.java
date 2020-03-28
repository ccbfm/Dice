package com.ccbfm.dice;

import android.app.Application;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SPTools.init(this);
    }
}
