package com.zhuoli.onecard.setting.usecase;

import android.support.annotation.NonNull;

import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.utils.CryptoUtils;
import com.zhuoli.onecard.utils.Preconditions;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.util.concurrent.FutureTask;

/**
 * Created by CLD on 2016/9/26 0026.
 */

public class WriteParameter extends UseCase<WriteParameter.RequestValues,WriteParameter.ResponseValue> {

	private final DataSource mDataSource;

	private Cancelable mCancelable;

	public WriteParameter(@NonNull DataSource dataSource) {
		mDataSource = Preconditions.checkNotNull(dataSource);
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return ReadParameter.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(RequestValues requestValues) {
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
		return mCancelable;
	}


	public static class RequestValues implements UseCase.RequestValues {
		private String charWidth;
		private String pointSize;
		private String printDelay;
		private String charSpace;
		private String counterDigit;
		private String counterMaxValue;
		private String batchCounter;
		private String reservedWord;
		private String charStyle;

		public RequestValues(String charWidth, String pointSize, String printDelay, String charSpace, String charStyle) {
			this.charWidth = charWidth;
			this.pointSize = pointSize;
			this.printDelay = printDelay;
			this.charSpace = charSpace;
			this.charStyle = charStyle;
			this.counterDigit = "05";
			this.counterMaxValue = "03E7";
			this.batchCounter = "0063";
			this.reservedWord = "0000";
		}

		byte[] getCommand(){
			String charWidth = CryptoUtils.HEX.decToHexString(this.charWidth,8);
			String pointSize = CryptoUtils.HEX.decToHexString(this.pointSize,8);
			String printDelay = CryptoUtils.HEX.decToHexString(this.printDelay,8);
			String charSpace = CryptoUtils.HEX.decToHexString(this.charSpace,8);
			String charStyle = CryptoUtils.HEX.decToHexString(this.charStyle,2);
			String data = String.format("%s%s%s%s%s%s%s%s%s%s",
					charWidth,pointSize,printDelay,charSpace,counterDigit,
					counterMaxValue,batchCounter,reservedWord,charStyle,charStyle);
			return Util.encodingCommand(SysConstant.WRITE_PARAMETER,data);
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
			return mFutureTask.isCancelled();
		}

		@Override
		public void cancel() {
			mFutureTask.cancel(true);
		}
	}
}
