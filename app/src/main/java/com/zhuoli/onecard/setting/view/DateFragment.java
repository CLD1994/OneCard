package com.zhuoli.onecard.setting.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.setting.SettingActivity;
import com.zhuoli.onecard.setting.SettingComponent;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.view.state.DateFragmentState;
import com.zhuoli.onecard.utils.TimeHelper;
import com.zhuoli.onecard.utils.Toastor;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by CLD on 2016/9/27 0027.
 *
 */

public class DateFragment
		extends BaseFragment<SettingContract.DateView,SettingContract.DatePresenter>
		implements SettingContract.DateView{

	private final static String REGEX ="^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";

	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_setting_date;
	}

	@NotEmpty(message = "不能为空")
	@Pattern(regex = REGEX, message = "格式:2016-10-27")
	@BindView(R.id.et_date)
	TextView et_date;

	@NotEmpty(message = "不能为空")
	@Pattern(regex = "^[0-2][0-3]:[0-5]\\d:[0-5]\\d$", message = "格式:20:35:59")
	@BindView(R.id.et_time)
	TextView et_time;

	@BindView(R.id.btn_read)
	Button btn_read;

	@BindView(R.id.btn_write)
	Button btn_write;

	private SettingComponent mComponent;

	private Validator mValidator;

	private Toastor mToastor;

	private ProgressDialog mProgressDialog;

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		setRippleEffect(btn_read,R.drawable.bg_btn);
		setRippleEffect(btn_write,R.drawable.bg_btn);

		mToastor = mComponent.getToastor();
		mProgressDialog = mComponent.getProgressDialog();

		mValidator = new Validator(this);
		mValidator.setValidationListener(new Validator.ValidationListener() {
			@Override
			public void onValidationSucceeded() {
				String date = et_date.getText().toString();
				String time = et_time.getText().toString();
				getPresenter().writeDate(TimeHelper.getDateFromNativeTime(date + " " + time));
			}

			@Override
			public void onValidationFailed(List<ValidationError> errors) {
				for (ValidationError error : errors) {
					View view = error.getView();
					String message = error.getCollatedErrorMessage(getHostActivity());
					// Display error messages
					if (view instanceof TextView) {
						view.requestFocus();
						((TextView) view).setError(message);
					}else {
						mToastor.showToast(message);
					}
				}
			}
		});
	}

	@NonNull
	@Override
	public SettingContract.DatePresenter createPresenter() {
		if (!(getHostActivity() instanceof SettingActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		mComponent = ((SettingActivity) getHostActivity()).getComponent();
		return mComponent.getDatePresener();
	}

	@Override
	public void onNewViewStateInstance() {
		getPresenter().readDate();
	}

	@OnClick(R.id.btn_read)
	void onReadClick(){
		getPresenter().readDate();
	}

	@OnClick(R.id.btn_write)
	void onWriteClick(){
		mValidator.validate();
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
	public void readSucceed(Date date) {
		String str_date = TimeHelper.getDisplayTime(date);
		String[] strings = str_date.split(" ");
		et_date.setText(strings[0]);
		et_time.setText(strings[1]);
	}

	@Override
	public void showSucceed(String message) {
		mToastor.showToast(message);
	}

	@Override
	public void showError(String error) {
		mToastor.showToast(error);
	}

	@NonNull
	@Override
	public ViewState createViewState() {
		return new DateFragmentState();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mComponent = null;
		mValidator = null;
		mToastor = null;
		if (mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
		mProgressDialog = null;
	}
}
