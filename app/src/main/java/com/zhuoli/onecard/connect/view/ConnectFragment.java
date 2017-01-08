package com.zhuoli.onecard.connect.view;


import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.RxBus;
import com.zhuoli.onecard.base.BaseFragment;
import com.zhuoli.onecard.connect.ConnectActivity;
import com.zhuoli.onecard.connect.ConnectContract;
import com.zhuoli.onecard.connect.ConnectEvent;
import com.zhuoli.onecard.connect.view.state.ConnectViewState;
import com.zhuoli.onecard.main.MainActivity;
import com.zhuoli.onecard.utils.CountDownTimer;
import com.zhuoli.onecard.utils.WifiAdmin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import rx.functions.Action1;

/**
 * @author CLD
 */
public class ConnectFragment
		extends BaseFragment<ConnectContract.ConnectView, ConnectContract.ConnectPresenter>
		implements ConnectContract.ConnectView {

	public static final String SEARCHING = "searching";

	public static final String CONNECTING = "connecting";

	public static final String SUCCESS = "success";

	public static final String FAILURE = "failure";

	public static ConnectFragment newInstance() {
		return new ConnectFragment();
	}

	@BindView(R.id.title)
	TextView title;

	@BindView(R.id.center_image)
	ImageView center_image;

	@BindView(R.id.waiting_gif)
	GifImageView wating_gif;

	@BindView(R.id.button)
	Button mButton;

	private GifDrawable wating_git_draw;

	private WifiAdmin mWifiAdmin;
	private String ssid;
	private ArrayList<String> SSIDList = new ArrayList<>();
	private boolean connecting = false;
	private boolean connected = false;
	private boolean isSearched = false;
	private boolean isCountdown = false;
	private CountDownTimer timer;
	private final long TIMEOUT = 15000;
	private int retryCount = 0;

	ChooseSSIDFragment chooseSSIDFragment;


	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_connect;
	}

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		wating_git_draw = (GifDrawable) wating_gif.getDrawable();
		initWifiAdmin();
		busSubscribe(ConnectEvent.class, new Action1<ConnectEvent>() {
			@Override
			public void call(ConnectEvent connectEvent) {
				switch (connectEvent.getType()){
					case ConnectEvent.CHOOSE_SSID:
						//连接选择的WIFI
						String currentSsid = mWifiAdmin.getSSID().replace("\"","");
						String selectSsid = connectEvent.getBundle().getString(ConnectEvent.KEY_CHOOSE_SSID);
						if (TextUtils.isEmpty(currentSsid)){
							ssid = selectSsid;
							changeState(ConnectFragment.CONNECTING);
						}else {
							if (judgeSSID(currentSsid)) {
								ssid = currentSsid;
								if (!TextUtils.equals(ssid, selectSsid)) {
									ssid = selectSsid;
									changeState(ConnectFragment.CONNECTING);
								} else {
									timer.cancel();
									connected = true;
									isCountdown = false;
									startActivity(new Intent(getHostActivity(), MainActivity.class));
									new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											getFragmentManager().beginTransaction().hide(chooseSSIDFragment).show(ConnectFragment.this).commitAllowingStateLoss();
										}
									},1000);

								}
							} else {
								ssid = selectSsid;
								changeState(ConnectFragment.CONNECTING);
							}
						}
						break;
				}
			}
		});

	}


	@Override
	public void onResume() {
		super.onResume();
		if (!isCountdown){
			connecting = false;
			connected = false;
			isSearched = false;
			RxBus.getDefault().post(ConnectEvent.instant().setType(ConnectEvent.SEARCHING_SSID));
		}
	}

	private void initWifiAdmin(){

		mWifiAdmin = new WifiAdmin(getHostActivity());

		mWifiAdmin.setWifiStateListener(new WifiAdmin.WifiStateListener() {
			@Override
			public void onWifiDisable() {

			}

			@Override
			public void onWifiReady() {

			}

			@Override
			public void onScanResult(List<ScanResult> wifiList, List<WifiConfiguration> WifiConfiguration) {
				if (connecting || connected) {
					return;
				}

				if (wifiList == null || wifiList.size() == 0) {
					return;
				}

				SSIDList.clear();
				for (ScanResult result : wifiList) {
					String SSID = result.SSID;
					if (judgeSSID(SSID)) {
						Logger.d("扫描到SSID :" + SSID);
						if (!SSIDList.contains(SSID)) {
							SSIDList.add(SSID);
						}
					}
				}

				if (isSearched) {
					if (SSIDList == null){
						SSIDList = new ArrayList<>();
					}
					chooseSSIDFragment.setNewData(SSIDList);
				}
			}
		});
		mWifiAdmin.setConnectionListener(new WifiAdmin.OnWifiConnectedListener() {
			@Override
			public void onWifiConnected(WifiInfo wifiInfo) {
				if (judgeSSID(wifiInfo.getSSID().replace("\"",""))){
					Logger.d("连接成功");
					if (!connected){
						changeState(SUCCESS);
						getPresenter().setCurrentSSID(ssid);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								startActivity(new Intent(getHostActivity(), MainActivity.class));
							}
						},1000);
					}
				}else {
					Logger.d("连接上了别的ssid");
					if (connecting){
						//连接失败
						changeState(FAILURE);
					}
				}
			}

			@Override
			public void onWifiDisconnected() {
				Logger.d("wifi自动断开,扫描中...");
				mWifiAdmin.startScan();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (connected){
							if (retryCount <= 3){
								retryCount++;
								Logger.d("尝试自动连接...第%d次", retryCount);
								if (!connectAP(ssid)){
									Logger.d("直接失败,放弃重连");
									retryCount = 0;
									connected = false;
									isSearched = false;
									RxBus.getDefault().post(ConnectEvent.instant().setType(ConnectEvent.WIFI_DISCONNECTED));
									chooseSSIDFragment.ssidAdapter.getData().clear();
									changeState(SEARCHING);
								}
							}else {
								Logger.d("自动重连失败");
								retryCount = 0;
								connected = false;
								isSearched = false;
								RxBus.getDefault().post(ConnectEvent.instant().setType(ConnectEvent.WIFI_DISCONNECTED));
								chooseSSIDFragment.ssidAdapter.getData().clear();
								changeState(SEARCHING);
							}
						}
					}
				},6000);

			}
		});
	}

	public void changeState(String state){
		switch (state){
			case SEARCHING :
				title.setText("正在搜索");
				center_image.setImageResource(R.mipmap.search);
				center_image.setVisibility(View.VISIBLE);
				if (!wating_git_draw.isRunning()){
					wating_git_draw.start();
				}
				mButton.setVisibility(View.GONE);
				if (!isSearched && !isCountdown) {
					Logger.d("搜索水杯AP，倒计时!");
					mWifiAdmin.startScan();
					timer = new CountDownTimer(TIMEOUT * 2, 1000) {
						@Override
						public void onTick(long millisUntilFinished) {
							int second = (int) millisUntilFinished / 1000;
							Logger.d("目前第" + second + "秒");
							if (second % 5 == 0) {
								mWifiAdmin.startScan();
							}
							if (SSIDList.size() > 0){
								if (chooseSSIDFragment == null){
									chooseSSIDFragment = ChooseSSIDFragment.newInstance(SSIDList);
									getHostActivity().addFragment(chooseSSIDFragment,"chooseSSID",false);
								}else {
									if (chooseSSIDFragment.isHidden()){
										getFragmentManager().beginTransaction().show(chooseSSIDFragment).commitAllowingStateLoss();
									}
								}
								isSearched = true;
							}
						}

						@Override
						public void onFinish() {
							isCountdown = false;
							//when time is end,we do some endding operation
							if (!isSearched) {
								Logger.d("超时");
								title.setText("搜索超时!");
								if (wating_git_draw.isRunning()){
									wating_git_draw.stop();
								}
								mButton.setVisibility(View.VISIBLE);
								center_image.setVisibility(View.GONE);
								mButton.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										changeState(SEARCHING);
									}
								});
							}else {
								if (chooseSSIDFragment.isResumed()){
									timer.start();
								}
							}
						}
					};
					isCountdown = true;
					timer.start();
				}
				break;
			case CONNECTING :
				timer.cancel();
				isCountdown = false;
				title.setText("正在连接");
				mButton.setVisibility(View.GONE);
				if (!wating_git_draw.isRunning()){
					wating_git_draw.start();
				}
				center_image.setVisibility(View.GONE);
				connecting = true;
				getFragmentManager().beginTransaction().hide(chooseSSIDFragment).show(ConnectFragment.this).commitAllowingStateLoss();
				connectAP(ssid);
				break;
			case SUCCESS :
				title.setText("连接成功");
				connecting = false;
				connected = true;
				break;
			case FAILURE :
				title.setText("连接失败");
				connecting = false;
				connected = false;
				if (wating_git_draw.isRunning()){
					wating_git_draw.stop();
				}
				center_image.setVisibility(View.GONE);
				mButton.setVisibility(View.VISIBLE);
				mButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getFragmentManager().beginTransaction().show(chooseSSIDFragment).hide(ConnectFragment.this).commitAllowingStateLoss();
						chooseSSIDFragment.ssidAdapter.getData().clear();
						mWifiAdmin.startScan();
					}
				});
				break;
		}
	}

	private boolean connectAP(final String SSID) {
		//进入连接过程
		return mWifiAdmin.connectWIFI(SSID, "LINGDIANZEROINK");
	}

	public Boolean judgeSSID(String SSID) {
		return Pattern.compile("(?i)^ZeroInk|Octopus").matcher(SSID).find();
	}

	@NonNull
	@Override
	public ConnectContract.ConnectPresenter createPresenter() {
		if (!(getHostActivity() instanceof ConnectActivity)) {
			throw new RuntimeException("宿主错误!");
		}
		return ((ConnectActivity) getHostActivity()).getComponent().getConnectPresenter();
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
	public void onDetach() {
		super.onDetach();
		mWifiAdmin.unregisterReceiver();
	}
}