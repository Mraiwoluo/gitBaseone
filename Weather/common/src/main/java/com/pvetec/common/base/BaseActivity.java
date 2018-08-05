package com.pvetec.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import org.xutils.x;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable
                                    Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        x.image().clearCacheFiles();
        x.image().clearMemCache();
        super.onLowMemory();
    }
  
}
