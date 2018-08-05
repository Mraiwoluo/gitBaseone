package com.pvetec.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2017/3/29 0029.
 */

public class NetWorkUtils {

    //判断是否有网络连接
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断MOBILE网络是否可用
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //判断网络类型
    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

            if (mMobileNetworkInfo != null&& mMobileNetworkInfo.isAvailable()) {
                return mMobileNetworkInfo.getType();
            }
        }
        return -1; //无网络
    }






}
