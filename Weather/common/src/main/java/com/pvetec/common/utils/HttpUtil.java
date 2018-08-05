package com.pvetec.common.utils;

import org.json.JSONObject;
import org.xutils.common.Callback.Cancelable;
import org.xutils.common.Callback.ProgressCallback;
import org.xutils.http.RequestParams;
import org.xutils.http.app.HttpRetryHandler;
import org.xutils.x;

import java.io.File;

public class HttpUtil {

    public static String downLoadFile(String url, String savePath) {
        RequestParams params = new RequestParams(url);
        params.setSaveFilePath(savePath);
        params.setCharset("utf-8");
        try {
            File downFile = x.http().getSync(params, File.class);
            if (downFile == null) return null;
            return downFile.getAbsolutePath();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Cancelable downLoadFile(String url, String savePath, HttpRetryHandler httpRetryHandler, final ProgressCallback<File> callback) {
        RequestParams params = new RequestParams(url);
        params.setCharset("utf-8");
        params.setAutoResume(true);
        params.setCancelFast(true);
        params.setMaxRetryCount(5);
        if (httpRetryHandler != null) params.setHttpRetryHandler(httpRetryHandler);
        params.setCacheMaxAge(1000 * 60 * 60);
        params.setSaveFilePath(savePath);
        try {
            Cancelable xCancel = x.http().get(params, callback);
            return xCancel;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean uploadFile(String url, File file) {
        RequestParams requestParams = new RequestParams(url);
        requestParams.setMultipart(true);
        requestParams.setCharset("utf-8");
        requestParams.addBodyParameter("upfile", file);
        try {
            String date = x.http().postSync(requestParams, String.class);
            if (date == null) return false;
            JSONObject jsonObject = new JSONObject(date);
            int resultCode = jsonObject.getInt("code");
            return resultCode == 0;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

}
