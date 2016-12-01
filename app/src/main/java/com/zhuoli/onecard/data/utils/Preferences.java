package com.zhuoli.onecard.data.utils;

import android.content.SharedPreferences;

/**
 * Created by CLD1994 on 2016/6/22.
 */
public class Preferences {

    private final SharedPreferences p;

    public Preferences(SharedPreferences sharedPreferences) {
        p = sharedPreferences;
    }

    public void setAdminFlag(boolean isAdmin){
        p.edit().putBoolean("isAdmin",isAdmin).apply();
    }

    public boolean isAdmin(){
        return p.getBoolean("isAdmin",false);
    }

    public void setCurrentSSID(String ssid){
        p.edit().putString("currentSSID",ssid).apply();
    }

    public String getCurrentSSID(){
        return p.getString("currentSSID","");
    }

}

