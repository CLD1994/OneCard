package com.zhuoli.onecard.setting.usecase;

import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.utils.CryptoUtils;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.util.concurrent.FutureTask;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class WriteSystemConfig extends UseCase<WriteSystemConfig.RequestValues,WriteSystemConfig.ResponseValue>{
	private final DataSource mDataSource;

	private final Cancelable mCancelable;

	public WriteSystemConfig(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return WriteSystemConfig.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(final WriteSystemConfig.RequestValues requestValues) {
		mDataSource.saveOrUpdateSSID(requestValues.ssid, requestValues.name, new DataSource.Callback<Boolean>() {
			@Override
			public void onDataLoad(Boolean ok) {
				if (ok){
					mCancelable.mFutureTask = mDataSource.execute(requestValues.getCommand(), new DataSource.Callback<byte[]>() {
						@Override
						public void onDataLoad(byte[] data) {
							try {
								getUseCaseCallback().onSuccess(new ResponseValue(data));
							} catch (IOException e) {
								if (!retry()){
									getUseCaseCallback().onError(new Status("NOK",e.getMessage()));
								}
							}
						}

						@Override
						public void onError(Status error) {
							getUseCaseCallback().onError(error);
						}
					});
				}else {
					getUseCaseCallback().onError(new Status("失败","保存SSID失败"));
				}
			}

			@Override
			public void onError(Status error) {
				getUseCaseCallback().onError(error);
			}
		});
		return mCancelable;
	}

	public static class RequestValues implements UseCase.RequestValues {
		private String ssid;
		private String name;
		private String address;

		public RequestValues(String ssid, String name, String address) {
			this.ssid = ssid;
			this.name = name;
			this.address = address;
		}

		byte[] getCommand(){
			String data = "03FFFF" + CryptoUtils.HEX.decToHexString(address,2);
			return Util.encodingCommand(SysConstant.WRITE_ADDRESS,data);
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {
		private String message;

		public ResponseValue(byte[] responseValue) throws IOException {
			if (Util.writeIsSucceed(responseValue)){
				message = "写入成功";
			}else {
				throw new IOException("失败,请重新请求!");
			}
		}

		public String getMessage() {
			return message;
		}
	}

	public static class Cancelable implements UseCase.Cancelable {

		private FutureTask mFutureTask;

		public Cancelable() {

		}

		public Cancelable(FutureTask futureTask) {
			mFutureTask = futureTask;
		}

		@Override
		public boolean isCanceled() {
			return mFutureTask.isCancelled();
		}

		@Override
		public void cancel() {
			if (mFutureTask != null)
			mFutureTask.cancel(true);
		}
	}
}
