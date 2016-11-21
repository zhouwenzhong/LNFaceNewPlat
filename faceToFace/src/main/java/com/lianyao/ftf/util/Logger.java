package com.lianyao.ftf.util;

import android.util.Log;

import com.lianyao.ftf.BuildConfig;

public class Logger {

	private static String sClassName;

	private static String sMethodName;

	private static int sLineNumber;
	
	public static void d(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		Log.e(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		Log.v(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		Log.w(tag, msg);
	}
	
	/**
	 * 
	 * @param sElements
	 */
	private static void getMethodNames(final StackTraceElement[] sElements) {
		sClassName = sElements[1].getFileName();
		sMethodName = sElements[1].getMethodName();
		sLineNumber = sElements[1].getLineNumber();
	}

	/**
	 * 
	 * @param message
	 */
	public static void e(final String message) {
		if (!isDebuggable()) {
			return;
		}

		// Throwable instance must be created before any methods
		getMethodNames(new Throwable().getStackTrace());
		Log.e(sClassName, createLog(message));
	}

	/**
	 * 
	 * @param message
	 */
	public static void i(final String message) {
		if (!isDebuggable()) {
			return;
		}

		getMethodNames(new Throwable().getStackTrace());
		Log.i(sClassName, createLog(message));
	}

	/**
	 * 
	 * @param message
	 */
	public static void d(final String message) {
		if (!isDebuggable()) {
			return;
		}

		getMethodNames(new Throwable().getStackTrace());
		Log.d(sClassName, createLog(message));
	}

	/**
	 * 
	 * @param message
	 */
	public static void v(String message) {
		if (!isDebuggable()) {
			return;
		}

		getMethodNames(new Throwable().getStackTrace());
		Log.v(sClassName, createLog(message));
	}

	/**
	 * 
	 * @param message
	 */
	public static void w(String message) {
		if (!isDebuggable()) {
			return;
		}

		getMethodNames(new Throwable().getStackTrace());
		Log.w(sClassName, createLog(message));
	}
	
	/**
	 * 
	 * @param log
	 * @return
	 */
	private static String createLog(final String log) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(sMethodName);
		buffer.append(":");
		buffer.append(sLineNumber);
		buffer.append("]");
		buffer.append(log);

		return buffer.toString();
	}

	public static boolean isDebuggable() {
		return BuildConfig.DEBUG;
	}

}
