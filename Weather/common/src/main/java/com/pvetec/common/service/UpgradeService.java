package com.pvetec.common.service;

import com.pvetec.common.bean.AppBean;
import com.pvetec.common.bean.AppUpgradeBean;

import org.json.JSONException;

import java.util.List;

public interface UpgradeService {
    
    public String checkUpgrade(String appPackage);

    /**
     * 获取当前版本可升级的版本号
     * @param url
     * @param appPackage
     * @param versionCode
     * @return
     * @throws JSONException
     */
    public AppUpgradeBean getUpgrade(String url, String appPackage, int sdk, int versionCode, int platform) throws JSONException;
    
    
    /**
     * 获取程序是否有可升级
     * @param appList
     * @return
     */
    public List<AppUpgradeBean> getUpgradeList(String url, int sdk, List<AppBean> appList, int platform);
    
   
}
