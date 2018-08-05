package com.pvetec.weather.provider;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.pvetec.weather.control.cityadder.CityAdderLogic;
import com.pvetec.weather.control.weather.WeatherLogic;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;
import com.pvetec.weather.model.litepalmodel.WeatherInfo;
import com.pvetec.weather.module.CityAndWeather;
import com.pvetec.weather.module.CitySearcher;
import com.pvetec.weather.utils.LitePalUtils;
import com.pvetec.weather.utils.LogUtils;
import com.pvetec.weather.view.widget.WeatherWidgetProvider;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zeyu on 2017/1/9.
 */

public class ForecastProvider {
    private static final String TAG="ForecastProvider";

    private static final String TABLE_FORCAST="weatherinfo";

    Context mContext;
    List<RequestCallback> mCallbacks = new ArrayList<>();
    List<ForecastChangedListener> mListeners = new ArrayList<>();
    MForecastRequestCallback mForecastRequestCallback;
    ForecastLocationRequestCallback forecastLocationRequestCallback;
    private ForecastProvider(Context context) {
        if (null != context) {
            this.mContext=context;
            mForecastRequestCallback = new MForecastRequestCallback();
            forecastLocationRequestCallback = new ForecastLocationRequestCallback();
            ForecastRequest.getInstall(mContext).addCallBack(mForecastRequestCallback);
            ForecastRequest.getInstall(mContext).addCallBack(forecastLocationRequestCallback);
        }
    }

    static ForecastProvider sForecastProvider;
    public static ForecastProvider getInstance(Context context) {
        if (null == sForecastProvider) {
            sForecastProvider = new ForecastProvider(context);
        }
        return sForecastProvider;
    }

    private void execCallback(boolean sucess, CityInfo info, ForecastJson json, String extra) {
        List<RequestCallback> needRemoves = new ArrayList<>();
        synchronized (mCallbacks) {
            for (RequestCallback callback : mCallbacks) {
                if (null != callback) {
                    if (callback.onResult(sucess, info, json, extra)) {
                        needRemoves.add(callback);
                    }
                }
            }

            for (RequestCallback callback : needRemoves) {
                mCallbacks.remove(callback);
            }
        }
    }

    private String checkCityName(CityInfo info) {
        String name = null;
        if (null != info) {
            name = info.getName();
            if (null != name) {
                name = name.replaceAll("市", "");
            }
        }
        return name;
    }

    public void requestForecast(CityInfo info) {
        requestForecast(info, null);
    }

    public void requestForecast(final CityInfo info, RequestCallback callback) {
        requestForecast(info, callback, true);
    }
    String name;
    public void requestForecast(final CityInfo info, RequestCallback callback, boolean requestNotExist) {

        if (!TextUtils.isEmpty(info.getName())) {
            //先从数据库中读取,然后请求,然后再返回callback
            if (null != callback) {
                ForecastJson json = getForecast(info);
                if (null != json) {//已经在数据库中由该条数据
                    callback.onResult(true, info, json, null);

                } else if (!requestNotExist) { //不请求立即返回
                    callback.onResult(true, info, json, null);
                }
            }

            if (requestNotExist) {
                if (null != callback) {
                    synchronized (mCallbacks) {
                        if (!mCallbacks.contains(callback)) {
                            mCallbacks.add(callback);
                        }
                    }
                }
                name=info.getName();
                ForecastRequest.getInstall(mContext).request(name);
            }
        }
    }

    String mCityName;
    public void requestForecastLocation(String cityName){
        mCityName = cityName;
        if (mCityName.equals("香港特別行政區")){
            mCityName = "香港特别行政区";
        }
        if (mCityName.equals("澳門特別行政區")){
            mCityName = "澳门特别行政区";
        }
        if(!TextUtils.isEmpty(mCityName)) {
            ForecastRequest.getInstall(mContext).request(mCityName);
        }
    }

    class MForecastRequestCallback implements ForecastRequest.RequestCallback{

