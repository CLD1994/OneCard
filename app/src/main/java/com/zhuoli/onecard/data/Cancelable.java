package com.zhuoli.onecard.data;

import java.util.concurrent.FutureTask;

import retrofit2.Call;
import rx.Subscription;

/**
 * Created by CLD on 2016/7/18 0018.
 */
public class Cancelable {

	private final Call mCancelable;

	private final FutureTask mFutureTask;

	private final Subscription mSubscription;

	public Cancelable(Call cancelable) {
		mCancelable = cancelable;
		mFutureTask = null;
		mSubscription = null;
	}

	public Cancelable(FutureTask futureTask){
		mFutureTask = futureTask;
		mCancelable = null;
		mSubscription = null;
	}

	public Cancelable(Subscription subscription) {
		mSubscription = subscription;
		mCancelable = null;
		mFutureTask = null;
	}

	public void cancel(){
		//将已取消的,执行过的请求过滤
		if (mCancelable != null && !mCancelable.isCanceled() && mCancelable.isExecuted()){
			mCancelable.cancel();
		}

		if (mFutureTask != null && !mFutureTask.isCancelled() && !mFutureTask.isDone()){
			mFutureTask.cancel(true);
		}

		if (mSubscription != null && mSubscription.isUnsubscribed()){
			mSubscription.unsubscribe();
		}
	}
}
