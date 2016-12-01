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
import com.zhuoli.onecard.customview.PasswordDialogFragment;
import com.zhuoli.onecard.main.MainActivity;
import com.zhuoli.onecard.main.MainComponent;
import com.zhuoli.onecard.main.MainContract;
import com.zhuoli.onecard.main.adapter.OptionAdapter;
import com.zhuoli.onecard.main.adapter.model.Option;
import com.zhuoli.onecard.main.view.state.MainFragmentState;
import com.zhuoli.onecard.setting.SettingActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author CLD
 */
public class MainFragment
		extends BaseFragment<MainContract.View, MainContract.Presenter>
		implements MainContract.View{

	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_main;
	}


	public static final String TAG = "MainFragment";
	public static final String SETTING_PRINT = "打印设置";
	public static final String SETTING_CLEAN = "清洗喷头";
	public static final String SETTING_OTHER = "其他设置";

	@BindView(R.id.list)
	RecyclerView rv_list;

	private MainActivity mActivity;

	private MainComponent mComponent;

	@Override
	protected void initView(View root, Bundle savedInstanceState) {

		List<Option> optionList = new ArrayList<>();
		optionList.add(new Option(SETTING_PRINT));
		optionList.add(new Option(SETTING_CLEAN));
		optionList.add(new Option(SETTING_OTHER));

		final OptionAdapter optionAdapter = new OptionAdapter(R.layout.item_main_option,optionList);
		rv_list.setLayoutManager(new LinearLayoutManager(getHostActivity()));
		rv_list.setOverScrollMode(View.OVER_SCROLL_NEVER);
		rv_list.setAdapter(optionAdapter);
		rv_list.addOnItemTouchListener(new OnItemClickListener() {
			@Override
			public void SimpleOnItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
				Intent intent = new Intent(mActivity, SettingActivity.class);
				String optionName = optionAdapter.getItem(i).getName();
				switch (optionName){
					case SETTING_PRINT:
						intent.putExtra(MainFragment.TAG,SETTING_PRINT);
						startActivity(intent);
						break;
					case SETTING_CLEAN:
						intent.putExtra(MainFragment.TAG,SETTING_CLEAN);
						startActivity(intent);
						break;
					case SETTING_OTHER:
						new PasswordDialogFragment().show(getFragmentManager(),"dialog");
						break;
				}
			}
		});

	}

	@NonNull
	@Override
	public MainContract.Presenter createPresenter() {
		if (!(getHostActivity() instanceof MainActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		mActivity = (MainActivity) getHostActivity();
		mComponent = mActivity.getComponent();
		return mComponent.getMainPresenter();
	}

	@NonNull
	@Override
	public ViewState createViewState() {
		return new MainFragmentState();
	}

	@Override
	public void onNewViewStateInstance() {

	}


}