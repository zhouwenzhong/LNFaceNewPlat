package com.lianyao.ftf.sdk.layered;

import android.content.Context;
import android.hardware.Camera;

/**
 * 设备监测辅助类
 */
public class RtcDevCheckManager {
    /** Check if this device has a camera */
    public static boolean checkCameraHardware(Context context) {
        int cameraNumber = Camera.getNumberOfCameras();
        if(cameraNumber > 0) return true;

        return false;
    }
}
