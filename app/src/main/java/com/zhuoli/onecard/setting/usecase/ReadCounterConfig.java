package com.zhuoli.onecard.setting.usecase;

import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.utils.CryptoUtils;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.util.concurrent.FutureTask;

import okio.ByteString;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class ReadCounterConfig extends UseCase<ReadCounterConfig.RequestValues,ReadCounterConfig.ResponseValue>{
	private final DataSource mDataSource;

	private ReadCounterConfig.Cancelable mCancelable;

	public ReadCounterConfig(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return ReadCounterConfig.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(RequestValues requestValues) {
		mCancelable.mFutureTask = mDataSource.execute(requestValues.getCommand(),new DataSource.Callback<byte[]>() {
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
		return mCancelable;
	}

	public static class RequestValues implements UseCase.RequestValues {
		private String sn;

		public RequestValues(String sn) {
			this.sn = sn;
		}

		byte[] getCommand(){
			return Util.encodingCommand(SysConstant.READ_COUNTER_CONFIG, sn);
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {

		private String sn;

		private String countWay;

		private String countDigit;

		private String countPre;

		private String countTarget;

		private String countStart;

		private String countCurrent;

		public ResponseValue(byte[] responseValue) throws IOException{
			if (Util.dataIsValid(responseValue)){
				ByteString data = Util.getData(responseValue);
				if (data.size() == 0){
					throw new IOException("数据失效请重新请求");
				}
				try {
					sn = CryptoUtils.HEX.hexToDecString(data.substring(0,1).hex());
					countDigit = CryptoUtils.HEX.hexToDecString(data.substring(1,2).hex());
					countTarget = CryptoUtils.HEX.hexToDecString(data.substring(2,6).hex());
					countPre = data.substring(6,7).hex();
					countStart = CryptoUtils.HEX.hexToDecString(data.substring(7,11).hex());
					countCurrent = CryptoUtils.HEX.hexToDecString(data.substring(11,15).hex());
					countWay = data.substring(15,16).hex();
				} catch (Exception e) {
					throw new IOException("操作频率太快!");
				}
			}else {
				throw new IOException("数据失效请重新请求");
			}
		}

		public String getSn() {
			return sn;
		}

		public String getCountWay() {
			return countWay;
		}

		public String getCountDigit() {
			return countDigit;
		}

		public String getCountPre() {
			return countPre;
		}

		public String getCountTarget() {
			return countTarget;
		}

		public String getCountStart() {
			return countStart;
		}

		public String getCountCurrent() {
			return countCurrent;
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
			mFutureTask.cancel(true);
		}
	}
}
