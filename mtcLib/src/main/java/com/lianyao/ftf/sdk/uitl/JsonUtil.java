package com.lianyao.ftf.sdk.uitl;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {


	public static void jsonPut(JSONObject json, String key, Object value) {
		try {
			json.put(key, value);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}
}
