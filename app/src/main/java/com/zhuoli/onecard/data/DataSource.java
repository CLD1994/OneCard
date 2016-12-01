package com.zhuoli.onecard.data;

import com.zhuoli.onecard.data.model.SSID;
import com.zhuoli.onecard.data.source.remote.response.Status;

import java.util.List;
import java.util.concurrent.FutureTask;

import io.realm.RealmAsyncTask;

public interface DataSource {

    interface Callback<T> {

        void onDataLoad(T data);

        void onError(Status error);

    }

    FutureTask execute(byte[] commend, Callback<byte[]> callback);

    void setCurrentSSID(String ssid);

    String getCurrentSSID();

    RealmAsyncTask saveOrUpdateSSID(String ssid, String name, DataSource.Callback<Boolean> callback);

    void selectSSID(String ssid, DataSource.Callback<SSID> callback);

    SSID selectSSID(String ssid);

    void selectAllSSID(DataSource.Callback<List<SSID>> callback);
}
