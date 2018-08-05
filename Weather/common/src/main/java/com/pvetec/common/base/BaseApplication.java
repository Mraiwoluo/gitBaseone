package com.pvetec.common.base;

import android.app.Application;

import com.pvetec.common.core.Core;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!initCore()) {
            Core.init(this);
        }
    }
    public boolean initCore() {
        return false;
    }
}
