package com.pvetec.weather;

import android.app.Service;
import android.os.Vibrator;

import com.bumptech.glide.Glide;
import com.pvetec.weather.module.LocationService;
import com.pvetec.weather.utils.LitePalUtils;

import org.litepal.LitePalApplication;

import hrs.crash.log.CrashHandler;

//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class WeatherApplication extends LitePalApplication {

    public LocationService locationService;
    public Vibrator mVibrator;

//    @Override
//    public boolean initCore() {
//        Core.init(this);
//        return true;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，建议在Application中创建
         */

        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        LitePalUtils.getInstall(getApplicationContext()).initLitePal();

        //初始化日记收集
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        //DEBUG
        //if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
          //return;
       //}
       // LeakCanary.install(this);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try{
            Glide.get(this).clearMemory();
        }catch (Exception e){

        }
    }
}
