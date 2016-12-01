package com.zhuoli.onecard.setting.usecase;

import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.utils.CryptoUtils;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.FutureTask;

import okio.ByteString;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class WriteDate extends UseCase<WriteDate.RequestValues,WriteDate.ResponseValue>{

	private final DataSource mDataSource;

	private Cancelable mCancelable;

	public WriteDate(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return WriteDate.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(RequestValues requestValues) {
		mCancelable.mFutureTask = mDataSource.execute(requestValues.getCommand(), new DataSource.Callback<byte[]>() {
			@Override
			public void onDataLoad(byte[] data) {
				try {
					WriteDate.ResponseValue responseValue = new WriteDate.ResponseValue(data);
					getUseCaseCallback().onSuccess(responseValue);
				} catch (IOException e) {
					Logger.d(e.getMessage());
					if (!retry()) {
						getUseCaseCallback().onError(new Status("NOK", "请求失败"));
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

		private Date date;

		public RequestValues(Date date) {
			this.date = date;
		}

		byte[] getCommand(){
			Calendar ca = Calendar.getInstance();
			ca.setTime(date);
			String year = String.valueOf(ca.get(Calendar.YEAR));
			year = year.substring(2,4);
			year = ByteString.of(CryptoUtils.BCD.strToBcd(year)).hex();
			String month = String.valueOf(ca.get(Calendar.MONTH)+1);
			month = ByteString.of(CryptoUtils.BCD.strToBcd(month)).hex();
			String day = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
			day = ByteString.of(CryptoUtils.BCD.strToBcd(day)).hex();
			String week = String.valueOf(ca.get(Calendar.WEEK_OF_MONTH));
			week = ByteString.of(CryptoUtils.BCD.strToBcd(week)).hex();
			String hour = String.valueOf(ca.get(Calendar.HOUR_OF_DAY));
			hour = ByteString.of(CryptoUtils.BCD.strToBcd(hour)).hex();
			String minute = String.valueOf(ca.get(Calendar.MINUTE));
			minute = ByteString.of(CryptoUtils.BCD.strToBcd(minute)).hex();
			String second = String.valueOf(ca.get(Calendar.SECOND));
			second = ByteString.of(CryptoUtils.BCD.strToBcd(second)).hex();

			String data = String.format("%s%s%s%s%s%s%s",year,month,day,week,hour,minute,second);

			return Util.encodingCommand(SysConstant.WRITE_COUNTER_CONFIG,data);
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {
		private String message;

		public ResponseValue(byte[] responseValue) throws IOException {
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
			return mFutureTask == null || mFutureTask.isCancelled();
		}

		@Override
		public void cancel() {
			if (mFutureTask != null){
				mFutureTask.cancel(true);
			}
		}
	}
}
