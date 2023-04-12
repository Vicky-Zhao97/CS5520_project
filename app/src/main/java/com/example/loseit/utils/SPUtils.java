package com.example.loseit.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.loseit.App;
import com.example.loseit.R;

public class SPUtils {
    public static Application context;
    public static SharedPreferences sharedPreferences;

    public static void init(Application context){
        SPUtils.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
    }

    public static void putString(String key,String value){
        sharedPreferences.edit().putString(key,value).apply();
    }

    public static String getString(String key,String defVal){
        return sharedPreferences.getString(key,defVal);
    }
}
