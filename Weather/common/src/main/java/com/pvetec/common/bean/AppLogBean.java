package com.pvetec.common.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * @author lcp 2016/1/15
 */

@Table(name = "t_app_log")
public class AppLogBean {

    // @Id
    // @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id", isId = true)
    long id;

    @Column(name = "tag")
    String tag;

    @Column(name = "message")
    String message;

    /**
     * apk包名
     */
    @Column(name = "app_package")
    String app_package;

    /**
     * apk版本号
     */
    @Column(name = "app_version")
    int app_version;

    /**
     * 平台版本号
     */
    @Column(name = "platform_version")
    String platform_version;

    /**
     * android os版本号
     */
    @Column(name = "os_version")
    String os_version;

    /**
     * log产生时间,log不是产生后马上上传
     */
    // @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Column(name = "write_time")
    Date write_time;

    /**
     * 插入数据库时间
     */
    Date create_time;

    /**
     * WORN /ERROR
     */
    @Column(name = "priority")
    int priority;
    
    @Column(name = "deviceId")
    String deviceId;

    public AppLogBean(String tag, String message, String app_package, int app_version,
                      String platform_version, String os_version, Date write_time, int priority, String deviceId) {
        this.tag = tag;
        this.message = message;
        this.app_package = app_package;
        this.app_version = app_version;
        this.platform_version = platform_version;
        this.os_version = os_version;
        this.write_time = write_time;
        this.priority = priority;
        this.deviceId=deviceId;
    }

    public AppLogBean() {

    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {
        this.app_package = app_package;
    }

    public int getApp_version() {
        return app_version;
    }

    public void setApp_version(int app_version) {
        this.app_version = app_version;
    }

    public String getPlatform_version() {
        return platform_version;
    }

    public void setPlatform_version(String platform_version) {
        this.platform_version = platform_version;
    }

    public String getOs_version() {
        return os_version;
    }

    public void setOs_version(String os_version) {
        this.os_version = os_version;
    }

    public Date getWrite_time() {
        return write_time;
    }

    public void setWrite_time(Date write_time) {
        this.write_time = write_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
