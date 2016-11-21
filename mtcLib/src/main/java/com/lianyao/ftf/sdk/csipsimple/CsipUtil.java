package com.lianyao.ftf.sdk.csipsimple;

import org.pjsip.pjsua.pj_str_t;

import com.lianyao.ftf.sdk.uitl.MtcLog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class CsipUtil {
	/**
	 * pj_str_t 转String
	 * 
	 * @param pjStr
	 * @return
	 */
	public static String pjStrToString(pj_str_t pjStr) {
		try {
			if (pjStr != null) {
				int len = pjStr.getSlen();
				if (len > 0 && pjStr.getPtr() != null) {
					if (pjStr.getPtr().length() < len) {
						len = pjStr.getPtr().length();
					}
					if (len > 0) {
						return pjStr.getPtr().substring(0, len);
					}
				}
			}
		} catch (StringIndexOutOfBoundsException e) {
			MtcLog.e("Impossible to retrieve string from pjsip :" + e);
		}
		return "";
	}

	/**
	 *
	 */
	public static String uriToCallee(String uri) {
		uri = uri.substring(uri.indexOf(":") + 1, uri.lastIndexOf("@"));
		return uri;
	}

	/**
	 * If that's the official -not custom- user agent, send the release, // the
	 * device and the api level
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getUserAgent(Context ctx) {
		String userAgent = null;
		try {
			PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(
					ctx.getPackageName(), 0);
			if (pinfo != null) {
				userAgent = "sipDemo" + android.os.Build.DEVICE + "-"
						+ android.os.Build.VERSION.SDK_INT + "/r"
						+ pinfo.versionCode;
			}
		} catch (NameNotFoundException e) {

		}
		return userAgent;
	}

}
