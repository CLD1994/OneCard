package com.zhuoli.onecard.connect.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.RxBus;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.connect.ConnectActivity;
import com.zhuoli.onecard.connect.ConnectContract;
import com.zhuoli.onecard.connect.ConnectEvent;
import com.zhuoli.onecard.connect.adapter.SSIDAdapter;
import com.zhuoli.onecard.connect.view.state.ConnectViewState;
import com.zhuoli.onecard.data.model.SSID;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author CLD
 */
public class ChooseSSIDFragment
		extends BaseFragment<ConnectContract.ChooseSSIDView, ConnectContract.ChooseSSIDPresenter>
		implements ConnectContract.ChooseSSIDView {

	public static final String SSID_LIST = "SSIDList";

	public static ChooseSSIDFragment newInstance(ArrayList<String> SSIDList) {
		ChooseSSIDFragment fragment = new ChooseSSIDFragment();
		Bundle args = new Bundle();
		args.putStringArrayList(SSID_LIST, SSIDList);
		fragment.setArguments(args);
		return fragment;
	}

	@BindView(R.id.title)
	TextView title;

	@BindView(R.id.top_bar)
	View topBar;

	@BindView(R.id.bottom_bar)
	View bottomBar;

	@BindView(R.id.list)
	RecyclerView mRecyclerView;

	SSIDAdapter ssidAdapter;

	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_choose_ssid;
	}

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		ssidAdapter = new SSIDAdapter(R.layout.item_chooss_ssid,new ArrayList<SSID>());
		mRecyclerView.setAdapter(ssidAdapter);
		mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
			@Override
			public void SimpleOnItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
				SSID SSID = ssidAdapter.getData().get(i);
				Bundle bundle = new Bundle();
				bundle.putString(ConnectEvent.KEY_CHOOSE_SSID,SSID.getSsid());
				RxBus.getDefault().post(
						ConnectEvent.instant()
								.setType(ConnectEvent.CHOOSE_SSID)
								.setBundle(bundle)
				);
			}
		});

		if (getArguments() != null) {
			List<String> SSIDList = getArguments().getStringArrayList(SSID_LIST);
			if (SSIDList != null){
				getPresenter().selectAllSSID(SSIDList);
			}
		}


	}

	@NonNull
	@Override
	public ConnectContract.ChooseSSIDPresenter createPresenter() {
		if (!(getHostActivity() instanceof ConnectActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		return ((ConnectActivity) getHostActivity()).getComponent().getChooseSSIDPresenter();
	}
	
	@NonNull
	@Override
	public ViewState createViewState() {
		return new ConnectViewState();
	}

	@Override
	public void onNewViewStateInstance() {

	}

	@Override
	public void showAllSSID(List<SSID> SSIDList) {
		String str = "找到 " + SSIDList.size() + " 个信号";
		title.setText(str);

		if (SSIDList.size() > 3){
			topBar.setVisibility(View.VISIBLE);
			bottomBar.setVisibility(View.VISIBLE);
		}
		ssidAdapter.setNewData(SSIDList);
	}

	public void setNewData(List<String> SSIDList){

		if (SSIDList != null && getPresenter() != null){
			getPresenter().selectAllSSID(SSIDList);
		}
	}
}