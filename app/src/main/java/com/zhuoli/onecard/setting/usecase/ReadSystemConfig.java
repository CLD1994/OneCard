package com.zhuoli.onecard.setting.usecase;

import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.model.SSID;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.util.concurrent.FutureTask;

import okio.ByteString;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class ReadSystemConfig extends UseCase<ReadSystemConfig.RequestValues,ReadSystemConfig.ResponseValue>{
	private final DataSource mDataSource;

	private Cancelable mCancelable;

	public ReadSystemConfig(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return ReadSystemConfig.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(final RequestValues requestValues) {
		final String str_ssid = mDataSource.getCurrentSSID();
		mDataSource.selectSSID(str_ssid, new DataSource.Callback<SSID>() {
			@Override
			public void onDataLoad(final SSID ssid) {

				mCancelable.mFutureTask = mDataSource.execute(requestValues.getCommand(), new DataSource.Callback<byte[]>() {
					@Override
					public void onDataLoad(byte[] data) {
						try {
							getUseCaseCallback().onSuccess(new ResponseValue(ssid.getSsid(),ssid.getName(),data));
						} catch (IOException e) {
							Logger.d(e.getMessage());
							if (!retry()){
								getUseCaseCallback().onError(new Status("NOK","请求失败"));
							}
						}
					}

					@Override
					public void onError(Status error) {
						getUseCaseCallback().onError(error);
					}
				});
			}

			@Override
			public void onError(Status error) {

			}
		});
		return mCancelable;
	}

	public static class RequestValues implements UseCase.RequestValues {
		byte[] getCommand(){
			return Util.encodingCommand(SysConstant.READ_ADDRESS,null);
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {
		private String ssid;
		private String name;
		private String address;

		public ResponseValue(String ssid, String name, byte[] address) throws IOException{
			this.ssid = ssid;
			this.name = name;

			if (Util.dataIsValid(address)){
				ByteString data = Util.getData(address);
				if (data.size() == 0){
					throw new IOException("数据错误!请重新请求!");
				}
				this.address = data.substring(3,4).hex();
				Logger.d(this.address);

			}else {
				throw new IOException("CRC16校验失败,请重新请求!");
			}
		}

		public String getSsid() {
			return ssid;
		}

		public String getName() {
			return name;
		}

		public String getAddress() {
			return address;
		}
	}

	public static class Cancelable implements UseCase.Cancelable {

		private FutureTask mFutureTask;

		@Override
		public boolean isCanceled() {
			return mFutureTask.isCancelled();
		}

		@Override
		public void cancel() {
			if (mFutureTask != null && !mFutureTask.isCancelled()){
				mFutureTask.cancel(true);
			}
		}
	}
}
