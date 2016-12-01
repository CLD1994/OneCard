package com.zhuoli.onecard.setting.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Max;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.setting.SettingActivity;
import com.zhuoli.onecard.setting.SettingComponent;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.view.state.ParameterFragmentState;
import com.zhuoli.onecard.utils.Toastor;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by CLD on 2016/9/26 0026.
 */

public class ParameterFragment
		extends BaseFragment<SettingContract.ParameterView, SettingContract.ParameterPresenter>
		implements SettingContract.ParameterView{

	public static final String STYLE_NORMAL = "1";
	public static final String STYLE_FLIP_H = "2";
	public static final String STYLE_FLIP_V = "3";
	public static final String STYLE_H_AND_V = "4";

	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_setting_parameter;
	}

	@Order(1)
	@Min(value = 600,message = "超出范围")
	@Max(value = 60000,message = "超出范围")
	@BindView(R.id.et_char_width)
	EditText et_charWidth;

	@Order(2)
	@Min(value = 300,message = "超出范围")
	@Max(value = 30000, message = "超出范围")
	@BindView(R.id.et_point_size)
	EditText et_pointSize;

	@Order(3)
	@Min(value = 0,message = "超出范围")
	@Max(value = 5000,message = "超出范围")
	@BindView(R.id.et_print_time)
	EditText et_printTime;

	@Order(4)
	@Min(value = 0,message = "超出范围")
	@Max(value = 255,message = "超出范围")
	@BindView(R.id.et_char_space)
	EditText et_charSpace;

	@Order(5)
	@Checked(message = "请选择字符样式！")
	@BindView(R.id.radio_group)
	RadioGroup radioGroup;

	@BindView(R.id.btn_read)
	Button btn_read;

	@BindView(R.id.btn_write)
	Button btn_write;

	private SettingComponent mComponent;

	private Toastor mToastor;

	private ProgressDialog mProgressDialog;

	private Validator mValidator;

	private String mCharStyle;

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		setRippleEffect(btn_read,R.drawable.bg_btn);
		setRippleEffect(btn_write,R.drawable.bg_btn);

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId){
					case R.id.normal:
						mCharStyle = STYLE_NORMAL;
						break;
					case R.id.flip_h:
						mCharStyle = STYLE_FLIP_H;
						break;
					case R.id.flip_v:
						mCharStyle = STYLE_FLIP_V;
						break;
					case R.id.h_and_v:
						mCharStyle = STYLE_H_AND_V;
						break;
				}
			}
		});

		mToastor = mComponent.getToastor();
		mProgressDialog = mComponent.getProgressDialog();

		mValidator = new Validator(this);
		mValidator.setValidationMode(Validator.Mode.IMMEDIATE);
		mValidator.setValidationListener(new Validator.ValidationListener() {
			@Override
			public void onValidationSucceeded() {
				getPresenter().writeParameter(
						et_charWidth.getText().toString(),
						et_pointSize.getText().toString(),
						et_printTime.getText().toString(),
						et_charSpace.getText().toString(),
						mCharStyle);
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
	public SettingContract.ParameterPresenter createPresenter() {
		if (!(getHostActivity() instanceof SettingActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		mComponent = ((SettingActivity) getHostActivity()).getComponent();
		return mComponent.getParameterPresenter();
	}

	@Override
	public void onNewViewStateInstance() {
		getPresenter().readParameter();
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
	public void showError(String error) {
		mToastor.showToast(error);
	}

	@Override
	public void readSucceed(String charWidth, String pointSize, String printDelay, String charSpace, String charStyle) {
		et_charWidth.setText(charWidth);
		et_pointSize.setText(pointSize);
		et_printTime.setText(printDelay);
		et_charSpace.setText(charSpace);
		switch (charStyle){
			case STYLE_NORMAL:
				radioGroup.check(R.id.normal);
				break;
			case STYLE_FLIP_H:
				radioGroup.check(R.id.flip_h);
				break;
			case STYLE_FLIP_V:
				radioGroup.check(R.id.flip_v);
				break;
			case STYLE_H_AND_V:
				radioGroup.check(R.id.h_and_v);
				break;
		}
		mToastor.showToast("读取配置成功");
	}

	@Override
	public void showSucceed(String message) {
		mToastor.showToast(message);
	}

	@OnClick(R.id.btn_read)
	void onReadClick(){
		getPresenter().readParameter();
	}

	@OnClick(R.id.btn_write)
	void onWriteClick(){
		mValidator.validate();
	}

	@NonNull
	@Override
	public ViewState createViewState() {
		return new ParameterFragmentState();
	}

}
