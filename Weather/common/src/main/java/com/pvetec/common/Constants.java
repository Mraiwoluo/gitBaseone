package com.pvetec.common;

public class Constants {

    public final String app_session_key="eac604f0-4710-44d9-a567-514ec4124b07";
    
    /**
     * test主机地址 
     */
    //public static final String HOST="http://192.168.1.157:8080";
    
    
    /**
     * 商用主机
     */
    public static final String HOST="http://biz.pvetec.com";
    
    
    public static final String WEBAPP="PvetecWeb";
    
    /**
     * 以db文件的方式上传日志
     */
    public static final String PATH_UPLOAD_LOG_DB=WEBAPP+"/logManager/uploadLogFileJson.do";
    
    /**
     * 以对象的方式上传日志每次只能上传一条
     */
    public static final String PATH_UPLOAD_LOG=WEBAPP+"/logManager/uploadLogJson.do";
    
    /**获取单个应用的升级信息*/
    public static final String PATH_FETCH_UPGRADE_ITEM=WEBAPP+"/appManager/getUpgradeJson.do";
    
    /**获取应用列表的升级信息*/
    public static final String PATH_FETCH_UPGRADES_LIST=WEBAPP+"/appManager/getUpgradeListJson.do";
    
    public static final String PATH_FETCH_UPGRADES_BY_PATH=WEBAPP+"/appManager/getUpgradeFileByPath.do?";
    
    public static final String PATH_FETCH_IMAGE_BY_PATH=WEBAPP+"/image/getImageByPath.do?";
    
    public static final String PATH_FETCH_FILE_BY_PATH="file/";
    
    
    public static final String STRING_EXTRAS_APP_NAME="app_name";
    
    public static final String ACTION_APP_EXIT="pvetec.intent.action.app.exit";
    
}
