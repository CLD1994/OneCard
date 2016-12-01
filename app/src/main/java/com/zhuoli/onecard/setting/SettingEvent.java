package com.zhuoli.onecard.setting;

import android.os.Bundle;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CLD
 */
public class SettingEvent {

	public static final int CHANGE_COUNTER_WAY = 0;


	@IntDef({CHANGE_COUNTER_WAY})
	@Retention(RetentionPolicy.SOURCE)
	public @interface SettingType {
	}

	private int mType;
	private Bundle mBundle;
	
	public static SettingEvent instant() {
		return new SettingEvent();
	}
	
	public int getType() {
		return mType;
	}
	
	public SettingEvent setType(@SettingType int type) {
		mType = type;
		return this;
	}
	
	public Bundle getBundle() {
		return mBundle;
	}

	public SettingEvent setBundle(Bundle bundle) {
		mBundle = bundle;
		return this;
	}
	

}