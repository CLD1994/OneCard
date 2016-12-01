package com.zhuoli.onecard.setting.usecase;

import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.setting.adapter.model.PrintContent;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

import okio.ByteString;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class ReadPrintConfig extends UseCase<ReadPrintConfig.RequestValues,ReadPrintConfig.ResponseValue>{

	private final DataSource mDataSource;

	private Cancelable mCancelable;

	public ReadPrintConfig(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return ReadPrintConfig.class.getSimpleName();
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
			String sequence = "0001";
			String data = sequence;
			return Util.encodingCommand(SysConstant.READ_CONTENT,data);
		}
	}

	public static class ResponseValue implements UseCase.ResponseValue {
		private List<PrintContent> printContentList;

		public ResponseValue(byte[] responseValue) throws IOException {
			if (Util.dataIsValid(responseValue)){
				ByteString data = Util.getData(responseValue);
				if (data.size() == 0){
					throw new IOException("数据错误!请重新请求!");
				}
				try {
					data = data.substring(5,data.toByteArray().length);
					Logger.d(data.hex());
					String content = new String(data.toByteArray(),"gb2312");
					//String content = data.utf8();
					Logger.d(content);
					printContentList = new ArrayList<>();
					int start = 0;
					int end;
					while (start != content.length()){
						end = content.indexOf("}1",start);
						if (end != -1){
							String c = content.substring(start,end);
							if (c.length() != 0) {
								printContentList.add(new PrintContent(PrintContent.SINGLE,c));
							}
							start = end + 2;
							end = content.indexOf("}1",start);
							String c1 = content.substring(start,end);
							start = end + 4;
							end = content.indexOf("}2",start);
							String c2 = content.substring(start,end);
							printContentList.add(new PrintContent(PrintContent.BOTH,c1,c2));
							start = end + 2;
						}else {
							printContentList.add(new PrintContent(PrintContent.SINGLE,content.substring(start,content.length())));
							break;
						}
					}
				} catch (UnsupportedEncodingException e) {
					throw new IOException("操作频率太快!");
				}
			}else {
				throw new IOException("CRC16校验失败,请重新请求!");
			}
		}

		public List<PrintContent> getPrintContentList() {
			return printContentList;
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
