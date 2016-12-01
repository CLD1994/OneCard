package com.zhuoli.onecard.data.source.local.database;


import android.text.TextUtils;

import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.model.SSID;
import com.zhuoli.onecard.data.source.local.LocalDataSource;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by CLD on 2016/7/18 0018.
 */
public class RealmConsole implements LocalDataSource{
	private final Realm mRealm;

	public RealmConsole(Realm realm) {
		mRealm = realm;
	}

	@Override
	public RealmAsyncTask saveOrUpdateSSID(final String ssid, final String name, final DataSource.Callback<Boolean> callback) {
		return mRealm.executeTransactionAsync(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				SSID ssidObject = new SSID();
				ssidObject.setSsid(ssid);
				if (!TextUtils.isEmpty(name)) {
					ssidObject.setName(name);
				}else {
					ssidObject.setName(ssid);
				}
				realm.copyToRealmOrUpdate(ssidObject);
			}
		}, new Realm.Transaction.OnSuccess() {
			@Override
			public void onSuccess() {
				callback.onDataLoad(true);
			}
		});
	}

	@Override
	public void selectSSID(final String ssid, final DataSource.Callback<SSID> callback) {
		final SSID ssidObject = mRealm.where(SSID.class).equalTo("ssid", ssid).findFirstAsync();
		ssidObject.addChangeListener(new RealmChangeListener<SSID>() {
			@Override
			public void onChange(SSID element) {
				callback.onDataLoad(element);
				ssidObject.removeChangeListeners();
			}
		});

	}

	@Override
	public SSID selectSSID(String ssid) {
		return mRealm.where(SSID.class).equalTo("ssid",ssid).findFirst();
	}

	@Override
	public void selectAllSSID(final DataSource.Callback<List<SSID>> callback) {
		RealmResults<SSID> realmResults = mRealm.where(SSID.class).findAllAsync();
		realmResults.addChangeListener(new RealmChangeListener<RealmResults<SSID>>() {
			@Override
			public void onChange(RealmResults<SSID> element) {
				callback.onDataLoad(element);
			}
		});
	}
}
