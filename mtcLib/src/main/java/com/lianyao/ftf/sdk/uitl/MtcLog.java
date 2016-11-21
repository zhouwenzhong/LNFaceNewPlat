package com.lianyao.ftf.sdk.uitl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

public class MtcLog {
	/**
     * 
     */
	private static String sClassName;

	/**
     * 
     */
	private static String sMethodName;

	/**
     * 
     */
	private static int sLineNumber;

	public static void d(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		log(tag, Level.DEBUG, msg);
	}

	public static void e(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		log(tag, Level.ERROR, msg);
	}

	public static void i(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		log(tag, Level.INFO, msg);
	}

	public static void v(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		log(tag, Level.VERBOSE, msg);
	}

	public static void w(String tag, String msg) {
		if (!isDebuggable()) {
			return;
		}
		log(tag, Level.WARN, msg);
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isDebuggable() {
		return isDEBUG();
	}

	private static boolean DEBUG=true;
	
	public static boolean isDEBUG() {
		return DEBUG;
	}

	public static void setDEBUG(boolean dEBUG) {
		DEBUG = dEBUG;
	}

	public static boolean isSaveLog = true;
	private static enum Level {
		VERBOSE, DEBUG, INFO, WARN, ERROR
	};

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
		getMethodNames(new Throwable().getStackTrace());
		log(sClassName, Level.ERROR, createLog(message));
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
		log(sClassName, Level.INFO,  createLog(message));
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
		log(sClassName, Level.DEBUG, createLog(message));
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
		log(sClassName, Level.VERBOSE, createLog(message));
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
		log(sClassName, Level.WARN, createLog(message));
	}

	private static void log(String tag, Level level, String str) {
		if (isDebuggable()) {
			switch (level) {
				case VERBOSE:
					Log.v(tag, str);
					break;
				case DEBUG:
					Log.d(tag, str);
					break;
				case INFO:
					Log.i(tag, str);
					break;
				case WARN:
					Log.w(tag, str);
					break;
				case ERROR:
					Log.e(tag, str);
					break;
			}
			if (isSaveLog) {
				if (level.equals(Level.VERBOSE)) {
					writeLogToFile("[VERBOSE] " + str);
				} else if (level.equals(Level.DEBUG)) {
					writeLogToFile("[DEBUG] " + str);
				} else if (level.equals(Level.INFO)) {
					writeLogToFile("[INFO] " + str);
				} else if (level.equals(Level.WARN)) {
					writeLogToFile("[WARN] " + str);
				} else if (level.equals(Level.ERROR)) {
					writeLogToFile("[ERROR] " + str);
				}
			}
		}
	}

	/**
	 * The path of log file in sdcard, the path of sdk is represented by
	 * Environment.getExternalStorageDirectory().getPath(). Default value is
	 * Environment.getExternalStorageDirectory().getPath() +
	 * "/Android/data/com.suntek.mway.rcs/logs".
	 */
	public static String APP_LOG_ROOT_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/mtclib/sdk_log";

	private static SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
			Locale.getDefault());

	private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd_HH",
			Locale.getDefault());

	/**
	 * 将日志写入文件
	 */
	public synchronized static void writeLogToFile(String text) {
		if (text == null || "".equals(text.trim())) {
			return;
		}

		File filePath = new File(APP_LOG_ROOT_PATH);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		String date = logfile.format(new Date(System.currentTimeMillis()));
		Date nowtime = new Date();
		String fileName = "mtclib-sdk-" + date + ".log";
		String needWriteMessage = "\n" + logDateFormat.format(nowtime) + ": " + text;
		File file = new File(APP_LOG_ROOT_PATH, fileName);
		FileWriter filerWriter = null;
		BufferedWriter bufWriter = null;
		try {
			filerWriter = new FileWriter(file, true);
			bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(needWriteMessage);
			bufWriter.flush();
		} catch (IOException e) {
			// Nothing to do
		} finally {
			try {
				if (filerWriter != null) {
					filerWriter.close();
				}
				if (bufWriter != null) {
					bufWriter.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
