package com.pvetec.weather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.pvetec.weather.control.cityadder.CityAdderLogic;
import com.pvetec.weather.control.citymanager.CityManagerLogic;
import com.pvetec.weather.control.forecast.ForecastLogic;
import com.pvetec.weather.control.weather.WeatherLogic;
import com.pvetec.weather.provider.LocationProvider;
import com.pvetec.weather.provider.RemoteResolver;

/**
 * Created by zeu on 2017/1/4.
 */

public class WeatherService extends Service {
    CityAdderLogic mCityAdderLogic;  //添加城市
    CityManagerLogic mCityManagerLogic; //城市管理
    ForecastLogic mForecastLogic; //天气查询
    WeatherLogic mWeatherLogic;  //主界面控制
    RemoteResolver mRemoteResolver;

    @Override
    public void onCreate() {
        super.onCreate();
        mCityAdderLogic = CityAdderLogic.getInstance(getApplicationContext());
        mForecastLogic =  ForecastLogic.getInstance(getApplicationContext());
        mWeatherLogic =  WeatherLogic.getInstance(getApplicationContext());
        mCityManagerLogic = CityManagerLogic.getInstance(getApplicationContext());

        mRemoteResolver = new RemoteResolver(getApplicationContext());


        //启动定位
        LocationProvider.getInstance(WeatherService.this).stop();
        LocationProvider.getInstance(WeatherService.this).unregisterListener();
        LocationProvider.getInstance(WeatherService.this).registerListener();
        LocationProvider.getInstance(WeatherService.this).requestLocationCity();
    }

    private void initCityDb(){
        //初始化天气，默认显示深圳

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
