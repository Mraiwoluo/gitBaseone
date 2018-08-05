package com.pvetec.common.service;

public interface UploadLogService {
	
    /**
     * 
     * 同步上传日志
     * @return
     */
    public  boolean uploadLogFile(String url, String appPkg, int appVer, String platVer, String osVer, int priority, String deviceId, String tag, String msg);

}
