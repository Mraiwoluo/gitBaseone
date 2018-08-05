package com.pvetec.weather.provider;

import android.content.Context;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.pvetec.weather.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeu on 2017/1/5.
 */

public class ForecastRequest {
    //static final String URL="https://restapi.amap.com/v3/weather/weatherInfo?key=e2ffca738413b70c9762f4ad81619f0c&extensions=all&city=";
    //static final String URL_TEMPERATURE="https://restapi.amap.com/v3/weather/weatherInfo?key=e2ffca738413b70c9762f4ad81619f0c&city=";

    private static final String TAG = "ForecastRequest";

    private static ForecastRequest mForecastRequest;

    private static DistrictSearchProvider mDistrictSearchProvider;

    private LocalWeatherLive weatherlive;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherForecast weatherforecast;
    private List<LocalDayWeatherForecast> forecastlist = null;

    private Context mContext;

    private ForecastRequest(Context context) {
        this.mContext = context;
    }

    public static ForecastRequest getInstall(Context mContext) {
        if (mForecastRequest == null) {
            mForecastRequest = new ForecastRequest(mContext);
            mDistrictSearchProvider = DistrictSearchProvider.getInstance(mContext);
        }
        return mForecastRequest;
    }

    public void request(final String city, final int connectTimeout, final int requestTimeout) {
        if (null != city) {
            try {
                LogUtils.e(TAG, "mDistrictSearchProvider =" + mDistrictSearchProvider);
                if (mDistrictSearchProvider == null) {
                    onCallBackStauts();
                    return;
                }
                LogUtils.e(TAG, "ForecastRequest  -JOIN RUN--Search");
                //开始从高德SDK 获取adcode
                mDistrictSearchProvider.setOnDistrictSearchResultListener(new DistrictSearchProvider.OnDistrictSearchResultListener() {
                    @Override
                    public void OnDistrictSearchResult(DistrictResult districtResult) {
                        LogUtils.e(TAG, "getErrorCode----" + districtResult.getAMapException().getErrorCode());
                        int errorCode = districtResult.getAMapException().getErrorCode();
                        if (errorCode == 1000) {
//                            ArrayList<DistrictItem> list = districtResult.getDistrict();
//                            if (list != null && list.size() != 0) {
//                                String adcode = list.get(0).getAdcode();
//                                LogUtils.e(TAG, "getAdcode---" + adcode + "-----------" + list.get(0).getName());
//                                //开始请求天气数据
//                                doActionGetWeather(adcode);
//                            }
                            //开始请求天气数据
                            doActionGetWeather(city);
                        } else {
                            //获取adcode 失败
                            onCallBackStauts();
                        }
                    }
                }).doDistrictSearchQuery(city);

            } catch (Exception e) {

            }
        }
    }

    public void requestNewAdd(final String city, final RequestNewAddCallback callback, final int connectTimeout, final int requestTimeout) {
        if (null != city) {
            try {
                LogUtils.e(TAG, "ForecastRequest New Add -JOIN RUN--");
                if (mDistrictSearchProvider == null) {
                    if (callback != null) callback.onCallBackStauts(false);
                    return;
                }
                LogUtils.e(TAG, "ForecastRequest New Add -JOIN RUN--Search--");
                //开始从高德SDK 获取adcode
                mDistrictSearchProvider.setOnDistrictSearchResultListener(new DistrictSearchProvider.OnDistrictSearchResultListener() {
                    @Override
                    public void OnDistrictSearchResult(DistrictResult districtResult) {
                        LogUtils.e(TAG, "getErrorCode----" + districtResult.getAMapException().getErrorCode());
                        int errorCode = districtResult.getAMapException().getErrorCode();
                        if (errorCode == 1000) {
//                            ArrayList<DistrictItem> list = districtResult.getDistrict();
//                            if (list != null && list.size() != 0) {
//                                String adcode = list.get(0).getAdcode();
//                                LogUtils.e(TAG, "getAdcode---" + adcode + "-----------" + list.get(0).getName());
//                                //开始请求天气数据
//                                doNewAddActionGetWeather(callback, adcode);
//                            }
                            //开始请求天气数据
                            doNewAddActionGetWeather(callback, city);
                        } else {
                            //获取adcode 失败
                            if (callback != null) callback.onCallBackStauts(false);
                        }
                    }
                }).doDistrictSearchQuery(city);

            } catch (Exception e) {

            }
        }
    }

