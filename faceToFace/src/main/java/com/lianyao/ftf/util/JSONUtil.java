package com.lianyao.ftf.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.lianyao.ftf.BuildConfig;

public class JSONUtil {
	
	private static String sClassName;

	private static String sMethodName;

	private static int sLineNumber;

	private static void getMethodNames(final StackTraceElement[] sElements) {
		sClassName = sElements[1].getFileName();
		sMethodName = sElements[1].getMethodName();
		sLineNumber = sElements[1].getLineNumber();
	}
	
	public static boolean isDebuggable() {
		return BuildConfig.DEBUG;
	}
	
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
	
	// JSON添加
	public static void putJson(JSONObject json, String key, String value) {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("json新增String异常：" + e));
		}
	}

	public static void putJson(JSONObject json, String key, boolean value) {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("json新增boolean异常：" + e));
		}
	}

	public static void putJson(JSONObject json, String key, int value) {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("json新增int异常：" + e));
		}
	}

	public static void putJson(JSONObject json, String key, JSONArray value) {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("json新增jsonarray异常：" + e));
		}
	}
	
	public static void putJson(JSONObject json, String key, JSONObject value) {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("json新增jsonObject异常：" + e));
		}
	}

	// JSON取值
	public static JSONArray getArrayFromJSON(JSONObject json, String key) {
		try {
			return json.getJSONArray(key);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return new JSONArray();
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("jsonobject取值JSONArray异常：" + e));
			return new JSONArray();
		}
	}

	public static String getStringFromJson(JSONObject json, String key) {
		try {
			String returnValue = json.getString(key);
			if (returnValue == null || "null".equals(returnValue)) {
				return "";
			}
			return returnValue;
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return "";
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("jsonobject取值String异常：" + e));
			return "";
		}
	}

	public static boolean getBoolFromJson(JSONObject json, String key) {
		try {
			return json.getBoolean(key);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return false;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONObject取值Bool错误：" + e));
			return false;
		}
	}

	public static int getIntFromJson(JSONObject json, String key) {
		try {
			return json.getInt(key);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return -1;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONObject取值int错误：" + e));
			return -1;
		}
	}

	public static double getDoubleFromJson(JSONObject json, String key) {
		try {
			return json.getDouble(key);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return -1L;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONObject取值Double错误：" + e));
			return -1L;
		}
	}

	public static JSONObject getJsonFromJson(JSONObject json, String key) {
		try {
			return json.getJSONObject(key);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return new JSONObject();
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONObject取值JSONObject错误：" + e));
			return new JSONObject();
		}
	}

	public static JSONObject getJsonFromString(String str) {
		try {
			return new JSONObject(str);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return new JSONObject();
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("String取值JSONObject错误：" + e));
			return new JSONObject();
		}
	}

	// JSONArray添加
	public static void putArray(JSONArray array, int position, String value) {
		try {
			array.put(position, value);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return ;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray新增异常：" + e));
		}
	}

	public static void putArray(JSONArray array, int position, boolean value) {
		try {
			array.put(position, value);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return ;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray新增boolean异常：" + e));
		}
	}

	public static void putArray(JSONArray array, int position, JSONObject value) {
		try {
			array.put(position, value);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return ;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray新增JSONObject异常：" + e));
		}
	}
	
	public static void putArray(JSONArray array, int position, JSONArray value) {
		try {
			array.put(position, value);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return ;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray新增JSONArray异常：" + e));
		}
	}

	public static JSONArray getArrayFromString(String str) {
		try {
			return new JSONArray(str);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return new JSONArray();
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray的New操作异常：" + e));
			return new JSONArray();
		}
	}

	// JSONArray取值
	public static String getStringFromArray(JSONArray array, int position) {
		try {
			return array.getString(position);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return "";
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray取值String错误：" + e));
			return "";
		}
	}

	public static boolean getBoolFromArray(JSONArray array, int position) {
		try {
			return array.getBoolean(position);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return false;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray取值Bool错误：" + e));
			return false;
		}
	}

	public static int getIntFromArray(JSONArray array, int position) {
		try {
			return array.getInt(position);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return -1;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray取值int错误：" + e));
			return -1;
		}
	}

	public static double getDoubleFromArray(JSONArray array, int position) {
		try {
			return array.getDouble(position);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return -1L;
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray取值double错误：" + e));
			return -1L;
		}
	}

	public static JSONArray getArrayFromArray(JSONArray array, int position) {
		try {
			return array.getJSONArray(position);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return new JSONArray();
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray取值JSONArray错误：" + e));
			return new JSONArray();
		}
	}

	public static JSONObject getJsonFromArray(JSONArray array, int position) {
		try {
			return array.getJSONObject(position);
		} catch (JSONException e) {
			if (!isDebuggable()) {
				return new JSONObject();
			}
			getMethodNames(new Throwable().getStackTrace());
			Log.e(sClassName, createLog("JSONArray取值JSONObject错误：" + e));
			return new JSONObject();
		}
	}

}
