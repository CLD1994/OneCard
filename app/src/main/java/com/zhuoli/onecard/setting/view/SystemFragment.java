package com.zhuoli.onecard.setting.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.setting.SettingActivity;
import com.zhuoli.onecard.setting.SettingComponent;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.view.state.SystemFragmentState;
import com.zhuoli.onecard.utils.Toastor;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by CLD on 2016/9/27 0027.
 *
 */

public class SystemFragment
		extends BaseFragment<SettingContract.SystemView,SettingContract.SystemPresenter>
		implements SettingContract.SystemView{
	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_setting_system;
	}

	@BindView(R.id.et_address)
	EditText et_address;

	@NotEmpty(message = "不能为空")
	@BindView(R.id.et_name)
	EditText et_name;

	@NotEmpty(message = "不能为空")
	@BindView(R.id.et_device_address)
	EditText et_device_address;

	@BindView(R.id.et_password)
	EditText et_password;

	@BindView(R.id.btn_write)
	Button btn_write;

	private SettingComponent mComponent;

	private Toastor mToastor;

	private ProgressDialog mProgressDialog;

	private Validator mValidator;

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		setRippleEffect(btn_write,R.drawable.bg_btn);

		et_address.setEnabled(false);

		mToastor = mComponent.getToastor();
		mProgressDialog = mComponent.getProgressDialog();

		mValidator = new Validator(this);
		mValidator.setValidationListener(new Validator.ValidationListener() {
			@Override
			public void onValidationSucceeded() {
				getPresenter().writeSystemConfig(
						et_address.getText().toString(),
						et_name.getText().toString(),
						et_device_address.getText().toString(),
						et_password.getText().toString());
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
	public SettingContract.SystemPresenter createPresenter() {
		if (!(getHostActivity() instanceof SettingActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		mComponent = ((SettingActivity) getHostActivity()).getComponent();
		return mComponent.getSystemPresenter();
	}

	@Override
	public void onNewViewStateInstance() {
		getPresenter().readSystemConfig();
	}

	@OnClick(R.id.btn_write)
	void  onWriteClick(){
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
	public void showSucceed(String message) {
		mToastor.showToast(message);
	}

	@Override
	public void showError(String error) {
		mToastor.showToast(error);
	}

	@Override
	public void showSystemConfig(String address, String name, String deviceAddress) {
		et_address.setText(address);
		et_name.setText(name);
		et_device_address.setText(deviceAddress);
	}

	@NonNull
	@Override
	public ViewState createViewState() {
		return new SystemFragmentState();
	}
}
