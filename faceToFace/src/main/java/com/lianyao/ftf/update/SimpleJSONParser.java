package com.lianyao.ftf.update;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SimpleJSONParser implements ResponseParser {

	@Override
	public Version parser(String response) {
		try {
			JSONTokener jsonParser = new JSONTokener(response);
			JSONObject json = (JSONObject) jsonParser.nextValue();
			Version version = null;
			int code = json.getInt("code");
			String name = json.getString("version");
			String feature = json.getString("content");
			String targetUrl = json.getString("downloadUrl");
			version = new Version(code, name, feature, targetUrl);
			return version;
		} catch (JSONException exp) {
			exp.printStackTrace();
			return null;
		}
	}

}