        @Override
        public void onCallback(LocalWeatherForecastResult forecastResult, LocalWeatherLiveResult liveResult) {
            if (name == null){
                return;
            }
            if (forecastResult != null && liveResult != null){
                try {
                    synchronized (ForecastProvider.class) {
                        CityAndWeather mCityAndWeather = LitePalUtils.getInstall(mContext).AmapWeatherToCityAndWeather(forecastResult,liveResult,name);
                        if (mCityAndWeather != null&&mCityAndWeather.mCityName.equals(name)){
                            final ForecastJson json = formatToForecastJson(mCityAndWeather);
                            final CityInfo mCityInfo= new CityInfo(name);
                            //保存到数据库中
                            LogUtils.e(TAG, " ForecastJson--requestForecast-- 保存进数据库--- " + name);
                            setForecast(mCityInfo, json, mCityAndWeather);
                            execCallback(true, mCityInfo, json, null);
                            WeatherLogic.getInstance(mContext).updateDataBases();
                            name = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onCallBackStauts(boolean status) {

        }
    }

    String locationCity;
    private void cityName(String cityName){
        if (cityName.equals("香港特别行政区")) {
            cityName = "香港";
        }
        if (cityName.equals("澳门特别行政区")) {
            cityName = "澳门";
        }
        if (cityName.equals("湘西土家族苗族自治州")) {
            cityName = "湘西";
        }
        if (cityName.equals("延边朝鲜族自治州")) {
            cityName = "延边";
        }
        if (cityName.equals("恩施土家族苗族自治州")) {
            cityName = "恩施";
        }
        if (cityName.equals("阿坝藏族羌族自治州")) {
            cityName = "阿坝州";
        }
        if (cityName.equals("甘孜藏族自治州")) {
            cityName = "甘孜州";
        }
        if (cityName.equals("凉山彝族自治州")) {
            cityName = "凉山州";
        }
        if (cityName.equals("红河哈尼族彝族自治州")) {
            cityName = "红河州";
        }
        if (cityName.equals("西双版纳傣族自治州")) {
            cityName = "西双版纳";
        }
        if (cityName.equals("怒江傈僳族自治州")) {
            cityName = "怒江州";
        }
        if (cityName.equals("德宏傣族景颇族自治州")) {
            cityName = "德宏州";
        }
        if (cityName.equals("甘南藏族自治州")) {
            cityName = "甘南州";
        }
        if (cityName.equals("黄南藏族自治州")) {
            cityName = "黄南州";
        }
        if (cityName.equals("海北藏族自治州")) {
            cityName = "海北州";
        }
        if (cityName.equals("海南藏族自治州")) {
            cityName = "海南州";
        }
        if (cityName.equals("玉树藏族自治州")) {
            cityName = "玉树";
        }
        if (cityName.equals("果洛藏族自治州")) {
            cityName = "果洛州";
        }
        if (cityName.equals("海西蒙古族藏族自治州")) {
            cityName = "海西州";
        }
        if (cityName.equals("伊犁哈萨克自治州")) {
            cityName = "伊犁州";
        }
        if (cityName.equals("昌吉回族自治州")) {
            cityName = "昌吉";
        }
        if (cityName.equals("博尔塔拉蒙古自治州")) {
            cityName = "博尔塔拉";
        }
        if (cityName.equals("巴音郭楞蒙古自治州")) {
            cityName = "巴音郭楞";
        }
        if (cityName.equals("克孜勒苏柯尔克孜自治州")) {
            cityName = "克孜勒苏柯尔克孜";
        }
        if (cityName.equals("黔南布依族苗族自治州")) {
            cityName = "黔南";
        }
        if (cityName.equals("黔东南苗族侗族自治州")) {
            cityName = "黔东南";
        }
        if (cityName.equals("黔西南布依族苗族自治州")) {
            cityName = "黔西南";
        }
        locationCity = cityName;
    }

    class ForecastLocationRequestCallback implements ForecastRequest.RequestCallback{

        @Override
        public void onCallback(LocalWeatherForecastResult forecastResult, LocalWeatherLiveResult liveResult) {
             if (mCityName==null){
                 return;
             }
            if (forecastResult != null && liveResult != null){
                try {
                    synchronized (ForecastProvider.class) {
                        CityAndWeather mCityAndWeather = LitePalUtils.getInstall(mContext).AmapWeatherToCityAndWeather(forecastResult,liveResult,mCityName);
                        LogUtils.e(TAG,"mCityName ="+mCityName + "   mCityAndWeather ="+mCityAndWeather.mCityName);
                        if (mCityAndWeather != null&&mCityAndWeather.mCityName.equals(mCityName)){
                            final ForecastJson json = formatToForecastJson(mCityAndWeather);
                            cityName(mCityName);
                            final CityInfo mCityInfo= new CityInfo(locationCity);
                            //保存到数据库中
                            LogUtils.e(TAG, " ForecastJson--requestForecast-- 保存进数据库--- " + locationCity);
                            setForecastToFirst(mCityInfo, json, mCityAndWeather);
                            //通知主页更新
                            WeatherLogic.getInstance(mContext).initWeather();
                            //定位城市差异比较
                            if (!TextUtils.isEmpty(getSpLocationCity())) {
                                //if (!name.contains(getSpLocationCity())) {
                                //新定位城市，刷新
                                ControlProvider.getInstall(mContext).notifyIWeatherViewLocationChange();
                            }
                            locationCity = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onCallBackStauts(boolean status) {

        }
    }

    public String getSpLocationCity(){
        String city="" ;
        if(mContext!=null)    city= LocationProvider.getInstance(mContext).getSpLocationCity();
        LogUtils.e(TAG,"city ="+city);
        return city;
    }


    public void requestNewAddCityForecast(final CityInfo info){
        if(null!=info){
            final String name=info.getName();
            ForecastRequest.getInstall(mContext).requestNewAdd(name, new ForecastRequest.RequestNewAddCallback() {
                @Override
                public void onCallback(LocalWeatherForecastResult forecastResult, LocalWeatherLiveResult liveResult) {
                    if (forecastResult != null && liveResult != null){
                        try {
                            synchronized (ForecastProvider.class) {
                                CityAndWeather mCityAndWeather = LitePalUtils.getInstall(mContext).AmapWeatherToCityAndWeather(forecastResult,liveResult,name);
                                if (mCityAndWeather != null){
                                    final ForecastJson json = formatToForecastJson(mCityAndWeather);
                                    final CityInfo mCityInfo= CitySearcher.getInstance(mContext).get(name);
                                    //保存到数据库中
                                    LogUtils.e(TAG, " ForecastJson--requestForecast-- 保存进数据库--- " + name);
                                    setForecastToFirst(mCityInfo, json, mCityAndWeather);
                                    insertForecast(info, json, mCityAndWeather);
                                    if (CityAdderLogic.getInstance(mContext).getAddedCitys() != null)
                                        CityAdderLogic.getInstance(mContext).getAddedCitys().add(info);
                                    if (null != requestCallbackSaveDataBase) {
                                        requestCallbackSaveDataBase.onResult(info, true);
                                        ControlProvider.getInstall(mContext).notifyIWeatherViewCityList();
                                        LogUtils.i(TAG, " requestNewAddCityForecast---- requestCallbackSaveDataBase  true ");
                                    }
                                } else {
                                    if (null != requestCallbackSaveDataBase) {
                                        requestCallbackSaveDataBase.onResult(info, false);
                                        LogUtils.i(TAG, " requestNewAddCityForecast---- requestCallbackSaveDataBase  false ");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCallBackStauts(boolean status) {
                    LogUtils.i(TAG," requestNewAddCityForecast---- false ");
                }
            });


        }
    }

    public ForecastJson formatToForecastJson(CityAndWeather mCityAndWeather){
        ForecastJson json=new ForecastJson();
        if(mCityAndWeather==null) return json;
        if(mCityAndWeather.mForecastList==null) return json;

        json.setCity(mCityAndWeather.mCityName);
        json.setDate(mCityAndWeather.mForecastList.get(0).obsdate);

        List<ForecastJson.ForecastBean> list=new ArrayList<>();

        for (CityAndWeather.Forecast forecast:mCityAndWeather.mForecastList){
            if(forecast==null) continue;
            ForecastJson.ForecastBean mForecastBean=new ForecastJson.ForecastBean();
            mForecastBean.setDate(forecast.daycode);
            mForecastBean.setTemperarray(forecast.temperaturerange);
            mForecastBean.setWeather(forecast.weathericon);
            mForecastBean.setWeatherIcon(forecast.weathericon);
            mForecastBean.setTemperature(mCityAndWeather.mCurrentTemperature);
            mForecastBean.setWind(forecast.wind);
        }

        json.setForecasts(list);

        return json;
    }



    public ForecastJson getForecast(CityInfo cityInfo) {
        //直接重数据库读取, 如果没有的话,先请求,然后保存到数据库
        ForecastJson json = new ForecastJson();
        if (null != cityInfo) {
            try {
                json=LitePalUtils.getInstall(mContext).getForecastJson(cityInfo.getName()) ;
            }catch (Exception e){

            }
        }
        return json;
    }

    public Cursor getAllForecastCursor() {
        LitePalUtils.getInstall(mContext).getSQLiteDatabase();
        return LitePalUtils.getInstall(mContext).getCursorForCityNane(getSpLocationCity());
    }

    private void insertForecast(CityInfo city, ForecastJson json,CityAndWeather mWeather){
        if (null != city && null != json ) {
               //更新数据库
                try {
                    Map<CityInfo, ForecastJson> cityMap=getAllCity();
                    if(cityMap!=null){
                        if(cityMap.size()>=6){
                            return;
                        }
                    }
                    int result =LitePalUtils.getInstall(mContext).save(mWeather,city.getName());
                    LogUtils.i(TAG, "insertForecast---" + result);
                    sendBrocast(WeatherWidgetProvider.WIDGET_UPDATE_WEATHER);
                }catch (Exception e){
                    return;
                }
        }
    }


    private void setForecast(CityInfo city, ForecastJson json,CityAndWeather mWeather) {
        if (null != city && null != json) {
            //更新数据库
            try {
                boolean isExistence=isExistenceCity(city.getName());
                if (isExistence) {
                    LitePalUtils.getInstall(mContext).update(mWeather,city.getName());
                    execForecastChangedListener(city, json);
                } else {
                    LitePalUtils.getInstall(mContext).save(mWeather,city.getName());
                    execForecastChangedListener(city, json);
                }
                sendBrocast(WeatherWidgetProvider.WIDGET_UPDATE_WEATHER);
            }catch (Exception e){
                LogUtils.i(TAG,"setForecast----"+e.toString());
                return;
            }
        }
    }

    public void saveForecast(CityInfo city, ForecastJson json,CityAndWeather mWeather){
        if (null != city && null != json ) {
            //更新数据库
            try {
                boolean isExistence=isExistenceCity(city.getName());
                if (isExistence) {
                    LitePalUtils.getInstall(mContext).update(mWeather,city.getName());
                    execForecastChangedListener(city, json);

                }else{
                    LitePalUtils.getInstall(mContext).save(mWeather,city.getName());
                    execForecastChangedListener(city, json);
                    LogUtils.i(TAG,"saveForecast---no find city--"+city.getName());
                }
                sendBrocast(WeatherWidgetProvider.WIDGET_UPDATE_WEATHER);
            }catch (Exception e){
                return;
            }
        }
    }

    private void setForecastToFirst(CityInfo city, ForecastJson json,CityAndWeather mCityAndWeather) {
        if (null != city && null != json) {
            //更新数据库
            try {
                //查询数据库存在城市，存在==更新，不存在===保存
                boolean isCity = LitePalUtils.getInstall(mContext).findWeatherInfo(city.getName());
                if(isCity){
                    LitePalUtils.getInstall(mContext).update(mCityAndWeather,city.getName());
                }else{
                    //数据库空，直接保存
                    LitePalUtils.getInstall(mContext).save(mCityAndWeather,city.getName());
                }
                execForecastChangedListener(city, json);

                sendBrocast(WeatherWidgetProvider.WIDGET_UPDATE_WEATHER);
            }catch (Exception e){
                LogUtils.i(TAG,"setForecastToFirst-----"+e.toString());
                return;
            }
        }
    }


    public void execForecastChangedListener(CityInfo info, ForecastJson json) {
        synchronized (mListeners) {
            for (ForecastChangedListener listener : mListeners) {
                if (null != listener) {
                    listener.onChanged(info, json);
                }
            }
        }
    }

    public void addForecastChangedListener(ForecastChangedListener listener) {
        if (null != listener && !mListeners.contains(listener)) {
            synchronized (mListeners) {
                mListeners.add(listener);
            }
        }
    }


    //获取第一个位置的城市
    public CityInfo getCityInfoFirst(Context context){
        CityInfo mCityInfo=new CityInfo();
        try{
            //数据库中加载
            WeatherInfo lastWeather = DataSupport.findFirst(WeatherInfo.class);
            if(lastWeather!=null&&lastWeather.getCityName()!=null){
                mCityInfo.setName(lastWeather.getCityName());
            }
        }catch (Exception e){
             LogUtils.i(TAG,"--Exception---"+e.toString());
        }
        return mCityInfo;
    }

    //获取第一位位置的城市天气
    private ForecastJson getCityWeatherFirst(String city){
        ForecastJson mForecastJson=null;
        try{
            mForecastJson=LitePalUtils.getInstall(mContext).getForecastJson(city);

        }catch (Exception e){

        }
        return mForecastJson;
    }

    //获取第一位位置的城市天气
    private WeatherInfo getCityWeatherInfoFirst(String city){
        WeatherInfo mWeatherInfo=null;
        try{
            mWeatherInfo=DataSupport.findFirst(WeatherInfo.class);
        }catch (Exception e){

        }
        return mWeatherInfo;
    }

    //获取定位位置的城市天气
    private WeatherInfo getCityLocationWeatherInfo(Context context,CityInfo cityInfo){
        WeatherInfo mWeatherInfo=null;
        try{
            List<WeatherInfo> list = DataSupport.where("cityname = ?", cityInfo.getName()).find(WeatherInfo.class);
            if(null!=list&&list.size()!=0){
                mWeatherInfo=list.get(0);
            }

        }catch (Exception e){

        }
        return mWeatherInfo;
    }



    public Map<CityInfo, ForecastJson> getAllCity() {
        Map<CityInfo, ForecastJson> results = new LinkedHashMap<>();
        try {
            List<WeatherInfo> allWeather = DataSupport.findAll(WeatherInfo.class);
            if (null != allWeather) {
                try {
                    for (WeatherInfo mWeatherInfo : allWeather) {

                        CityInfo cityInfo = new CityInfo();
                        cityInfo.setName(mWeatherInfo.getCityName());

                        ForecastJson forecastJson = LitePalUtils.getInstall(mContext).creatForecastJson(mWeatherInfo);

                        results.put(cityInfo, forecastJson);
                    }
                } catch (Exception e) {

                }
            }
        }catch (Exception e){

        }
        return results;
    }

    public List<WeatherInfo> getAllWeatherInfo(){
        List<WeatherInfo> mList=null;
        try {
            mList = DataSupport.findAll(WeatherInfo.class);
        }catch (Exception e){

        }
        return mList;
    }

    public boolean isExistenceCity(String cityName){
        boolean isisExistence=false;
        if(TextUtils.isEmpty(cityName)) return isisExistence;
        try {
            List<WeatherInfo> list = DataSupport.where("cityname = ?", cityName).find(WeatherInfo.class);
            if (list != null && list.size() != 0) {
                isisExistence = true;
            }
        }catch (Exception e){

        }
        return isisExistence;
    }


    public List<Object> getForecastForCityName(String cityName){
        //Map<CityInfo, ForecastJson> results = new LinkedHashMap<>();
        List<Object> list=new ArrayList<>();
        try {
            List<WeatherInfo> allWeather = DataSupport.where("cityname = ?", cityName).find(WeatherInfo.class);
            if (null != allWeather) {
                try {

                    for (WeatherInfo mWeatherInfo : allWeather) {

                        CityInfo cityInfo = new CityInfo();
                        cityInfo.setName(mWeatherInfo.getCityName());

                        ForecastJson forecastJson = LitePalUtils.getInstall(mContext).creatForecastJson(mWeatherInfo);

                        list.add(cityInfo);
                        list.add(forecastJson);
                    }

                } catch (Exception e) {
                }
            }
        }catch (Exception E){

        }
        return list;
    }


    public int delectCity(CityInfo info){
        int result=-1;
        try {
            result = LitePalUtils.getInstall(mContext).delect(info.getName());
            LogUtils.e(TAG, "delectCity-----" + result);
        }catch (Exception e){
            return result;
        }
        return result;
    }

    public void sendBrocast(String action){
        try{
            if (mContext==null) return;
            Intent intentWidget=new Intent();
            intentWidget.setAction(action);
            mContext.sendBroadcast(intentWidget);
        }catch (Exception e){

        }
    }

    public void updateAllWeather(Map<CityInfo, ForecastJson> result){
        new updateAllWeatherAsyn(result).execute();
    }

    private class updateAllWeatherAsyn extends AsyncTask<Void,Void,Void>{

        Map<CityInfo, ForecastJson> result;

        private updateAllWeatherAsyn(Map<CityInfo, ForecastJson> result){
            this.result=result;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(result!=null) {
                try {
                for (CityInfo info : result.keySet()) {
                    if (null != info) {
                        //开始请求天气
                        requestForecast(info);
                        Thread.sleep(50);
                        LogUtils.i(TAG,"updateAllWeatherAsyn---requestForecast---");
                    }
                }
                }catch (Exception e){

                }
            }

            return null;
        }
    }


    public interface RequestCallback {
        boolean onResult(boolean sucess, CityInfo info, ForecastJson json, String error);
    }

    public interface ForecastChangedListener {
        void onChanged(CityInfo info, ForecastJson json);
    }

    public RequestCallbackSaveDataBase getRequestCallbackSaveDataBase() {
        return requestCallbackSaveDataBase;
    }

    public void setRequestCallbackSaveDataBase(RequestCallbackSaveDataBase requestCallbackSaveDataBase) {
        this.requestCallbackSaveDataBase = requestCallbackSaveDataBase;
    }

    public RequestCallbackSaveDataBase requestCallbackSaveDataBase;
    public interface  RequestCallbackSaveDataBase{
        boolean onResult(CityInfo mCityInfo,boolean sucess);
    }

}