    private void doActionGetWeather(final String cityName) {
        LogUtils.e(TAG,"cityName1 ="+cityName);
        cityName(cityName);
        mquery = new WeatherSearchQuery(finalCityName, WeatherSearchQuery.WEATHER_TYPE_FORECAST);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(mContext);
        mweathersearch.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int rCode) {

            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult weatherForecastResult, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (weatherForecastResult != null && weatherForecastResult.getForecastResult() != null
                            && weatherForecastResult.getForecastResult().getWeatherForecast() != null
                            && weatherForecastResult.getForecastResult().getWeatherForecast().size() > 0) {
                        weatherforecast = weatherForecastResult.getForecastResult();
                        forecastlist = weatherforecast.getWeatherForecast();
                        fillforecast();
                        doActionGetTemperature(finalCityName, weatherForecastResult);
                    }
                } else {
                    onCallBackStauts();
                }
            }
        });
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索

    }

    private void doActionGetTemperature(String cityName, final LocalWeatherForecastResult mLocalWeatherForecastResult) {
        mquery = new WeatherSearchQuery(cityName, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(mContext);
        mweathersearch.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(final LocalWeatherLiveResult weatherLiveResult, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                        weatherlive = weatherLiveResult.getLiveResult();
                        String liveWeather = weatherlive.getWeather();
                        String temperature = weatherlive.getTemperature() + "°";
                        String liveWind = weatherlive.getWindDirection() + "风";
                        LogUtils.e(TAG, "liveWeather =" + liveWeather + "  temperature =" + temperature + "  liveWind =" + liveWind + "liveCity ="+weatherlive.getCity());
                        for (int i = 0; i < list.size(); i++) {
                            RequestCallback mCallback  = list.get(i);
                            if (mCallback != null) {
                                LogUtils.e(TAG, "mLocalWeatherForecastResult =" + mLocalWeatherForecastResult + "weatherLiveResult=" + weatherLiveResult);
                                mCallback.onCallback(mLocalWeatherForecastResult, weatherLiveResult);
                            }
                            if (mCallback != null) {
                                LogUtils.e(TAG, "status =" + true);
                                mCallback.onCallBackStauts(true);
                            }
                        }
                    }
                } else {
                    //失败返回false
                    onCallBackStauts();
                }
            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

            }
        });
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    private void onCallBackStauts(){
        for (int i = 0; i < list.size(); i++) {
            RequestCallback mCallback  = list.get(i);
            if (mCallback != null) mCallback.onCallBackStauts(false);
        }
    }

    private void cityName(String cityName){
        if (cityName.equals("香港特别行政区")){
            cityName = "香港";
        }
        if (cityName.equals("澳门特别行政区")){
            cityName = "澳门";
        }
        if (cityName.equals("湘西")){
            cityName = "湘西土家族苗族自治州";
        }
        if (cityName.equals("延边")){
            cityName = "延边朝鲜族自治州";
        }
        if (cityName.equals("恩施")){
            cityName = "恩施土家族苗族自治州";
        }
        if (cityName.equals("阿坝州")){
            cityName = "阿坝藏族羌族自治州";
        }
        if (cityName.equals("甘孜州")){
            cityName = "甘孜藏族自治州";
        }
        if (cityName.equals("凉山州")){
            cityName = "凉山彝族自治州";
        }
        if (cityName.equals("红河州")){
            cityName = "红河哈尼族彝族自治州";
        }
        if (cityName.equals("西双版纳")){
            cityName = "西双版纳傣族自治州";
        }
        if (cityName.equals("怒江州")){
            cityName = "怒江傈僳族自治州";
        }
        if (cityName.equals("德宏州")){
            cityName = "德宏傣族景颇族自治州";
        }
        if (cityName.equals("甘南州")){
            cityName = "甘南藏族自治州";
        }
        if (cityName.equals("黄南州")){
            cityName = "黄南藏族自治州";
        }
        if (cityName.equals("海北州")){
            cityName = "海北藏族自治州";
        }
        if (cityName.equals("海南州")){
            cityName = "海南藏族自治州";
        }
        if (cityName.equals("玉树")){
            cityName = "玉树藏族自治州";
        }
        if (cityName.equals("果洛州")){
            cityName = "果洛藏族自治州";
        }
        if (cityName.equals("海西州")){
            cityName = "海西蒙古族藏族自治州";
        }
        if (cityName.equals("伊犁州")){
            cityName = "伊犁哈萨克自治州";
        }
        if (cityName.equals("昌吉")){
            cityName = "昌吉回族自治州";
        }
        if (cityName.equals("博尔塔拉")){
            cityName = "博尔塔拉蒙古自治州";
        }
        if (cityName.equals("巴音郭楞")){
            cityName = "巴音郭楞蒙古自治州";
        }
        if (cityName.equals("克孜勒苏柯尔克孜")){
            cityName = "克孜勒苏柯尔克孜自治州";
        }
        if (cityName.equals("黔南")){
            cityName = "黔南布依族苗族自治州";
        }
        if (cityName.equals("黔东南")){
            cityName = "黔东南苗族侗族自治州";
        }
        if (cityName.equals("黔西南")){
            cityName = "黔西南布依族苗族自治州";
        }
        finalCityName = cityName;
    }

    String finalCityName;
    private void doNewAddActionGetWeather(final RequestNewAddCallback callback, String cityName) {
        synchronized (ForecastRequest.class) {
            LogUtils.e(TAG,"cityName2 ="+cityName);
            cityName(cityName);
            mquery = new WeatherSearchQuery(finalCityName, WeatherSearchQuery.WEATHER_TYPE_FORECAST);//检索参数为城市和天气类型，实时天气为1、天气预报为2
            mweathersearch = new WeatherSearch(mContext);
            mweathersearch.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
                @Override
                public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {

                }

                @Override
                public void onWeatherForecastSearched(LocalWeatherForecastResult weatherForecastResult, int rCode) {
                    if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                        if (weatherForecastResult != null && weatherForecastResult.getForecastResult() != null
                                && weatherForecastResult.getForecastResult().getWeatherForecast() != null
                                && weatherForecastResult.getForecastResult().getWeatherForecast().size() > 0) {
                            weatherforecast = weatherForecastResult.getForecastResult();
                            forecastlist = weatherforecast.getWeatherForecast();
                            fillforecast();
                            doNewAddActionGetTemperature(callback, finalCityName, weatherForecastResult);
                        }
                    } else {
                        if (callback != null) callback.onCallBackStauts(false);
                    }
                }
            });
            mweathersearch.setQuery(mquery);
            mweathersearch.searchWeatherAsyn(); //异步搜索
        }
    }

    private void doNewAddActionGetTemperature(final RequestNewAddCallback callback, String cityName, final LocalWeatherForecastResult mLocalWeatherForecastResult) {
        mquery = new WeatherSearchQuery(cityName, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch = new WeatherSearch(mContext);
        mweathersearch.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(final LocalWeatherLiveResult weatherLiveResult, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                        weatherlive = weatherLiveResult.getLiveResult();
                        String liveWeather = weatherlive.getWeather();
                        String temperature = weatherlive.getTemperature() + "°";
                        String liveWind = weatherlive.getWindDirection() + "风";
                        LogUtils.e(TAG, "liveWeather =" + liveWeather + "  temperature =" + temperature + "  liveWind" + liveWind + "callback =" + callback);
                        if (callback != null) callback.onCallback(mLocalWeatherForecastResult, weatherLiveResult);
                        if (callback != null) callback.onCallBackStauts(true);
                    }
                } else {
                    if (callback != null) callback.onCallBackStauts(false);
                }
            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

            }
        });
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    public void requestNewAdd(final String city, final RequestNewAddCallback callback) {
        requestNewAdd(city, callback, -1, -1);
    }

    public void request(final String city) {
        request(city, -1, -1);
    }

    private void fillforecast() {
        for (int i = 0; i < forecastlist.size(); i++) {
            LocalDayWeatherForecast localdayweatherforecast = forecastlist.get(i);
            String dayTemp = localdayweatherforecast.getDayTemp() + "℃";
            String nightTemp = localdayweatherforecast.getNightTemp() + "℃";
            String temp = String.format("%-3s~%3s",
                    localdayweatherforecast.getNightTemp() + "℃",
                    localdayweatherforecast.getDayTemp() + "℃");
            String date = localdayweatherforecast.getDate();

            String dayWeather = localdayweatherforecast.getDayWeather();
            LogUtils.e(TAG, "temp =" + temp + "  date =" + date + "  dayWeather =" + dayWeather);
        }
    }

    List<RequestCallback> list = new ArrayList<>();

    public void addCallBack(RequestCallback mRequestCallback) {
        list.add(mRequestCallback);
    }

    public void reMoveCallBack(RequestCallback mRequestCallback) {
        list.remove(mRequestCallback);
    }

    public interface RequestCallback {
        void onCallback(LocalWeatherForecastResult forecastResult, LocalWeatherLiveResult liveResult);

        void onCallBackStauts(boolean status);
    }

    public interface RequestNewAddCallback {
        void onCallback(LocalWeatherForecastResult forecastResult, LocalWeatherLiveResult liveResult);

        void onCallBackStauts(boolean status);
    }
}
