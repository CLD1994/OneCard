package com.zhuoli.onecard.setting.presenter;

import com.zhuoli.onecard.base.BasePresenter;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.usecase.CleanSprayer;
import com.zhuoli.onecard.setting.usecase.StopCleanSprayer;

/**
 * Created by CLD on 2016/9/27 0027.
 *
 */

public class CleanPresenter
		extends BasePresenter<SettingContract.CleanView>
		implements SettingContract.CleanPresenter{

	private final CleanSprayer mCleanSprayer;
	private final StopCleanSprayer mStopCleanSprayer;

	public CleanPresenter(CleanSprayer cleanSprayer, StopCleanSprayer stopCleanSprayer) {
		mCleanSprayer = cleanSprayer;
		mStopCleanSprayer = stopCleanSprayer;
	}

	@Override
	public void cleanSprayer() {
		CleanSprayer.RequestValues requestValues = new CleanSprayer.RequestValues();
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}
		executeUseCase(mCleanSprayer, requestValues, new UseCase.UseCaseCallback<CleanSprayer.ResponseValue>() {
			@Override
			public void onSuccess(CleanSprayer.ResponseValue response) {
				if (isViewAttached()){
					//noinspection ConstantConditions
					getView().hideProgressIndicator();
					getView().showSucceed("清洗成功");
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					//noinspection ConstantConditions
					getView().hideProgressIndicator();
					getView().showError(status.getMsg());
				}
			}
		});
	}

	@Override
	public void cleanSprayer(String sn) {
		CleanSprayer.RequestValues requestValues = new CleanSprayer.RequestValues();
		requestValues.setSn(sn);
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}
		executeUseCase(mCleanSprayer, requestValues, new UseCase.UseCaseCallback<CleanSprayer.ResponseValue>() {
			@Override
			public void onSuccess(CleanSprayer.ResponseValue response) {
				if (isViewAttached()){
					//noinspection ConstantConditions
					getView().hideProgressIndicator();
					getView().showSucceed("清洗成功");
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					//noinspection ConstantConditions
					getView().hideProgressIndicator();
					getView().showError(status.getMsg());
				}
			}
		});
	}

	@Override
	public void stopClean() {
		StopCleanSprayer.RequestValues requestValues = new StopCleanSprayer.RequestValues();
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}
		executeUseCase(mStopCleanSprayer, requestValues, new UseCase.UseCaseCallback<StopCleanSprayer.ResponseValue>() {
			@Override
			public void onSuccess(StopCleanSprayer.ResponseValue response) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().stopSucceed();
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
