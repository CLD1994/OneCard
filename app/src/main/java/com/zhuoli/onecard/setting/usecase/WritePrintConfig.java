package com.zhuoli.onecard.setting.usecase;

import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.utils.CryptoUtils;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.FutureTask;

import okio.ByteString;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class WritePrintConfig extends UseCase<WritePrintConfig.RequestValues,WritePrintConfig.ResponseValue>{

	private final DataSource mDataSource;

	private final Cancelable mCancelable;

	public WritePrintConfig(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return WritePrintConfig.class.getSimpleName();
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
		private String content;

		public RequestValues(String content) {
			this.content = content;
		}

		byte[] getCommand(){
			//String data = CryptoUtils.HEX.enUnicode(content);
			try {
				ByteString byteString = ByteString.of(content.getBytes("gb2312"));
				int contentLength = byteString.toByteArray().length;
				String data = "01" + "0001" + CryptoUtils.HEX.decToHexString(contentLength,4) + byteString.hex();
				Logger.d("data:" + data);
				return Util.encodingCommand(SysConstant.WRITE_CONTENT,data);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;

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
