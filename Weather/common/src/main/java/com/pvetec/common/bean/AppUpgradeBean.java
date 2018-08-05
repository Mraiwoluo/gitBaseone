package com.pvetec.common.bean;

import com.pvetec.common.utils.urlUtl;

import java.util.Date;

/*
 * import javax.persistence.Column; import javax.persistence.Entity; import javax.persistence.Table;
 */

/**
 * @author lcp 2016/1/15
 */
/*
 * @Entity
 * @Table(name = "t_app_upgrade")
 */
public class AppUpgradeBean extends AppBean {

    /**
     * apk下载路径
     */
    /* @Column(name = "app_url") */
    String app_url;

    /* @Column(name = "app_icon") */
    String app_icon;

    /**
     * apk描述
     */
    /* @Column(name = "app_describe") */
    String app_describe;

    /**
     * 升级标志 0 表示强制升级
     */
    /* @Column(name = "upgrade_flag") */
    int upgrade_flag;

    /**
     * 插入数据库时间
     */
    /* @Column(name = "create_time") */
    Date create_time;

    /**
     * apk文件的md5值
     */
    /* @Column(name = "md5") */
    String md5;

    long app_size;

    public AppUpgradeBean(AppUpgradeBean o) {
        super(o);
        if (o == null) return;
        app_url = o.app_url;
        app_icon = o.app_icon;
        app_describe = o.app_describe;
        upgrade_flag = o.upgrade_flag;
        app_size = o.app_size;
        if (o.create_time != null) create_time = new Date(o.create_time.getTime());
        md5 = o.md5;

    }

    public AppUpgradeBean(AppBean o) {
        super(o);
    }

    public AppUpgradeBean() {
        create_time = new Date();
    }

    public String getApp_url() {
        return app_url;
    }

    public void setApp_url(String app_url) {
        this.app_url = app_url;
    }

    public String getApp_icon() {
        return app_icon;
    }

    public void setApp_icon(String app_icon) {
        this.app_icon = app_icon;
    }

    public String getApp_describe() {
        return app_describe;
    }

    public void setApp_describe(String app_describe) {
        this.app_describe = app_describe;
    }

    public int getUpgrade_flag() {
        return upgrade_flag;
    }

    public void setUpgrade_flag(int upgrade_flag) {
        this.upgrade_flag = upgrade_flag;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getApp_size() {
        return app_size;
    }

    public void setApp_size(long app_size) {
        this.app_size = app_size;
    }

    public static void bulidUrl(AppUpgradeBean bean) {
        if (bean == null) return;
        if (bean.getApp_url() != null && !bean.getApp_url().startsWith("http")) bean.setApp_url(urlUtl.buildAppUrl(bean.getApp_url()));
        if (bean.getApp_icon() != null && !bean.getApp_icon().startsWith("http")) bean.setApp_icon(urlUtl.buildImageUrl(bean.getApp_icon()));
/*        bean.setApp_url(urlUtl.encodeUtl(bean.getApp_url(), "utf-8"));
        bean.setApp_icon(urlUtl.encodeUtl(bean.getApp_icon(), "utf-8"));*/

    }

    public static boolean checkValid(AppUpgradeBean upgrade) {
        if (upgrade.app_package != null && upgrade.app_url != null && !upgrade.app_url.isEmpty()) return true;
        return false;
    }

}
