package com.zhuoli.onecard.connect.usecase;

import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.model.SSID;
import com.zhuoli.onecard.data.source.remote.response.Status;

import io.realm.RealmAsyncTask;

/**
 * author: CLD
 * created on: 2016/10/26 0026 下午 9:55
 * description:
 */

public class SetCurrentSSID extends UseCase<SetCurrentSSID.RequestValues,SetCurrentSSID.ResponseValue>{

	private final DataSource mDataSource;

	private SetCurrentSSID.Cancelable mCancelable;

	public SetCurrentSSID(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new SetCurrentSSID.Cancelable();
	}

	@Override
	protected String getTag() {
		return SetCurrentSSID.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(final RequestValues requestValues) {
		mDataSource.setCurrentSSID(requestValues.SSID);
		mDataSource.selectSSID(requestValues.SSID, new DataSource.Callback<SSID>() {
			@Override
			public void onDataLoad(SSID data) {
				if (!data.isValid()){
					mCancelable.mRealmAsyncTask = mDataSource.saveOrUpdateSSID(requestValues.SSID, requestValues.name, new DataSource.Callback<Boolean>() {
						@Override
						public void onDataLoad(Boolean data) {

						}

						@Override
						public void onError(Status error) {

						}
					});
				}
			}

			@Override
			public void onError(Status error) {

			}
		});
		return mCancelable;
	}


	public static class RequestValues implements UseCase.RequestValues {
		private String SSID;
		private String name;

		public RequestValues(String SSID) {
			this.SSID = SSID;
		}

		public RequestValues(String SSID, String name) {
			this.SSID = SSID;
			this.name = name;
		}

		public String getSSID() {
			return SSID;
		}

		public void setSSID(String SSID) {
			this.SSID = SSID;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {

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
			return mRealmAsyncTask == null || mRealmAsyncTask.isCancelled();
		}

		@Override
		public void cancel() {
			if (mRealmAsyncTask != null && !mRealmAsyncTask.isCancelled()) {
				mRealmAsyncTask.cancel();
			}
		}
	}
}
