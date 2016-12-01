package com.zhuoli.onecard.setting.usecase;

import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.utils.CryptoUtils;
import com.zhuoli.onecard.utils.TimeHelper;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.FutureTask;

import okio.ByteString;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class ReadDate extends UseCase<ReadDate.RequestValues,ReadDate.ResponseValue>{

	private final DataSource mDataSource;

	private Cancelable mCancelable;

	public ReadDate(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return ReadDate.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(RequestValues requestValues) {
		mCancelable.mFutureTask = mDataSource.execute(requestValues.getCommand(), new DataSource.Callback<byte[]>() {
			@Override
			public void onDataLoad(byte[] data) {
				try {
					getUseCaseCallback().onSuccess(new ReadDate.ResponseValue(data));
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
		byte[] getCommand(){
			return Util.encodingCommand(SysConstant.READ_SYSTEM_TIME,null);
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {

		private String year;
		private String month;
		private String sun;
		private String week;
		private String hour;
		private String minute;
		private String second;
		private Date date;

		public ResponseValue(byte[] responseValue) throws IOException {
			if (Util.dataIsValid(responseValue)){
				ByteString data = Util.getData(responseValue);
				if (data.size() == 0){
					throw new IOException("数据错误!请重新请求!");
				}
				year = CryptoUtils.BCD.bcdToStr(data.substring(0,1).toByteArray());
				month = CryptoUtils.BCD.bcdToStr(data.substring(1,2).toByteArray());
				sun = CryptoUtils.BCD.bcdToStr(data.substring(2,3).toByteArray());
				week = CryptoUtils.BCD.bcdToStr(data.substring(3,4).toByteArray());
				hour = CryptoUtils.BCD.bcdToStr(data.substring(4,5).toByteArray());
				minute = CryptoUtils.BCD.bcdToStr(data.substring(5,6).toByteArray());
				second = CryptoUtils.BCD.bcdToStr(data.substring(6,7).toByteArray());

				String str_date = String.format("20%s-%s-%s %s:%s:%s",year,month,sun,hour,minute,second);
				date = TimeHelper.getDateFromNativeTime(str_date);

			}else {
				throw new IOException("CRC16校验失败,请重新请求!");
			}
		}

		public Date getDate() {
			return date;
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
			if (mFutureTask != null){
				return mFutureTask.isCancelled();
			}
			return true;
		}

		@Override
		public void cancel() {
			if (mFutureTask != null){
				mFutureTask.cancel(true);
			}

		}
	}
}
