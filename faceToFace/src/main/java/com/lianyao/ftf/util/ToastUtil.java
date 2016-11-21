package com.lianyao.ftf.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	/**
	 * 短时间显示Toast
	 */
	public static void showShort(Context context, CharSequence message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 短时间显示Toast
	 */
	public static void showShort(Context context, int message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 长时间显示Toast
	 */
	public static void showLong(Context context, CharSequence message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * 长时间显示Toast
	 */
	public static void showLong(Context context, int message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * 自定义显示Toast时间
	 */
	public static void show(Context context, CharSequence message, int duration) {
		Toast.makeText(context, message, duration).show();
	}

	/**
	 * 自定义显示Toast时间
	 */
	public static void show(Context context, int message, int duration) {
		Toast.makeText(context, message, duration).show();
	}

}
