package com.zhuoli.onecard.base;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CLD on 2016/9/20 0020.
 */
public class BasePresenter<V extends BaseView> extends MvpBasePresenter<V> {
	private Map<String,UseCase.Cancelable> mCancelableHolder;

	@Override
	public void attachView(V view) {
		super.attachView(view);
		mCancelableHolder = new HashMap<>();
	}

	public <Q extends UseCase.RequestValues, R extends UseCase.ResponseValue>
	void executeUseCase(UseCase<Q,R> useCase, Q values){
		executeUseCase(useCase,values,null);
	}
	public <Q extends UseCase.RequestValues, R extends UseCase.ResponseValue>
	void executeUseCase(UseCase<Q,R> useCase, Q values, UseCase.UseCaseCallback<R> callback){
		//根据TAG取得用例的取消权
		if (mCancelableHolder != null){
			//得到用例的TAG
			String tag = useCase.getTag();
			UseCase.Cancelable cancelable = mCancelableHolder.get(tag);
			//如果取消权不为空并且处于未取消状态
			if (cancelable != null && !cancelable.isCanceled()){
				//取消用例
				cancelable.cancel();
			}
			//将用例的取消权添加进取消权持有者
			mCancelableHolder.put(tag,useCase.run(values,callback));
		}
	}

	@Override
	public void detachView(boolean retainInstance) {
		super.detachView(retainInstance);
		//取消所有请求
		if (mCancelableHolder != null){
			for (UseCase.Cancelable cancelable: mCancelableHolder.values()) {
				if (cancelable != null){
					cancelable.cancel();
				}
			}
			mCancelableHolder = null;
		}
	}
}
