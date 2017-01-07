package com.zhuoli.onecard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.List;

public class WifiAdmin {

    //上下文
    private Context context;

    //广播接受者
    private BroadcastReceiver mReceiver;

    //定义ConnectivityManager对象
    private ConnectivityManager mConnectivityManager;

    // 定义WifiManager对象  
    private WifiManager mWifiManager;
    // 定义WifiInfo对象  
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表  
    private List<ScanResult> mWifiList;
    // 网络连接列表  
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock  
    private WifiManager.WifiLock mWifiLock;

    //回调接口
    private WifiStateListener mListener;

    private OnWifiConnectedListener connectionListener;



    // 构造器  
    public WifiAdmin(Context context) {

        this.context = context;

        // 取得WifiManager对象  
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        mReceiver = new BroadcastReceiver() {

            private boolean WIFIIsChange = false;

            private boolean WIFIIsDisconnected = false;
            @Override
            public void onReceive(Context context, Intent intent) {

                try {
                    final String action = intent.getAction();
                    // wifi已成功扫描到可用wifi。
                    if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {

                        mWifiList = mWifiManager.getScanResults();
                        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
                        if (mListener != null) {
                            mListener.onScanResult(mWifiList, mWifiConfiguration);
                        }

                    }
                    //WIFI网卡的状态
                    else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                        if (mListener != null) {
                            switch (wifiState) {
                                case WifiManager.WIFI_STATE_ENABLED:
                                    mWifiInfo = mWifiManager.getConnectionInfo();
                                    mListener.onWifiReady();
                                    break;
                                case WifiManager.WIFI_STATE_DISABLED:
                                    mWifiInfo = null;
                                    mListener.onWifiDisable();
                                    break;
                            }
                        }
                    }
                    //WIFI连接的状态
                    else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                        Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        if (parcelableExtra != null) {
                            NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                            NetworkInfo.DetailedState state = networkInfo.getDetailedState();
                            if (state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                                 WIFIIsChange = true;
                            } else if (state == NetworkInfo.DetailedState.CONNECTED && WIFIIsChange) {
                                Logger.d("CONNECTED");
                                WIFIIsChange = false;
                                mWifiInfo = mWifiManager.getConnectionInfo();
                                if (connectionListener != null) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                Network[] networks = mConnectivityManager.getAllNetworks();
                                                for (Network network : networks) {
                                                    String typeName = mConnectivityManager.getNetworkInfo(network).getTypeName();
                                                    if (TextUtils.equals(typeName, "WIFI")) {
                                                        mConnectivityManager.bindProcessToNetwork(network);
                                                    }
                                                }
                                            }
                                            connectionListener.onWifiConnected(mWifiInfo);
                                        }
                                    }, 3000);
                                }
                            } else if (state == NetworkInfo.DetailedState.DISCONNECTED){
	                            Logger.d("DISCONNECTED  " + networkInfo.getExtraInfo());
//	                            connectionListener.onWifiDisconnected(ssid);
	                            Logger.d("原因：" + networkInfo.getReason());
	                            switch (networkInfo.getReason()){
		                            case "0":
			                            //失去信号 自动断开
			                            break;
		                            case "3":
			                            //程序内部修改
			                            break;
	                            }
                            } else if (state == NetworkInfo.DetailedState.SCANNING){
	                            Logger.d("SCANNING  " + networkInfo.getExtraInfo());
                            }
                        }
                    }
                } catch(SecurityException e){
                    e.printStackTrace();
                }
            }

        };

        registerBroadcast();
    }

    /**
     * 注册广播
     */
    public void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        //监听WIFI网卡状态的改变
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //监听WIFI网卡扫描结果的改变
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //监听WIFI连接状态的改变
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(mReceiver, filter);
    }
 
    // 打开WIFI  
    public void openWifi() { 
        if (!mWifiManager.isWifiEnabled()) { 
            mWifiManager.setWifiEnabled(true); 
        } 
    } 
 
    // 关闭WIFI  
    public void closeWifi() { 
        if (mWifiManager.isWifiEnabled()) { 
            mWifiManager.setWifiEnabled(false); 
        } 
    } 
 
    // 检查当前WIFI状态  
    public int checkState() { 
        return mWifiManager.getWifiState(); 
    } 
 
    // 锁定WifiLock  
    public void acquireWifiLock() { 
        mWifiLock.acquire(); 
    } 
 
    // 解锁WifiLock  
    public void releaseWifiLock() { 
        // 判断时候锁定  
        if (mWifiLock.isHeld()) { 
            mWifiLock.release();
        } 
    } 
 
    // 创建一个WifiLock  
    public void creatWifiLock() { 
        mWifiLock = mWifiManager.createWifiLock("Test");
    } 
 
    // 得到配置好的网络  
    public List<WifiConfiguration> getConfiguration() { 
        return mWifiConfiguration; 
    } 
 
    // 指定配置好的网络进行连接  
    public void connectConfiguration(int index) { 
        // 索引大于配置好的网络索引返回  
        if (index > mWifiConfiguration.size()) { 
            return; 
        } 
        // 连接配置好的指定ID的网络  
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    } 

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    public void startScan(){
        mWifiManager.startScan();
    }

    public String getSSID(){
        return (mWifiInfo == null) ? "" : mWifiInfo.getSSID();
    }

    // 得到MAC地址  
    public String getMacAddress() { 
        return (mWifiInfo == null) ? "" : mWifiInfo.getMacAddress();
    }
    // 得到接入点的BSSID  
    public String getBSSID() { 
        return (mWifiInfo == null) ? "" : mWifiInfo.getBSSID();
    } 
 
    // 得到IP地址  
    public int getIPAddress() { 
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress(); 
    } 
 
    // 得到连接的ID  
    public int getNetworkId() { 
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId(); 
    } 
 
    // 得到WifiInfo的所有信息包  
    public String getWifiInfo() { 
        return (mWifiInfo == null) ? "" : mWifiInfo.toString();
    } 

    // 断开指定ID的网络  
    public void disconnectWifi(int netId) { 
        mWifiManager.disableNetwork(netId); 
        mWifiManager.disconnect();
    }


    // 添加一个网络并连接
    public boolean addNetwork(ScanResult result,String Password) {
        WifiConfiguration config = createConfiguration(result,Password);
        int id = mWifiManager.addNetwork(config);
        if (id != -1){
            return mWifiManager.enableNetwork(id,true);
        }else {
            return false;
        }
    }

    public boolean connectWIFI(ScanResult result,String Password){
        //查询本地配置信息
        WifiConfiguration localConfig = this.IsExsits(result.SSID);
        //如果本地已有配置信息
        if(localConfig != null) {
            //直接连接
            if (mWifiManager.enableNetwork(localConfig.networkId,true)){
                Logger.d("已经保存配置,直接连接");
                return true;
            }
            else {
                Logger.d("配置信息失效,移除");
                //配置信息失效,移除
                mWifiManager.removeNetwork(localConfig.networkId);

                return addNetwork(result,Password);
            }
        }else {
            Logger.d("第一次连接,新建配置");
            return addNetwork(result,Password);
        }
    }

    public boolean connectWIFI(String SSID, String Password){
        for (ScanResult result : mWifiList) {
            //如果扫描结果里有
            if (TextUtils.equals(result.SSID,SSID)){
                return connectWIFI(result,Password);
            }
        }
        return false;
    }

    private WifiConfiguration createConfiguration(ScanResult result,String Password){

        //创建配置
        WifiConfiguration config = new WifiConfiguration();
        //配置SSID,SSID必须加双引号
        config.SSID = "\"" + result.SSID + "\"";
        //配置状态为可用
        config.status = WifiConfiguration.Status.ENABLED;
        //显示SSID
        config.hiddenSSID = false;
        //未知
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        //判读加密类型
        if (result.capabilities.contains("WPA")){

            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            if (result.capabilities.contains("PSK")){
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            }else {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
            }
            config.preSharedKey = "\""+Password+"\"";
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        }else if (result.capabilities.contains("WEP")){

            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepKeys[0]= "\""+Password+"\"";
            config.wepTxKeyIndex = 0;

            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

        }else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        return config;
    }

    private WifiConfiguration IsExsits(String SSID)  
    {  
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();  
           for (WifiConfiguration existingConfig : existingConfigs)   
           {  
             if (existingConfig.SSID.equals("\""+SSID+"\""))  
             {  
                 return existingConfig;  
             }  
           }  
        return null;   
    }

    public void unregisterReceiver(){
        context.unregisterReceiver(mReceiver);
    }

    public interface WifiStateListener{
        void onWifiDisable();
        void onWifiReady();
        void onScanResult(List<ScanResult> WifiList, List<WifiConfiguration> WifiConfiguration);
    }

    public interface OnWifiConnectedListener{
        void onWifiConnected(WifiInfo wifiInfo);
        void onWifiDisconnected(String nowConnectedSsid);
    }

    public void setWifiStateListener(WifiStateListener Listener) {
        this.mListener = Listener;
    }
    public void setConnectionListener(OnWifiConnectedListener connectionListener){
        this.connectionListener = connectionListener;
    }
}