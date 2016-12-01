package com.zhuoli.onecard.main;

import android.os.Bundle;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CLD
 */
public class MainEvent {

	public static final int CLOSE = 0;
	public static final int OTHER_SETTING = 1;

	public static final String PASSWORD = "password";

	private int mType;
	private Bundle mBundle;
	
	public static MainEvent instant() {
		return new MainEvent();
	}
	
	public int getType() {
		return mType;
	}
	
	public MainEvent setType(@MainType int type) {
		mType = type;
		return this;
	}
	
	public Bundle getBundle() {
		return mBundle;
	}

	public MainEvent setBundle(Bundle bundle) {
		mBundle = bundle;
		return this;
	}
	
	@IntDef({CLOSE, OTHER_SETTING})
	@Retention(RetentionPolicy.SOURCE)
	public @interface MainType {
	}
}