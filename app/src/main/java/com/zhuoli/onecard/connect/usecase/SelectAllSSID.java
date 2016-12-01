package com.zhuoli.onecard.connect.usecase;

import android.text.TextUtils;

import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.model.SSID;
import com.zhuoli.onecard.data.source.remote.response.Status;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmAsyncTask;

/**
 * author: CLD
 * created on: 2016/10/27 0027 下午 2:36
 * description:
 */

public class SelectAllSSID extends UseCase<SelectAllSSID.RequestValues,SelectAllSSID.ResponseValue>{

	private final DataSource mDataSource;

	private SelectAllSSID.Cancelable mCancelable;

	public SelectAllSSID(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new SelectAllSSID.Cancelable();
	}

	@Override
	protected String getTag() {
		return SelectAllSSID.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(final RequestValues requestValues) {
		mDataSource.selectAllSSID(new DataSource.Callback<List<SSID>>() {
			@Override
			public void onDataLoad(List<SSID> data) {
				getUseCaseCallback().onSuccess(new ResponseValue(data,requestValues.getSSIDList()));
			}

			@Override
			public void onError(Status error) {

			}
		});
		return mCancelable;
	}


	public static class RequestValues implements UseCase.RequestValues {
		private List<String> SSIDList;

		public RequestValues(List<String> SSIDList) {
			this.SSIDList = SSIDList;
		}

		public List<String> getSSIDList() {
			return SSIDList;
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {
		private List<SSID> SSIDList;

		public ResponseValue(List<SSID> SSIDList, List<String> str_SSIDList) {
			this.SSIDList = new ArrayList<>();
			for (SSID ssid : SSIDList) {
				if (str_SSIDList.contains(ssid.getSsid())){
					if (!TextUtils.isEmpty(ssid.getName())){
						str_SSIDList.remove(ssid.getSsid());
						this.SSIDList.add(ssid);
					}
				}
			}
			for (String str_ssid : str_SSIDList){
				SSID ssid = new SSID();
				ssid.setSsid(str_ssid);
				ssid.setName(str_ssid);
				this.SSIDList.add(ssid);
			}
		}

		public List<SSID> getSSIDList() {
			return SSIDList;
		}
	}

	public static class Cancelable implements UseCase.Cancelable {

		private RealmAsyncTask mRealmAsyncTask;

		public Cancelable() {

		}

		public Cancelable(RealmAsyncTask realmAsyncTask) {
			mRealmAsyncTask = realmAsyncTask;
		}

		@Override
		public boolean isCanceled() {
			if (mRealmAsyncTask != null){
				return mRealmAsyncTask.isCancelled();
			}
			return true;
		}

		@Override
		public void cancel() {
			if (mRealmAsyncTask != null && !mRealmAsyncTask.isCancelled()) {
				mRealmAsyncTask.cancel();
			}
		}
	}
}
