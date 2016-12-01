package com.zhuoli.onecard.setting.presenter;

import com.zhuoli.onecard.base.BasePresenter;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.usecase.ReadCounterConfig;
import com.zhuoli.onecard.setting.usecase.WriteCounterConfig;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class CounterPresenter
		extends BasePresenter<SettingContract.CounterView>
		implements SettingContract.CounterPresenter{

	private final ReadCounterConfig mReadCounterConfig;

	private final WriteCounterConfig mWriteCounterConfig;

	public CounterPresenter(ReadCounterConfig readCounterConfig, WriteCounterConfig writeCounterConfig) {
		mReadCounterConfig = readCounterConfig;
		mWriteCounterConfig = writeCounterConfig;
	}


	@Override
	public void readCounterConfig(String sn) {
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}

		ReadCounterConfig.RequestValues requestValues = new ReadCounterConfig.RequestValues(sn);

		executeUseCase(mReadCounterConfig, requestValues, new UseCase.UseCaseCallback<ReadCounterConfig.ResponseValue>() {
			@Override
			public void onSuccess(ReadCounterConfig.ResponseValue response) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().readSucceed(response.getSn(),
							response.getCountWay(),
							response.getCountDigit(),
							response.getCountPre(),
							response.getCountTarget(),
							response.getCountStart(),
							response.getCountCurrent());
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showError(status.getMsg());
				}
			}
		});

	}

	@Override
	public void writeCounterConfig(String sn, String way, String digit, String pre, String target, String start, String current) {
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}
		WriteCounterConfig.RequestValues requestValues = new WriteCounterConfig.RequestValues(sn,way,digit,pre,target,start,current);
		executeUseCase(mWriteCounterConfig, requestValues, new UseCase.UseCaseCallback<WriteCounterConfig.ResponseValue>() {
			@Override
			public void onSuccess(WriteCounterConfig.ResponseValue response) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showSucceed(response.getMessage());
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showError(status.getMsg());
				}
			}
		});
	}
}
