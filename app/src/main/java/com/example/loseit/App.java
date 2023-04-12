package com.example.loseit;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.example.loseit.utils.CrashHandlers;
import com.example.loseit.utils.SPUtils;

import org.xutils.x;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SPUtils.init(this);
        CrashHandlers crashHandlers = CrashHandlers.getInstance();
        crashHandlers.init(this);
        x.Ext.init(this);
    }
}
