package com.zhuoli.onecard.connect.presenter;

import com.zhuoli.onecard.base.BasePresenter;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.connect.ConnectContract;
import com.zhuoli.onecard.connect.usecase.SelectAllSSID;
import com.zhuoli.onecard.data.source.remote.response.Status;

import java.util.List;

/**
 * @author CLD
 */
public class ChooseSSIDPresenter extends BasePresenter<ConnectContract.ChooseSSIDView> implements ConnectContract.ChooseSSIDPresenter {

	private final SelectAllSSID mSelectAllSSID;

	public ChooseSSIDPresenter(SelectAllSSID selectAllSSID) {
		mSelectAllSSID = selectAllSSID;
	}

	@Override
	public void selectAllSSID(List<String> SSIDList) {
		SelectAllSSID.RequestValues requestValues = new SelectAllSSID.RequestValues(SSIDList);
		executeUseCase(mSelectAllSSID, requestValues, new UseCase.UseCaseCallback<SelectAllSSID.ResponseValue>() {
			@Override
			public void onSuccess(SelectAllSSID.ResponseValue response) {
				if (isViewAttached()){
					//noinspection ConstantConditions
					getView().showAllSSID(response.getSSIDList());
				}

			}

			@Override
			public void onError(Status status) {

			}
		});
	}
}