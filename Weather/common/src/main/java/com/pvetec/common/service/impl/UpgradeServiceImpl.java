package com.pvetec.common.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pvetec.common.ResultParaser;
import com.pvetec.common.bean.AppBean;
import com.pvetec.common.bean.AppUpgradeBean;
import com.pvetec.common.service.UpgradeService;

import org.json.JSONException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class UpgradeServiceImpl implements UpgradeService {

    public AppUpgradeBean getUpgrade(String url, String appPackage, int sdk, int versionCode, int platform) throws JSONException {
        String resultJson = getUpgradeListJson(url, appPackage, sdk, versionCode,platform);
        return new ResultParaser<AppUpgradeBean>() {
            @Override
            public AppUpgradeBean parseData(String jsonData) {
                Gson g = new Gson();
                AppUpgradeBean data = g.fromJson(jsonData, new TypeToken<AppUpgradeBean>() {
                }.getType());
                if (data == null) return null;
                AppUpgradeBean.bulidUrl(data);
                return data;
            }
        }.parse(resultJson).getData();
    }

    private String getUpgradeListJson(String url, String appPackage, int sdk, int versionCode, int platform) {
        RequestParams requestParams = new RequestParams(url);
        requestParams.addParameter("appPackage", appPackage);
        requestParams.addParameter("sdk", sdk);
        requestParams.addParameter("versionCode", "" + versionCode);
        requestParams.addParameter("platform", "" + platform);
        try {
            return x.http().postSync(requestParams, String.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<AppUpgradeBean> getUpgradeList(String url, int sdk, List<AppBean> appList, int platform) {
        RequestParams requestParams = new RequestParams(url);
        Gson g = new Gson();
        requestParams.addParameter("sdk", sdk);
        requestParams.addParameter("applist", g.toJson(appList));
        requestParams.addParameter("platform", "" + platform);
        try {
            String resultJson = x.http().postSync(requestParams, String.class);
            return new ResultParaser<List<AppUpgradeBean>>() {
                @Override
                public List<AppUpgradeBean> parseData(String jsonData) {
                    Gson g = new Gson();
                    List<AppUpgradeBean> data = g.fromJson(jsonData, new TypeToken<List<AppUpgradeBean>>() {
                    }.getType());
                    if (data == null) return data;
                    for (int i = 0; i < data.size(); i++) {
                        AppUpgradeBean.bulidUrl(data.get(i));
                    }
                    return data;
                }
            }.parse(resultJson).getData();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String checkUpgrade(String appPackage) {
        return null;
    }

}
