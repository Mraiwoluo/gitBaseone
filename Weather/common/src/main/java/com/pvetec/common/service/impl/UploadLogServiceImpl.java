package com.pvetec.common.service.impl;

import com.pvetec.common.service.UploadLogService;

import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UploadLogServiceImpl implements UploadLogService {
    /**
     * 
     * 同步上传日志
     * @return
     */
    public  boolean uploadLogFile(String url, String appPkg, int appVer, String platVer, String osVer, int priority, String deviceId, String tag, String msg) {
        RequestParams requestParams = new RequestParams(url);
        requestParams.addParameter("tag", tag);
        requestParams.addParameter("message", msg);
        requestParams.addParameter("app_package", appPkg);
        requestParams.addParameter("app_version", "" + appVer);
        requestParams.addParameter("platform_version", platVer);
        requestParams.addParameter("os_version", osVer);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date());
        requestParams.addParameter("write_time", dateString);
        requestParams.addParameter("priority", "" + priority);
        requestParams.addParameter("deviceId", "" + deviceId);
        requestParams.setConnectTimeout(1000);
        try {
            String date = x.http().getSync(requestParams, String.class);
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
