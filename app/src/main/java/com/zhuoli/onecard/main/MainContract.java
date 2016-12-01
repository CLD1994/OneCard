package com.zhuoli.onecard.main;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.zhuoli.onecard.base.BaseView;

/**
 * @author CLD
 */
public interface MainContract {
	interface View extends BaseView {
	}

	interface Presenter extends MvpPresenter<View> {
	}

	interface OtherView extends BaseView{

	}

	interface OtherPresenter extends MvpPresenter<OtherView>{

	}
}