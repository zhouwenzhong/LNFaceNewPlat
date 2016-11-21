package com.lianyao.ftf.sdk.uitl;

/**
 * Created by zhouwz on 16/7/15.
 */
public class StringUtil {
    public static boolean isNullOrEmpety(String str) {
        if(str == null || str.length() == 0) {
            return true;
        }

        return  false;
    }

    public static String getUidFromTid(String tid) {
        String[] strings = tid.split("_");
        if (strings.length > 2) {
            return strings[0] + "_" + strings[1];
        }

        return tid;
    }

    public static String getMobileFromUid(String tid) {
        String[] strings = tid.split("_");
        if (strings.length > 1) {
            return strings[1];
        }

        return tid;
    }

    public static String getUidFromMobile(String mobile, String appId) {
        return appId + "_" + mobile;
    }
}
