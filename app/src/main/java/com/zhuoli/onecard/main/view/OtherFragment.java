package com.zhuoli.onecard.main.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.main.MainActivity;
import com.zhuoli.onecard.main.MainContract;
import com.zhuoli.onecard.main.adapter.OptionAdapter;
import com.zhuoli.onecard.main.adapter.model.Option;
import com.zhuoli.onecard.main.view.state.OtherViewState;
import com.zhuoli.onecard.setting.SettingActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author CLD
 */
public class OtherFragment
		extends BaseFragment<MainContract.OtherView, MainContract.OtherPresenter>
		implements MainContract.OtherView {

	public static final String SETTING_PARAMETER = "参数设置";
	public static final String SETTING_COUNTER = "计数器设置";
	public static final String SETTING_DATE = "日期设置";
	public static final String SETTING_SYSTEM = "系统设置";


	public static OtherFragment newInstance() {
		return new OtherFragment();
	}

	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_other;
	}

	@BindView(R.id.list)
	RecyclerView rv_list;

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		List<Option> optionList = new ArrayList<>();
		optionList.add(new Option(SETTING_PARAMETER));
		optionList.add(new Option(SETTING_COUNTER));
		optionList.add(new Option(SETTING_DATE));
		optionList.add(new Option(SETTING_SYSTEM));

		final OptionAdapter optionAdapter = new OptionAdapter(R.layout.item_main_option,optionList);
		rv_list.setLayoutManager(new LinearLayoutManager(getHostActivity()));
		rv_list.setOverScrollMode(View.OVER_SCROLL_NEVER);
		rv_list.setAdapter(optionAdapter);
		rv_list.addOnItemTouchListener(new OnItemClickListener() {
			@Override
			public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
				Intent intent = new Intent(getHostActivity(), SettingActivity.class);
				String optionName = optionAdapter.getItem(i).getName();
				switch (optionName){
					case SETTING_PARAMETER:
						intent.putExtra(MainFragment.TAG,SETTING_PARAMETER);
						break;
					case SETTING_COUNTER:
						intent.putExtra(MainFragment.TAG,SETTING_COUNTER);
						break;
					case SETTING_DATE:
						intent.putExtra(MainFragment.TAG,SETTING_DATE);
						break;
					case SETTING_SYSTEM:
						intent.putExtra(MainFragment.TAG,SETTING_SYSTEM);
						break;
				}
				startActivity(intent);
			}
		});
	}

	@NonNull
	@Override
	public MainContract.OtherPresenter createPresenter() {
		if (!(getHostActivity() instanceof MainActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		return ((MainActivity) getHostActivity()).getComponent().getOtherPresenter();
	}
	
	@NonNull
	@Override
	public ViewState createViewState() {
		return new OtherViewState();
	}

	@Override
	public void onNewViewStateInstance() {

	}
}