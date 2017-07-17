package com.guo.android.architecture;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;



public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        initFresco();
    }

    private void initFresco() {
        Fresco.initialize(this);
    }
}
