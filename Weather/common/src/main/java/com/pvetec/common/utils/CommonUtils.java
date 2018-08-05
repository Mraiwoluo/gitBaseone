package com.pvetec.common.utils;

import android.content.Context;
import android.content.Intent;

import com.pvetec.common.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /*
     * 注意返回一个-1值 str可能是-1
     */
    public static int Str2Int(String str) {
        try {
            return Integer.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void sendAppExit(Context context, String appName) {
        try {
            Intent intent = new Intent();
            intent.setAction(Constants.ACTION_APP_EXIT);
            intent.putExtra(Constants.STRING_EXTRAS_APP_NAME, appName);
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
