package com.zhuoli.onecard.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okio.ByteString;


/**
 * Created by CLD on 2016/7/28 0028.
 */
public class Util {

	public static void showSoftKeyboard(final Context context, final EditText editText) {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
			}
		}, 100);
	}

	public static void closeSoftKeyboard(Activity activity) {

		View currentFocusView = activity.getCurrentFocus();
		if (currentFocusView != null) {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(currentFocusView.getWindowToken(), 0);
		}
	}

	public static int dpToPx(int dp) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return (int) (dp * metrics.density);
	}

	public static int pxToDp(int px) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return (int) (px / metrics.density);
	}

	public static int spToPx(int sp) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
	}

	public static int pxToSp(int px) {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		return px / (int) metrics.scaledDensity;
	}

	public static int getScreenWidth(Activity activity) {

		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		return outMetrics.widthPixels;
	}

	public static int getScreenHeight(Activity activity) {

		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics outMetrics = new DisplayMetrics();
		display.getMetrics(outMetrics);

		return outMetrics.heightPixels;
	}

	public static void setIconColor(ImageView iconHolder, int color) {
		Drawable wrappedDrawable = DrawableCompat.wrap(iconHolder.getDrawable());
		DrawableCompat.setTint(wrappedDrawable, color);
		iconHolder.setImageDrawable(wrappedDrawable);
		iconHolder.invalidate();
	}

	/**
	 * Gets a reference to a given drawable and prepares it for use with tinting through.
	 *
	 * @param resId the resource id for the given drawable
	 * @return a wrapped drawable ready fo use
	 * with {@link android.support.v4.graphics.drawable.DrawableCompat}'s tinting methods
	 * @throws Resources.NotFoundException
	 */
	public static Drawable getWrappedDrawable(Context context, @DrawableRes int resId) throws Resources.NotFoundException {
		return DrawableCompat.wrap(ResourcesCompat.getDrawable(context.getResources(), resId, context.getTheme()));
	}

	public static int getColor(Context context, @ColorRes int resId) throws Resources.NotFoundException {
		return ContextCompat.getColor(context, resId);
	}

	public static void removeGlobalLayoutObserver(View view, ViewTreeObserver.OnGlobalLayoutListener layoutListener) {
		if (Build.VERSION.SDK_INT < 16) {
			view.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
		} else {
			view.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
		}
	}

	/**
	 * 将javaBean转换成Map
	 *
	 * @param bean javaBean
	 * @return Map对象
	 */
	public static HashMap<String, Object> beanToHash(Object bean){

		HashMap<String, Object> hashMap = new HashMap<>();
		Class clazz = bean.getClass();
		List<Class> clazzs = new ArrayList<>();

		do {
			clazzs.add(clazz);
			clazz = clazz.getSuperclass();
		} while (!clazz.equals(Object.class));

		for (Class iClazz : clazzs) {

			Field[] fields = iClazz.getDeclaredFields();

			try {
				for (Field field : fields) {
					Object objVal;
					field.setAccessible(true);
					objVal = field.get(bean);
					if (objVal != null){
						hashMap.put(field.getName(), objVal);
					}

				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return hashMap;
	}

	public static byte[] encodingCommand(String command,String dataHex){

		String commandHeader = "55";
		String commandLength_h;
		String commandLength_l;
		String taggedWord = "020000";
		String address = "FF";
		String crc16_h;
		String crc16_l;
		String commandFooter = "AA";

		command = ByteString.encodeUtf8(command).hex();

		String commandLength;
		String crc16;
		if (!TextUtils.isEmpty(dataHex)){
			byte[] data_ByteArray = ByteString.decodeHex(dataHex).toByteArray();
			commandLength = CryptoUtils.HEX.decToHexString(String.valueOf(data_ByteArray.length + 12),4);
			crc16 = CryptoUtils.HEX.decToHexString(CRC16.calcCrc16(data_ByteArray),4);
		}else {
			commandLength = CryptoUtils.HEX.decToHexString(String.valueOf(12),4);
			crc16 = "FFFF";
		}

		commandLength_h = commandLength.substring(0,2);
		commandLength_l = commandLength.substring(2,4);

		crc16_h = crc16.substring(0,2);
		crc16_l = crc16.substring(2,4);

		String result;
		if (!TextUtils.isEmpty(dataHex)){
			result = String.format(
					"%s%s%s%s%s%s%s%s%s%s",
					commandHeader,
					commandLength_h,
					commandLength_l,
					command,
					taggedWord,
					address,
					dataHex,
					crc16_l,
					crc16_h,
					commandFooter);
		}else {
			result = String.format(
					"%s%s%s%s%s%s%s%s%s",
					commandHeader,
					commandLength_h,
					commandLength_l,
					command,
					taggedWord,
					address,
					crc16_l,
					crc16_h,
					commandFooter);
		}


		Logger.d(result);

		return ByteString.decodeHex(result).toByteArray();
	}

	public static ByteString getData(byte[] responseValue){
		ByteString data = ByteString.of(responseValue).substring(9,responseValue.length-3);
		Logger.d(data.hex());
		return data;
	}

	public static boolean dataIsValid(byte[] responseValue){
		ByteString byteString = ByteString.of(responseValue);
		int dataLength = Integer.valueOf(CryptoUtils.HEX.hexToDecString(byteString.substring(1,3).hex()));
		if (responseValue.length != dataLength){
			return false;
		}else{
			String CRC16HexString_l = byteString.substring(responseValue.length-3,responseValue.length-2).hex();
			String CRC16HexString_h = byteString.substring(responseValue.length-2,responseValue.length-1).hex();
			String CRC16HexString = CRC16HexString_h + CRC16HexString_l;
			int responseValueCRC16 = Integer.valueOf(CryptoUtils.HEX.hexToDecString(CRC16HexString));
			ByteString data = byteString.substring(9,responseValue.length-3);
			int currentCRC16 = CRC16.calcCrc16(data.toByteArray());
			return responseValueCRC16 == currentCRC16;
		}
	}

	public static boolean writeIsSucceed(byte[] responseValue){
		ByteString byteString = ByteString.of(responseValue);
		int dataLength = Integer.valueOf(CryptoUtils.HEX.hexToDecString(byteString.substring(1,3).hex()));
		if (responseValue.length != dataLength){
			return false;
		}else {
			String message = byteString.substring(3,5).utf8();
			return TextUtils.equals(message,"OK");
		}
	}
}
