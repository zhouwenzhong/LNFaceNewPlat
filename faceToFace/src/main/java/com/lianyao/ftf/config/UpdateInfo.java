package com.lianyao.ftf.config;

/**
 * Created by zhouwz on 16/11/5.
 */

public class UpdateInfo
{
    private int code;
    private String name;
    private String feature;
    private String targetUrl;

    public UpdateInfo() {
        code = 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

}