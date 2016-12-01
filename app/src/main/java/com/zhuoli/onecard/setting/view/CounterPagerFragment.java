package com.zhuoli.onecard.setting.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.presenter.CounterPagerPresenter;
import com.zhuoli.onecard.setting.view.state.CounterPagerFragmentState;

import butterknife.BindView;

/**
 * author: CLD
 * created on: 2016/11/3 0003 下午 4:23
 * description:
 */

public class CounterPagerFragment
		extends BaseFragment<SettingContract.CounterPagerView,SettingContract.CounterPagerPresenter>
		implements SettingContract.CounterPagerView{

	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_counter_pager;
	}

	@BindView(R.id.tab_layout)
	TabLayout mTabLayout;

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		mTabLayout.addTab(mTabLayout.newTab().setText("计数器1"));
		mTabLayout.addTab(mTabLayout.newTab().setText("计数器2"));
		mTabLayout.addTab(mTabLayout.newTab().setText("计数器3"));

		final CounterFragment counterFragment1 = CounterFragment.newInstance("01");
		final CounterFragment counterFragment2 = CounterFragment.newInstance("02");
		final CounterFragment counterFragment3 = CounterFragment.newInstance("03");

		getChildFragmentManager().beginTransaction()
				.add(R.id.fragment_counter,counterFragment1,"counter01")
				.add(R.id.fragment_counter,counterFragment2,"counter02")
				.hide(counterFragment2)
				.add(R.id.fragment_counter,counterFragment3,"counter03")
				.hide(counterFragment3)
				.commit();
		mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
				switch (tab.getPosition()){
					case 0:
						transaction
								.hide(counterFragment2)
								.hide(counterFragment3)
								.show(counterFragment1);
						break;
					case 1:
						transaction
								.hide(counterFragment1)
								.hide(counterFragment3)
								.show(counterFragment2);
						break;
					case 2:
						transaction.hide(counterFragment1)
								   .hide(counterFragment2)
								   .show(counterFragment3);
						break;
				}
				transaction.commit();
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	@NonNull
	@Override
	public ViewState createViewState() {
		return new CounterPagerFragmentState();
	}

	@Override
	public void onNewViewStateInstance() {

	}

	@NonNull
	@Override
	public SettingContract.CounterPagerPresenter createPresenter() {
		return new CounterPagerPresenter();
	}
}
