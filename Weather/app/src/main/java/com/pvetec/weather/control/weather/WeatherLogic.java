package com.pvetec.weather.control.weather;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.pvetec.weather.control.cityadder.CityAdderLogic;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;
import com.pvetec.weather.module.CityAndWeather;
import com.pvetec.weather.provider.ForecastProvider;
import com.pvetec.weather.provider.ForecastRequest;
import com.pvetec.weather.provider.LocationProvider;
import com.pvetec.weather.utils.LitePalUtils;
import com.pvetec.weather.utils.LogUtils;
import com.pvetec.weather.utils.NetWorkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zeu on 2017/1/3.
 */

public class WeatherLogic implements IWeatherLogic {
    private static String TAG = "WeatherLogic";
    Context mContext;
    IWeatherView mView;
    LinkedList<CityInfo> mCitys;
    CityAdderLogic mCityAdderLogic;
    static WeatherLogic mWeatherLogic;
    Handler mHandler;
    private MRequestCallback mRequestCallback;

    public static WeatherLogic getInstance(Context context) {
        if (mWeatherLogic == null) {
            mWeatherLogic = new WeatherLogic(context);
        }
        return mWeatherLogic;
    }

    private WeatherLogic(Context context) {
        mContext = context;
        mCitys = new LinkedList<>();
        mRequestCallback = new MRequestCallback();
        mHandler = new Handler(Looper.getMainLooper());
        ForecastRequest.getInstall(mContext).addCallBack(mRequestCallback);
    }

    public void uninitHandler() {
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
    }

    public void setView(IWeatherView mView) {
        this.mView = mView;
    }

    @Override
    public void initWeather() {
        if (mHandler == null) mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mView != null) mView.initWeather(getCityList());
            }
        });

    }

    @Override
    public void requstAddCity(CityInfo mCityInfo) {
        if (mCityInfo != null && mView != null) mView.updateAddNewCity(mCityInfo);
    }

    @Override
    public void onLocationResult(boolean status) {
        initWeather();
    }

    CityInfo info;

    @Override
    public void requstUpdateWeather(final CityInfo info) {
        if (null != info) {
            LogUtils.e(TAG, "info =" + info + "name =" + info.getName());
            String name = info.getName();
            if (null != name) {
                this.info = info;
                ForecastRequest.getInstall(mContext).request(name);
            }
        }
    }

    @Override
    public void updateSelectedItem(CityInfo info) {
        if (null != mView && null != info) {
            mView.updateSelectedItem(info);
        }
    }

    @Override
    public void updateDataBases() {
        if (mView != null) mView.updateDataBases();
    }

    public CityInfo getCityInfo(String cityPath) {
        if (null != cityPath) {
            for (CityInfo city : mCitys) {
                if (null != city) {

                    return city;

                }
            }
        }
        return null;
    }

    public void checkNewWorkState() {
        //检测网络状态
        if (!NetWorkUtils.isNetworkConnected(mContext) || (NetWorkUtils.getConnectedType(mContext) == -1)) {
            if (mView != null) mView.netWorkError();
        }
    }

    private List<CityInfo> getCityList() {
        List<CityInfo> citys = null;
        if (mContext == null) return citys;
        //数据库中加载
        Map<CityInfo, ForecastJson> result = ForecastProvider.getInstance(mContext).getAllCity();
        citys = new ArrayList<CityInfo>();
        if (result != null) {
            for (CityInfo info : result.keySet()) {
                if (null != info) {
                    citys.add(info);
                    //LogUtils.e(TAG, "initCityList--CITY----" + info.getName());
                }
            }
        }

        //处理天气列表
        if (citys != null && citys.size() >= 1) {
            //获取定位城市的position
            int position = 0;
            String location = LocationProvider.getInstance(mContext).getSpLocationCity();
            LogUtils.e(TAG, "   locationcity=" + location);
            for (int i = 0; i < citys.size(); i++) {
                CityInfo info = citys.get(i);
                if (info != null) {
                    if (location.contains(info.getName()) || location.equals(info.getName())) {
                        position = i;
                        LogUtils.e(TAG, "   position=" + position);
                        break;
                    }
                }
            }
            if (position != 0 && position < citys.size()) {
                //移动位置
                Collections.swap(citys, 0, position);
            }

        }
        return citys;
    }


    class MRequestCallback implements ForecastRequest.RequestCallback {

        @Override
        public void onCallback(LocalWeatherForecastResult forecastResult, LocalWeatherLiveResult liveResult) {
            LogUtils.e(TAG, "forecastResult =" + forecastResult + "mView =" + mView + "liveResult =" + liveResult);
            if (info == null) {
                return;
            }
            if (null != forecastResult && null != mView && liveResult != null) {
                try {
                    CityAndWeather mCityAndWeather = LitePalUtils.getInstall(mContext).AmapWeatherToCityAndWeather(forecastResult, liveResult, info.getName());
                    if (forecastResult.getForecastResult() != null && mCityAndWeather != null) {
                        LogUtils.e(TAG, "getForecastResult =" + forecastResult.getForecastResult() + "mCityAndWeather =" + mCityAndWeather);
                        final ForecastJson json = ForecastProvider.getInstance(mContext).formatToForecastJson(mCityAndWeather);
                        //update database
                        ForecastProvider.getInstance(mContext).saveForecast(info, json, mCityAndWeather);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                mView.updateWeather(info, json);
                                LogUtils.e(TAG, "info =" + info + "json =" + json);
                            }
                        });
                    }
                    mView.updateWeatherStatus(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                info = null;
            }
        }

        @Override
        public void onCallBackStauts(boolean status) {
            if (info == null) {
                return;
            }
            if (null != mView) {
                if (status) {
                    //更新完成回调
                    mView.updateWeatherStatus(true);
                } else {
                    //更新失败回调
                    mView.updateWeatherStatus(false);
                }
            }
        }
    }

}
