package com.zhuoli.onecard.setting.usecase;

import com.orhanobut.logger.Logger;
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

public class WriteCounterConfig extends UseCase<WriteCounterConfig.RequestValues,WriteCounterConfig.ResponseValue>{

	private final DataSource mDataSource;

	private Cancelable mCancelable;

	public WriteCounterConfig(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return WriteCounterConfig.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(RequestValues requestValues) {
		mCancelable.mFutureTask = mDataSource.execute(requestValues.getCommand(), new DataSource.Callback<byte[]>() {
			@Override
			public void onDataLoad(byte[] data) {
				try {
					ResponseValue responseValue = new ResponseValue(data);
					getUseCaseCallback().onSuccess(responseValue);
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
		return mCancelable;
	}

	public static class RequestValues implements UseCase.RequestValues {
		private String sn;

		private String countWay;

		private String countDigit;

		private String countPre;

		private String countTarget;

		private String countStart;

		private String countCurrent;

		public RequestValues(String sn, String countWay, String countDigit, String countPre, String countTarget, String countStart, String countCurrent) {
			this.sn = sn;
			this.countWay = countWay;
			this.countDigit = countDigit;
			this.countPre = countPre;
			this.countTarget = countTarget;
			this.countStart = countStart;
			this.countCurrent = countCurrent;
		}

		byte[] getCommand(){

			//编号字符串转成16进制
			String sn = CryptoUtils.HEX.decToHexString(this.sn,2);
			//计数器位数字符串转成16进制
			String countDigit = CryptoUtils.HEX.decToHexString(this.countDigit,2);
			//计数器目标值转成16进制,占4字节
			String countTarget = CryptoUtils.HEX.decToHexString(this.countTarget,8);
			//计数器开始值转成16进制,占4字节
			String countStart = CryptoUtils.HEX.decToHexString(this.countStart,8);
			//计数器当前值转成16进制,占4字节
			String countCurrent = CryptoUtils.HEX.decToHexString(this.countCurrent,8);

			String data = String.format("%s%s%s%s%s%s%s",sn,countDigit,countTarget,countPre,countStart,countCurrent,countWay);

			return Util.encodingCommand(SysConstant.WRITE_COUNTER_CONFIG,data);
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {

		private String message;

		public ResponseValue(byte[] responseValue) throws IOException{
			if (Util.writeIsSucceed(responseValue)){
				message = "写入成功!";
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
			mFutureTask.cancel(true);
		}
	}
}
