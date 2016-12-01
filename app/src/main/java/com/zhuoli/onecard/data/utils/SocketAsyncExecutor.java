package com.zhuoli.onecard.data.utils;

import com.zhuoli.onecard.utils.AsyncExecutor;

import java.io.IOException;
import java.util.concurrent.FutureTask;

/**
 * author: CLD
 * created on: 2016/10/24 0024 下午 9:17
 * description:
 */

public class SocketAsyncExecutor extends AsyncExecutor{

	public SocketAsyncExecutor() {
	}

	public FutureTask execute(final byte[] command, final WorkerCallBack callBack){
		return execute(new Worker<byte[]>() {
			private IOException mIOException;
			@Override
			public byte[] doInBackground() {
				byte[] data = null;
				try {
					OneCardSocket socket = new OneCardSocket();
					socket.write(command);
					data = socket.read();
				} catch (IOException e) {
					mIOException = e;
				}

				return data;
			}

			@Override
			public void onPostExecute(byte[] data) {
				if (mIOException == null){
					callBack.onSucceed(data);
				}
				else {
					callBack.onError(mIOException);
				}
			}

			@Override
			public void onError(Exception e) {
				callBack.onError(e);
			}
		});
	}

	public interface WorkerCallBack{
		void onSucceed(byte[] bytes);
		void onError(Exception e);
	}
}
