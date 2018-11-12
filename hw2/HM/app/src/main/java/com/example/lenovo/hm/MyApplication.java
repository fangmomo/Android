package com.example.lenovo.hm;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public Context getContext() {
        return context;
    }
}
