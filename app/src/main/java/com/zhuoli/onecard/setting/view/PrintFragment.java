package com.zhuoli.onecard.setting.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.setting.SettingActivity;
import com.zhuoli.onecard.setting.SettingComponent;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.adapter.PrintAdapter;
import com.zhuoli.onecard.setting.adapter.model.PrintContent;
import com.zhuoli.onecard.setting.view.state.PrintFragmentState;
import com.zhuoli.onecard.utils.Toastor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class PrintFragment
		extends BaseFragment<SettingContract.PrintView,SettingContract.PrintPresenter>
		implements SettingContract.PrintView{
	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_setting_print;
	}

	@BindView(R.id.btn_single)
	Button btn_single;

	@BindView(R.id.btn_both)
	Button btn_both;

	@BindView(R.id.rv_print_content)
	RecyclerView rv_print_contetn;

	@BindView(R.id.btn_read)
	Button btn_read;

	@BindView(R.id.btn_write)
	Button btn_write;

	private SettingComponent mComponent;

	private Toastor mToastor;

	private ProgressDialog mProgressDialog;

	private PrintAdapter mPrintAdapter;

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		setRippleEffect(btn_single,R.drawable.bg_btn);
		setRippleEffect(btn_both,R.drawable.bg_btn);
		setRippleEffect(btn_read,R.drawable.bg_btn);
		setRippleEffect(btn_write,R.drawable.bg_btn);

		mToastor = mComponent.getToastor();
		mProgressDialog = mComponent.getProgressDialog();

		mPrintAdapter = new PrintAdapter(new ArrayList<PrintContent>());

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getHostActivity());
		rv_print_contetn.setLayoutManager(linearLayoutManager);
		rv_print_contetn.setAdapter(mPrintAdapter);


		rv_print_contetn.addOnItemTouchListener(new OnItemChildClickListener() {
			@Override
			public void SimpleOnItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
				mPrintAdapter.remove(i);
			}
		});

	}

	@NonNull
	@Override
	public SettingContract.PrintPresenter createPresenter() {
		if (!(getHostActivity() instanceof SettingActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		mComponent = ((SettingActivity) getHostActivity()).getComponent();
		return mComponent.getPrintPresenter();
	}

	@NonNull
	@Override
	public ViewState createViewState() {
		return new PrintFragmentState();
	}

	@Override
	public void onNewViewStateInstance() {
		getPresenter().readPrint();
	}

	@Override
	public void showProgressIndicator() {
		mProgressDialog.show();
	}

	@Override
	public void hideProgressIndicator() {
		mProgressDialog.dismiss();
	}

	@Override
	public void onReadSucceed(List<PrintContent> contentList) {
		mPrintAdapter.setNewData(contentList);
	}

	@Override
	public void showSucceed(String message) {
		mToastor.showToast(message);
	}

	@Override
	public void showError(String error) {
		mToastor.showToast(error);
	}

	@OnClick(R.id.btn_single)
	void OnSingleClick(){
		mPrintAdapter.addData(new PrintContent(PrintContent.SINGLE,""));
	}

	@OnClick(R.id.btn_both)
	void OnBothClick(){
		mPrintAdapter.addData(new PrintContent(PrintContent.BOTH,"",""));
	}

	@OnClick(R.id.btn_read)
	void onReadClick(){
		getPresenter().readPrint();
	}

	@OnClick(R.id.btn_write)
	void onWriteClick(){
		String content = "";
		for (PrintContent printContent : mPrintAdapter.getData()){
			String singleContent;
			String bothContent;
			singleContent = printContent.getSingleContent();
			if (TextUtils.isEmpty(singleContent)){
				singleContent = " ";
			}
			switch (printContent.getItemType()){
				case PrintContent.SINGLE:
					content += singleContent;
					break;
				case PrintContent.BOTH:
					bothContent = printContent.getBothContent();
					if (TextUtils.isEmpty(bothContent)){
						bothContent = " ";
					}
					content += "}1" + singleContent + "}1" + "}2" + bothContent + "}2";
					break;
			}
		}
		Logger.d(content);
		getPresenter().writePrint(content);
	}

}
