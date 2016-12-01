package com.zhuoli.onecard.setting.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.customview.GridDividerDecoration;
import com.zhuoli.onecard.setting.SettingActivity;
import com.zhuoli.onecard.setting.SettingComponent;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.adapter.CleanAdapter;
import com.zhuoli.onecard.setting.view.state.CleanFragmentState;
import com.zhuoli.onecard.utils.Util;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class CleanFragment
		extends BaseFragment<SettingContract.CleanView, SettingContract.CleanPresenter>
		implements SettingContract.CleanView {

	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_setting_clean;
	}

	@BindView(R.id.btn_clear)
	Button btn_clear;

	@BindView(R.id.tv_clear)
	TextView tv_clear;

	@BindView(R.id.rv_clean)
	RecyclerView rv_clean;

	private CleanAdapter mCleanAdapter;

	private SettingComponent mComponent;

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		setRippleEffect(btn_clear,R.drawable.bg_btn);
		rv_clean.setLayoutManager(new GridLayoutManager(getHostActivity(),4));
		rv_clean.addItemDecoration(new GridDividerDecoration(getHostActivity(),R.color.white, Util.dpToPx(5)));
		List<String> list = Arrays.asList("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16");
		mCleanAdapter = new CleanAdapter(R.layout.item_clean,list,getHostActivity());
		rv_clean.setAdapter(mCleanAdapter);
		rv_clean.addOnItemTouchListener(new OnItemClickListener() {
			@Override
			public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
				getPresenter().cleanSprayer(mCleanAdapter.getData().get(i));
			}
		});
	}

	@NonNull
	@Override
	public SettingContract.CleanPresenter createPresenter() {
		if (!(getHostActivity() instanceof SettingActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		mComponent = ((SettingActivity) getHostActivity()).getComponent();
		return mComponent.getCleanPresenter();
	}

	@NonNull
	@Override
	public ViewState createViewState() {
		return new CleanFragmentState();
	}

	@Override
	public void onNewViewStateInstance() {

	}

	@OnClick(R.id.btn_clear)
	void OnClearClick(){
		getPresenter().cleanSprayer();
	}

	@Override
	public void showProgressIndicator() {
		btn_clear.setEnabled(false);
		tv_clear.setVisibility(View.VISIBLE);
		tv_clear.setText("正在清洗中......");
	}

	@Override
	public void hideProgressIndicator() {
		btn_clear.setEnabled(true);
	}

	@Override
	public void showSucceed(String message) {
		tv_clear.setText(message);
	}

	@Override
	public void showError(String error) {
		tv_clear.setText(error);
	}

	@Override
	public void stopSucceed() {
		tv_clear.setText("已经停止清洗");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mCleanAdapter = null;
	}
}
