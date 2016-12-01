package com.zhuoli.onecard.connect;

import android.os.Bundle;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CLD
 */
public class ConnectEvent {

	public static final int SEARCHING_SSID = 0;
	public static final int CHOOSE_SSID = 1;
	public static final int WIFI_DISCONNECTED = 2;
	public static String KEY_CHOOSE_SSID = "SSID";

	@IntDef({CHOOSE_SSID,SEARCHING_SSID,WIFI_DISCONNECTED})
	@Retention(RetentionPolicy.SOURCE)
	public @interface ConnectType {}

	private int mType;
	private Bundle mBundle;
	
	public static ConnectEvent instant() {
		return new ConnectEvent();
	}
	
	public int getType() {
		return mType;
	}
	
	public ConnectEvent setType(@ConnectType int type) {
		mType = type;
		return this;
	}
	
	public Bundle getBundle() {
		return mBundle;
	}

	public ConnectEvent setBundle(Bundle bundle) {
		mBundle = bundle;
		return this;
	}
}