package com.lianyao.ftf.util.http;


import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.exception.HttpException;

public interface RestInterface {
	
	void onLoading(long total, long current, boolean isUploading);
	
    void onSuccess(JSONObject json);
    
    public void onFailure(HttpException error, String msg);
    
}
