package com.zhuoli.onecard.setting.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.RxBus;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.setting.SettingActivity;
import com.zhuoli.onecard.setting.SettingComponent;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.SettingEvent;
import com.zhuoli.onecard.setting.view.state.CounterFragmentState;
import com.zhuoli.onecard.utils.Toastor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by CLD on 2016/9/27 0027.
 *
 */

public class CounterFragment
		extends BaseFragment<SettingContract.CounterView,SettingContract.CounterPresenter>
		implements SettingContract.CounterView{

	public static final String COUNT_WAY_ADD = "01";

	public static final String COUNT_WAY_SUB = "FF";

	public static final String COUNT_PRE_ZEROIZE = "30";

	public static final String COUNT_PRE_NULL = "20";

	private boolean isVisible = false;

	public static CounterFragment newInstance(String sn) {
		Bundle bundle = new Bundle();
		bundle.putString("sn",sn);
		CounterFragment fragment = new CounterFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_setting_counter;
	}

	@Checked(message = "计数方式必须选择!")
	@Order(1)
	@BindView(R.id.count_way_radio_group)
	RadioGroup rg_countWay;

	@Pattern(regex = "^[1-5]$", message = "必须在1-5范围内")
	@Order(2)
	@BindView(R.id.et_count_digit)
	EditText et_countDigit;

	@Checked(message = "计数前置值必须选择!")
	@Order(3)
	@BindView(R.id.count_pre_radio_group)
	RadioGroup rg_countPre;


	@Order(4)
	@BindView(R.id.et_count_target)
	EditText et_countTarget;

	@Order(5)
	@BindView(R.id.et_count_start)
	EditText et_countStart;

	@Order(6)
	@BindView(R.id.et_count_current)
	EditText et_countCurrent;

	@BindView(R.id.btn_read)
	Button btn_read;

	@BindView(R.id.btn_write)
	Button btn_write;

	private SettingComponent mComponent;

	private Validator mValidator;

	private String mCountSn;

	private String mCountWay;

	private String mCountDigit;

	private String mCountPre;

	private String mCountTarget;

	private String mCountStart;

	private String mCountCurrent;

	private Toastor mToastor;

	private ProgressDialog mProgressDialog;

	@Override
	protected void initView(View root, final Bundle savedInstanceState) {
		setRippleEffect(btn_read,R.drawable.bg_btn);
		setRippleEffect(btn_write,R.drawable.bg_btn);

		mToastor = mComponent.getToastor();
		mProgressDialog = mComponent.getProgressDialog();

		if (getArguments() != null){
			mCountSn = getArguments().getString("sn");
		}

		rg_countWay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton button = ButterKnife.findById(group,checkedId);
				mCountWay = button.getText().toString();
				Bundle bundle = new Bundle();
				bundle.putString("way",mCountWay);
				RxBus.getDefault().post(SettingEvent.instant()
						.setType(SettingEvent.CHANGE_COUNTER_WAY)
						.setBundle(bundle));
			}
		});

		rg_countPre.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton button = ButterKnife.findById(group,checkedId);
				mCountPre = button.getText().toString();
			}
		});

		mValidator = new Validator(this);
		mValidator.setValidationMode(Validator.Mode.IMMEDIATE);

		//noinspection unchecked
		mValidator.put(et_countTarget, new QuickRule<EditText>() {
			@Override
			public boolean isValid(EditText view) {
				if (!TextUtils.isEmpty(view.getText())){
					String countDigit = et_countDigit.getText().toString();
					String countTarget = view.getText().toString();
					return countTarget.length() <= Integer.valueOf(countDigit);
				}else {
					return false;
				}

			}

			@Override
			public String getMessage(Context context) {
				if (!TextUtils.isEmpty(et_countTarget.getText())){
					return "不能超过最大位数限制";
				}else {
					return "不能为空";
				}

			}
		});

		//noinspection unchecked
		mValidator.put(et_countStart, new QuickRule<EditText>() {
			@Override
			public boolean isValid(EditText view) {
				if (!TextUtils.isEmpty(view.getText())){
					int countTarget = Integer.valueOf(et_countTarget.getText().toString());
					int countStat = Integer.valueOf(view.getText().toString());
					if (TextUtils.equals(mCountWay,"加")){
						return countStat <= countTarget;
					}else {
						return countStat >= countTarget;
					}
				}else {
					return false;
				}

			}

			@Override
			public String getMessage(Context context) {
				if (!TextUtils.isEmpty(et_countStart.getText())){
					if (TextUtils.equals(mCountWay,"加")){
						return "起始值不能大于目标值";
					}else {
						return "起始值不能小于目标值";
					}
				}else {
					return "不能为空";
				}
			}
		});

		//noinspection unchecked
		mValidator.put(et_countCurrent, new QuickRule<EditText>() {
			@Override
			public boolean isValid(EditText view) {
				if (!TextUtils.isEmpty(view.getText())){
					int countTarget = Integer.valueOf(et_countTarget.getText().toString());
					int countCurrent = Integer.valueOf(view.getText().toString());
					if (TextUtils.equals(mCountWay,"加")){
						return countCurrent <= countTarget;
					}else {
						return countCurrent >= countTarget;
					}
				}else {
					return false;
				}

			}

			@Override
			public String getMessage(Context context) {
				if (!TextUtils.isEmpty(et_countCurrent.getText())){
					if (TextUtils.equals(mCountWay,"加")){
						return "当前值不能大于目标值";
					}else {
						return "当前值不能小于目标值";
					}
				}else {
					return  "不能为空";
				}

			}
		});

		mValidator.setValidationListener(new Validator.ValidationListener() {
			@Override
			public void onValidationSucceeded() {
				mCountDigit = et_countDigit.getText().toString();
				mCountTarget = et_countTarget.getText().toString();
				mCountStart = et_countStart.getText().toString();
				mCountCurrent = et_countCurrent.getText().toString();
				String way = null;
				String pre = null;
				switch (mCountWay){
					case "加":
						way = COUNT_WAY_ADD;
						break;
					case "减":
						way = COUNT_WAY_SUB;
						break;
				}

				switch (mCountPre){
					case "补0":
						pre = COUNT_PRE_ZEROIZE;
						break;
					case "空":
						pre = COUNT_PRE_NULL;
						break;
				}
				getPresenter().writeCounterConfig(mCountSn, way, mCountDigit, pre, mCountTarget, mCountStart, mCountCurrent);
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

		busSubscribe(SettingEvent.class, new Action1<SettingEvent>() {
			@Override
			public void call(SettingEvent settingEvent) {
				if (settingEvent.getType() == SettingEvent.CHANGE_COUNTER_WAY){
					String countWay = settingEvent.getBundle().getString("way","");
					et_countDigit.setText("4");
					if (TextUtils.equals(countWay,"加")){
						if (rg_countWay.getCheckedRadioButtonId() != R.id.radio_btn_add){
							rg_countWay.check(R.id.radio_btn_add);
							et_countStart.setText("0");
							et_countCurrent.setText("0");
							et_countTarget.setText("9999");
						}
					}else {
						if (rg_countWay.getCheckedRadioButtonId() != R.id.radio_btn_sub){
							rg_countWay.check(R.id.radio_btn_sub);
							et_countStart.setText("9999");
							et_countCurrent.setText("9999");
							et_countTarget.setText("0");
						}
					}
				}
			}
		});

	}

	@NonNull
	@Override
	public SettingContract.CounterPresenter createPresenter() {
		if (!(getHostActivity() instanceof SettingActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		mComponent = ((SettingActivity) getHostActivity()).getComponent();
		return mComponent.getCounterPresenter();
	}

	@NonNull
	@Override
	public ViewState createViewState() {
		return new CounterFragmentState();
	}

	@Override
	public void onNewViewStateInstance() {
		getPresenter().readCounterConfig(mCountSn);
	}

	@OnClick(R.id.btn_read)
	void onReadClick(){
		getPresenter().readCounterConfig(mCountSn);
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
	public void showSucceed(String message) {
		mToastor.showToast(message);
	}

	@Override
	public void readSucceed(String sn, String way, String digit, String pre, String target, String start, String current) {
		mCountWay = way;
		switch (way){
			case COUNT_WAY_ADD:
				mCountWay = "加";
				rg_countWay.check(R.id.radio_btn_add);
				break;
			case COUNT_WAY_SUB:
				mCountWay = "减";
				rg_countWay.check(R.id.radio_btn_sub);
				break;
		}

		mCountDigit = digit;
		et_countDigit.setText(mCountDigit);

		switch (pre){
			case COUNT_PRE_ZEROIZE:
				mCountPre = "补0";
				rg_countPre.check(R.id.radio_btn_zeroize);
				break;
			case COUNT_PRE_NULL:
				mCountPre = "空";
				rg_countPre.check(R.id.radio_btn_null);
				break;
		}

		mCountTarget = target;
		et_countTarget.setText(mCountTarget);

		mCountStart = start;
		et_countStart.setText(mCountStart);

		mCountCurrent = current;
		et_countCurrent.setText(mCountCurrent);
	}

	@Override
	public void showError(String error) {
		mToastor.showToast(error);
	}

}
