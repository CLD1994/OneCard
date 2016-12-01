package com.zhuoli.onecard.setting.usecase;

import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;

import java.util.concurrent.FutureTask;

/**
 * author: CLD
 * created on: 2016/10/13 0013 下午 9:01
 * description:
 */

public class StopCleanSprayer extends UseCase<StopCleanSprayer.RequestValues,StopCleanSprayer.ResponseValue>{

	private final DataSource mDataSource;

	private Cancelable mCancelable;

	public StopCleanSprayer(DataSource dataSource) {
		mDataSource = dataSource;
		mCancelable = new Cancelable();
	}

	@Override
	protected String getTag() {
		return StopCleanSprayer.class.getSimpleName();
	}

	@Override
	protected UseCase.Cancelable executeUseCase(RequestValues requestValues) {
		return mCancelable;
	}

	public static class RequestValues implements UseCase.RequestValues {

	}

	public static class ResponseValue implements UseCase.ResponseValue {

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
