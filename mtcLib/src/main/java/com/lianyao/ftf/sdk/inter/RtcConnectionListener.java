package com.lianyao.ftf.sdk.inter;

/**
 * 连接监听
 *
 */
public interface RtcConnectionListener {
    public void onDisconnected(int error);

    public void onConnected();

}
