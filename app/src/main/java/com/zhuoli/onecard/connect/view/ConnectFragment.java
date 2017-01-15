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
import com.zhuoli.onecard.connect.ConnectComponent;
import com.zhuoli.onecard.connect.ConnectContract;
import com.zhuoli.onecard.connect.ConnectEvent;
import com.zhuoli.onecard.connect.view.state.ConnectViewState;
import com.zhuoli.onecard.main.MainActivity;
import com.zhuoli.onecard.utils.CountDownTimer;
import com.zhuoli.onecard.utils.Toastor;
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

	public static final String CONNECTING = "isConnecting";

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

	private ConnectComponent mComponent;

	private WifiAdmin mWifiAdmin;
	private String ssid;
	private ArrayList<String> SSIDList = new ArrayList<>();
	private boolean isConnecting = false;
	private boolean isSearched = false;
	private boolean isCountdown = false;
	private boolean isAutoDisconnect = false;
	private boolean isConnectSucceed = false;
	private boolean isConnected = false;
	private CountDownTimer timer;
	private CountDownTimer connectTimer;
	private static final long RETRY_TIMEOUT = 30000L;
	private int retryCount = 1;
	private final long TIMEOUT = 30000L;

	ChooseSSIDFragment chooseSSIDFragment;

	private Toastor mToastor;

	@Override
	protected Integer getLayoutId() {
		return R.layout.fragment_connect;
	}

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		wating_git_draw = (GifDrawable) wating_gif.getDrawable();
		mToastor = mComponent.getToastor();
		initWifiAdmin();
		initTimer();
		busSubscribe(ConnectEvent.class, new Action1<ConnectEvent>() {
			@Override
			public void call(ConnectEvent connectEvent) {
				switch (connectEvent.getType()) {
					case ConnectEvent.CHOOSE_SSID:
						String currentSsid = mWifiAdmin.getSSID().replace("\"", "");
						String selectSsid = connectEvent.getBundle().getString(ConnectEvent.KEY_CHOOSE_SSID);
						if (TextUtils.isEmpty(currentSsid)) {
							ssid = selectSsid;
							isConnecting = true;
							changeState(ConnectFragment.CONNECTING);
							connectTimer.start();
							isCountdown = true;
							connectAP(ssid);
						} else {
							if (judgeSSID(currentSsid)) {
								ssid = currentSsid;
								if (!TextUtils.equals(ssid, selectSsid)) {
									ssid = selectSsid;
									isConnecting = true;
									changeState(ConnectFragment.CONNECTING);
									connectTimer.start();
									isCountdown = true;
									connectAP(ssid);
								} else {
									timer.cancel();
									isCountdown = false;
									isConnectSucceed = true;
									isConnected = true;
									startActivity(new Intent(getHostActivity(), MainActivity.class));
									new Handler().postDelayed(new Runnable() {
										@Override
										public void run() {
											getFragmentManager().beginTransaction().hide(chooseSSIDFragment).show(ConnectFragment.this).commitAllowingStateLoss();
										}
									}, 1000);

								}
							} else {
								ssid = selectSsid;
								isConnecting = true;
								changeState(ConnectFragment.CONNECTING);
								connectTimer.start();
								isCountdown = true;
								connectAP(ssid);
							}
						}
						break;
				}
			}
		});

	}

	private void initTimer() {
		timer = new CountDownTimer(TIMEOUT, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				int second = (int) millisUntilFinished / 1000;
				Logger.d("目前第" + second + "秒");
				if (second % 5 == 0) {
					mWifiAdmin.startScan();
				}
				if (SSIDList.size() > 0) {
					if (chooseSSIDFragment == null) {
						chooseSSIDFragment = ChooseSSIDFragment.newInstance(SSIDList);
						getHostActivity().addFragment(chooseSSIDFragment, "chooseSSID", false);
					} else {
						if (chooseSSIDFragment.isHidden()) {
							getFragmentManager().beginTransaction().show(chooseSSIDFragment).commitAllowingStateLoss();
						}
					}
					isSearched = true;
				}
			}

			@Override
			public void onFinish() {
				isCountdown = false;
				if (!isSearched) {
					title.setText("搜索超时!");
					if (wating_git_draw.isRunning()) {
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
				} else {
					if (chooseSSIDFragment.isResumed()) {
						timer.start();
					}
				}
			}
		};

		connectTimer = new CountDownTimer(RETRY_TIMEOUT, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				int second = (int) millisUntilFinished / 1000;
				if (isConnectSucceed){
					if (isAutoDisconnect) {
						isAutoDisconnect = false;
						Logger.d("重新连接成功");
						mToastor.showToast("重新连接成功");
						retryCount = 1;
					}else {
						isConnected = true;
						changeState(SUCCESS);
						getPresenter().setCurrentSSID(ssid);
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								startActivity(new Intent(getHostActivity(), MainActivity.class));
							}
						}, 1000);
					}
					cancel();
					isCountdown = false;
				}else {
					if (isAutoDisconnect){
						mToastor.showToast("连接中。。。目前第" + second + "秒");
					}
				}
			}

			@Override
			public void onFinish() {
				Logger.d("连接超时！");
				isCountdown = false;
				isConnecting = false;
				isConnectSucceed = false;
				if (isAutoDisconnect){
					if (retryCount <= 3){
						mWifiAdmin.startScan();
					}else {
						Logger.d("自动重连失败");
						mToastor.showToast("自动重连失败");
						isAutoDisconnect = false;
						isConnected = false;
						retryCount = 1;
						isSearched = false;
						RxBus.getDefault().post(ConnectEvent.instant().setType(ConnectEvent.WIFI_DISCONNECTED));
						chooseSSIDFragment.ssidAdapter.getData().clear();
						changeState(SEARCHING);
					}
				}else {
					changeState(FAILURE);
					isConnected = false;
				}
				cancel();
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isCountdown && isConnected) {
			isConnecting = false;
			isConnectSucceed = false;
			isConnected = false;
			isSearched = false;
			RxBus.getDefault().post(ConnectEvent.instant().setType(ConnectEvent.SEARCHING_SSID));
		}
	}

	private void initWifiAdmin() {

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

				if (isConnecting || isConnectSucceed) {
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

				if (isAutoDisconnect) {
					if (SSIDList.contains(ssid)){
						Logger.d("尝试自动连接...");
						if (retryCount <= 3){
							if (!isCountdown){
								mToastor.showToast("WIFI自动断开！正在尝试重新连接, 第" + retryCount + "次");
								retryCount ++;
								isConnecting = true;
								connectTimer.start();
								connectAP(ssid);
								isCountdown = true;
							}
						}else {
							Logger.d("自动重连失败");
							mToastor.showToast("自动重连失败");
							isAutoDisconnect = false;
							isConnected = false;
							retryCount = 1;
							isSearched = false;
							RxBus.getDefault().post(ConnectEvent.instant().setType(ConnectEvent.WIFI_DISCONNECTED));
							chooseSSIDFragment.ssidAdapter.getData().clear();
							changeState(SEARCHING);
						}
					}else {
						if (retryCount <= 3){
							mToastor.showToast("WIFI自动断开！正在尝试重新连接, 第" + retryCount + "次");
							retryCount++;
							mWifiAdmin.startScan();
						}else {
							Logger.d("自动重连失败");
							mToastor.showToast("自动重连失败");
							isAutoDisconnect = false;
							isConnected = false;
							retryCount = 1;
							isSearched = false;
							RxBus.getDefault().post(ConnectEvent.instant().setType(ConnectEvent.WIFI_DISCONNECTED));
							chooseSSIDFragment.ssidAdapter.getData().clear();
							changeState(SEARCHING);
						}
					}

				} else if (isSearched) {
					chooseSSIDFragment.setNewData(SSIDList);
				}
			}
		});
		mWifiAdmin.setConnectionListener(new WifiAdmin.OnWifiConnectedListener() {
			@Override
			public void onWifiConnected(WifiInfo wifiInfo) {
				String currentSSID = wifiInfo.getSSID().replace("\"", "");
				if (judgeSSID(currentSSID) && TextUtils.equals(currentSSID, ssid)) {
					isConnecting = false;
					isConnectSucceed = true;
				} else {
					Logger.d("连接上了别的ssid");
					isConnecting = false;
					isConnectSucceed =false;
					if (isAutoDisconnect){
						if (retryCount <= 3) {
							mWifiAdmin.startScan();
						}else {
							Logger.d("自动重连失败");
							mToastor.showToast("自动重连失败");
							isAutoDisconnect = false;
							isConnected = false;
							retryCount = 1;
							isSearched = false;
							RxBus.getDefault().post(ConnectEvent.instant().setType(ConnectEvent.WIFI_DISCONNECTED));
							chooseSSIDFragment.ssidAdapter.getData().clear();
							changeState(SEARCHING);
						}
					}
				}
			}

			@Override
			public void onWifiDisconnected() {
				isConnectSucceed = false;
				isAutoDisconnect = true;
				Logger.d("wifi自动断开,扫描中...");
				mWifiAdmin.startScan();
			}
		});
	}

	public void changeState(String state) {
		switch (state) {
			case SEARCHING:
				title.setText("正在搜索");
				center_image.setImageResource(R.mipmap.search);
				center_image.setVisibility(View.VISIBLE);
				if (!wating_git_draw.isRunning()) {
					wating_git_draw.start();
				}
				mButton.setVisibility(View.GONE);
				if (!isSearched && !isCountdown) {
					Logger.d("搜索水杯AP，倒计时!");
					mWifiAdmin.startScan();
					isCountdown = true;
					timer.start();
				}
				break;
			case CONNECTING:
				timer.cancel();
				isCountdown = false;
				title.setText("正在连接");
				mButton.setVisibility(View.GONE);
				if (!wating_git_draw.isRunning()) {
					wating_git_draw.start();
				}
				center_image.setVisibility(View.GONE);
				getFragmentManager().beginTransaction().hide(chooseSSIDFragment).show(ConnectFragment.this).commitAllowingStateLoss();
				break;
			case SUCCESS:
				title.setText("连接成功");
				break;
			case FAILURE:
				title.setText("连接失败");
				if (wating_git_draw.isRunning()) {
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
		mComponent = ((ConnectActivity) getHostActivity()).getComponent();
		return mComponent.getConnectPresenter();
	}
	
	@NonNull
	@Override
	public ViewState createViewState() {
		return new ConnectViewState();
	}

	@Override
	public void onNewViewStateInstance() {
		RxBus.getDefault().post(ConnectEvent.instant().setType(ConnectEvent.SEARCHING_SSID));
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mWifiAdmin.unregisterReceiver();
	}
}