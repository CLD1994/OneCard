package com.zhuoli.onecard.setting.presenter;

import com.zhuoli.onecard.base.BasePresenter;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.usecase.ReadDate;
import com.zhuoli.onecard.setting.usecase.WriteDate;

import java.util.Date;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class DatePresenter
		extends BasePresenter<SettingContract.DateView>
		implements SettingContract.DatePresenter {

	private final WriteDate mWriteDate;

	private final ReadDate mReadDate;

	public DatePresenter(WriteDate writeDate, ReadDate readDate) {
		mWriteDate = writeDate;
		mReadDate = readDate;
	}

	@Override
	public void readDate() {
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}
		executeUseCase(mReadDate, new ReadDate.RequestValues(), new UseCase.UseCaseCallback<ReadDate.ResponseValue>() {
			@Override
			public void onSuccess(ReadDate.ResponseValue response) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().readSucceed(response.getDate());
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showError(status.getCode() + status.getMsg());
				}
			}
		});
	}

	@Override
	public void writeDate(Date date) {
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}

		executeUseCase(mWriteDate, new WriteDate.RequestValues(date), new UseCase.UseCaseCallback<WriteDate.ResponseValue>() {
			@Override
			public void onSuccess(WriteDate.ResponseValue response) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showSucceed(response.getMessage());
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showError(status.getCode() + status.getMsg());
				}
			}
		});
	}
}
