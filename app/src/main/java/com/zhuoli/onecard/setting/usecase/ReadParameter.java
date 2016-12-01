package com.zhuoli.onecard.setting.usecase;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.utils.CryptoUtils;
import com.zhuoli.onecard.utils.Preconditions;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.util.concurrent.FutureTask;

import okio.ByteString;

/**
 * Created by CLD on 2016/9/26 0026.
 */

public class ReadParameter extends UseCase<ReadParameter.RequestValues,ReadParameter.ResponseValue> {

	private final DataSource mDataSource;

	private Cancelable mCancelable;

	public ReadParameter(@NonNull DataSource dataSource) {
		mDataSource = Preconditions.checkNotNull(dataSource);
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return ReadParameter.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(RequestValues requestValues) {
		mCancelable.mFutureTask = mDataSource.execute(requestValues.getCommand(),new DataSource.Callback<byte[]>() {
			@Override
			public void onDataLoad(byte[] data) {
				try {
					getUseCaseCallback().onSuccess(new ResponseValue(data));
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
			return Util.encodingCommand(SysConstant.READ_PARAMETER,null);
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {
		private String charWidth;
		private String pointSize;
		private String printDelay;
		private String charSpace;
		private String counterDigit;
		private String counterMaxValue;
		private String batchCounter;
		private String reservedWord;
		private String charStyle;

		public ResponseValue(byte[] responseValue) throws IOException{
			if (Util.dataIsValid(responseValue)){
				ByteString data = Util.getData(responseValue);
				if (data.size() == 0){
					throw new IOException("数据错误!请重新请求!");
				}

				try {
					charWidth = CryptoUtils.HEX.hexToDecString(data.substring(0,4).hex());
					pointSize = CryptoUtils.HEX.hexToDecString(data.substring(4,8).hex());
					printDelay = CryptoUtils.HEX.hexToDecString(data.substring(8,12).hex());
					charSpace = CryptoUtils.HEX.hexToDecString(data.substring(12,16).hex());
					counterDigit = CryptoUtils.HEX.hexToDecString(data.substring(16,17).hex());
					counterMaxValue = CryptoUtils.HEX.hexToDecString(data.substring(17,19).hex());
					batchCounter = CryptoUtils.HEX.hexToDecString(data.substring(19,21).hex());
					reservedWord = CryptoUtils.HEX.hexToDecString(data.substring(21,23).hex());
					charStyle = CryptoUtils.HEX.hexToDecString(data.substring(23,24).hex());
				} catch (Exception e) {
					throw new IOException("操作频率太快!");
				}

			}else {
				throw new IOException("CRC16校验失败,请重新请求!");
			}
		}

		public String getCharWidth() {
			return charWidth;
		}

		public String getPointSize() {
			return pointSize;
		}

		public String getPrintDelay() {
			return printDelay;
		}

		public String getCharSpace() {
			return charSpace;
		}

		public String getCounterDigit() {
			return counterDigit;
		}

		public String getCounterMaxValue() {
			return counterMaxValue;
		}

		public String getBatchCounter() {
			return batchCounter;
		}

		public String getReservedWord() {
			return reservedWord;
		}

		public String getCharStyle() {
			return charStyle;
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
