package com.pvetec.common.core;

import android.app.Application;

import com.pvetec.common.Constants;
import com.pvetec.common.service.impl.LogServiceImpl;
import com.pvetec.common.utils.AndroidUtil;

import org.xutils.x;

public class Core {

    /**
     * //初始化异常处理系统 //初始化日志系统 //TODO:在app中create中初始化
     * 
     * @param app
     */
    public static void init(Application app, String appName) {
        init(app, appName, null);
    }

    public static void init(Application app) {
        init(app, null, null);
    }

    /**
     * //初始化异常处理系统 //初始化日志系统 //TODO:在app中create中初始化
     * 
     * @param app
     */
    public static void init(Application app, String appName, LogServiceImpl.LogConfig config) {
        /**
         * 初始化日志系统
         */
        if (config == null) {
            config = new LogServiceImpl.LogConfig();
            config.setHttpHost(Constants.HOST);
            config.setHttpLogAction(Constants.PATH_UPLOAD_LOG);
            config.setHttpLogFileAction(Constants.PATH_UPLOAD_LOG_DB);
            config.setCacheDir(AndroidUtil.getLogCacheDir(app));
            config.setUploadDir(AndroidUtil.getLogDir(app));
            config.setQuestFreq(1000 * 60 * 2);
        }
        LogPrint.init(app, config);
        /**
         * 初始化异常系统
         */
        CrashSystem.getInstance(app).setAppName(appName);
        /**
         * 初始化xutls
         */
        x.Ext.init(app);
        x.Ext.setDebug(false); // 是否输出debug日志
    }

    public static void setAppName(String appName) {
        CrashSystem.getInstance(null).setAppName(appName);
    }

}
