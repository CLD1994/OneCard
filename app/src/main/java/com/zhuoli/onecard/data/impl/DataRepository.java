package com.zhuoli.onecard.data.impl;

import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.model.SSID;
import com.zhuoli.onecard.data.source.local.LocalDataSource;
import com.zhuoli.onecard.data.source.remote.RemoteDataSource;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.data.utils.Preferences;
import com.zhuoli.onecard.data.utils.SocketAsyncExecutor;

import java.util.List;
import java.util.concurrent.FutureTask;

import io.realm.RealmAsyncTask;

public class DataRepository implements DataSource {

    private final LocalDataSource mLocalDataSource;

    private final RemoteDataSource mRemoteDataSource;

    private final Preferences mPreferences;

    private final SocketAsyncExecutor mSocketAsyncExecutor;

    public DataRepository(LocalDataSource localDataSource, RemoteDataSource remoteDataSource, Preferences preferences) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
        mPreferences = preferences;
        mSocketAsyncExecutor = new SocketAsyncExecutor();
    }

    @Override
    public FutureTask execute(byte[] commend, final Callback<byte[]> callback) {
        return mSocketAsyncExecutor.execute(commend, new SocketAsyncExecutor.WorkerCallBack() {
            @Override
            public void onSucceed(byte[] bytes) {
                callback.onDataLoad(bytes);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(new Status("Exception",e.getMessage()));
            }
        });
    }


    @Override
    public void setCurrentSSID(String ssid) {
        mPreferences.setCurrentSSID(ssid);
    }

    @Override
    public String getCurrentSSID() {
        return mPreferences.getCurrentSSID();
    }

    @Override
    public RealmAsyncTask saveOrUpdateSSID(String ssid, String name, Callback<Boolean> callback) {
        return mLocalDataSource.saveOrUpdateSSID(ssid,name,callback);
    }

    @Override
    public void selectSSID(String ssid, Callback<SSID> callback) {
        mLocalDataSource.selectSSID(ssid,callback);
    }

    @Override
    public SSID selectSSID(String ssid) {
        return mLocalDataSource.selectSSID(ssid);
    }

    @Override
    public void selectAllSSID(Callback<List<SSID>> callback) {
        mLocalDataSource.selectAllSSID(callback);
    }
}