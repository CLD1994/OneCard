package com.zhuoli.onecard.data.source.local;

import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.model.SSID;

import java.util.List;

import io.realm.RealmAsyncTask;

/**
 * Created by CLD on 2016/7/18 0018.
 */
public interface LocalDataSource {
	RealmAsyncTask saveOrUpdateSSID(String ssid, String name, DataSource.Callback<Boolean> callback);
	void selectSSID(String ssid, DataSource.Callback<SSID> callback);
	SSID selectSSID(String ssid);
	void selectAllSSID(DataSource.Callback<List<SSID>> callback);
}
