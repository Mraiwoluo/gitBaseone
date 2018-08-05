package com.pvetec.common.bean;

/*
 * import javax.persistence.Column; import javax.persistence.GeneratedValue; import javax.persistence.GenerationType; import javax.persistence.Id;
 * import javax.persistence.Inheritance; import javax.persistence.MappedSuperclass; import javax.persistence.InheritanceType;
 */

/*
 * @MappedSuperclass
 * @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
 */
public class AppBean {

    /*
     * @Id
     * @GeneratedValue(strategy = GenerationType.AUTO)
     * @Column(name = "id")
     */
    long id;

    /**
     * apk包名
     */
    /* @Column(name = "app_package") */
    String app_package;

    /**
     * 版本号
     */
    /* @Column(name = "version_code") */
    int version_code;

    /**
     * 版本名称
     */
    /* @Column(name = "version_name") */
    String version_name;

    /**
     * 应用名称
     */
    /* @Column(name = "app_name") */
    String app_name;
    
    String signature;
    /**
     * 最小sdk
     */
    /* @Column(name = "min_sdk") */
    int min_sdk;

    /**
     * 目标sdk
     */
    /* @Column(name = "target_sdk") */
    int target_sdk;
    
    long size;
    
    boolean sysApp;

    public AppBean() {

    }

    public AppBean(AppBean o) {
        if (o == null) return;
        this.id = o.id;
        this.app_package = o.app_package;
        this.version_code = o.version_code;
        this.version_name = o.version_name;
        this.app_name = o.app_name;
        this.signature=o.signature;
        this.min_sdk = o.min_sdk;
        this.target_sdk = o.target_sdk;
        this.size=o.size;
        this.sysApp=o.sysApp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApp_package() {
        return app_package;
    }

    public void setApp_package(String app_package) {
        this.app_package = app_package;
    }

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public int getMin_sdk() {
        return min_sdk;
    }

    public void setMin_sdk(int min_sdk) {
        this.min_sdk = min_sdk;
    }

    public int getTarget_sdk() {
        return target_sdk;
    }

    public void setTarget_sdk(int target_sdk) {
        this.target_sdk = target_sdk;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    

    public boolean isSysApp() {
        return sysApp;
    }

    public void setSysApp(boolean sysApp) {
        this.sysApp = sysApp;
    }

    @Override
    public Object clone() {
        return  new AppBean(this);
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}
