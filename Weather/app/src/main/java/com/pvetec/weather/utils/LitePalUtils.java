package com.pvetec.weather.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.pvetec.weather.R;
import com.pvetec.weather.model.forecast.ForecastJson;
import com.pvetec.weather.model.litepalmodel.WeatherInfo;
import com.pvetec.weather.module.CityAndWeather;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/31 0031.
 */

public class LitePalUtils {

    static final String TAG="LitePalUtils";

    private static LitePalUtils mLitePalUtils;

    private Context mContext;

    private SQLiteDatabase db;

    private LitePalUtils(Context mContext){
        this.mContext=mContext;
    }

    public static LitePalUtils getInstall(Context mContext){
        if(mLitePalUtils==null){
            mLitePalUtils=new LitePalUtils(mContext);
        }
        return mLitePalUtils;
    }


    public void initLitePal(){
        try {
            synchronized (LitePalUtils.class) {
                if(db==null){
                    db = Connector.getDatabase();
                }
            }
        }catch (Exception e){

        }

    }

    public SQLiteDatabase getSQLiteDatabase(){
        if(db==null){
            initLitePal();
        }
        return db;
    }

    public Cursor getCursorForCityNane(String cityName){
        Cursor mCursor=null;
        try{
            mCursor = DataSupport.findBySQL("select * from weatherinfo where cityname ='"+cityName+"'" );
            LogUtils.e(TAG,"select * from weatherinfo where cityname ='"+cityName+"'"+"------------ mCursor"+(mCursor==null?"null":"mCursor" ));

        }catch (Exception e){

        }
        return mCursor;
    }

    public int update(CityAndWeather mCityAndWeather,String cityName){
        int result=0;
        try {
            if(mCityAndWeather==null) return result;

            CityAndWeather cityAndWeather=mCityAndWeather;
            cityAndWeather.mCityName=cityName;

            ContentValues values = new ContentValues();
            values.put("cityname", cityAndWeather.mCityName);
            values.put("time", cityAndWeather.mTime);
            values.put("weathericon", cityAndWeather.mWeatherIcon);
            values.put("windspeed", cityAndWeather.mWindSpeed);
            values.put("wind", cityAndWeather.mWind);
            values.put("temperaturerange",cityAndWeather.mTemperatureRange );
            values.put("updateinfo", cityAndWeather.mUpdateInfo);
            values.put("currenttemperature", cityAndWeather.mCurrentTemperature);
            values.put("lastupdatetime",cityAndWeather.mLastUpdateTime );

            CityAndWeather.Forecast forecast=cityAndWeather.mForecastList.get(0);
            values.put("forecastday1obsdate",forecast.obsdate );
            values.put("forecastday1daycode", forecast.daycode);
            values.put("forecastday1weathericon",forecast.weathericon );
            values.put("forecastday1hightemperature",forecast.hightemperature);
            values.put("forecastday1lowtemperature", forecast.lowtemperature);
            values.put("forecastday1temperaturerange",forecast.temperaturerange );
            values.put("forecastday1windspeed", forecast.windspeed);
            values.put("forecastday1winddirection", forecast.winddirection);
            values.put("forecastday1wind", forecast.wind);

            CityAndWeather.Forecast forecast1=cityAndWeather.mForecastList.get(1);
            values.put("forecastday2obsdate", forecast1.obsdate);
            values.put("forecastday2daycode", forecast1.daycode);
            values.put("forecastday2weathericon",forecast1.weathericon );
            values.put("forecastday2hightemperature",forecast1.hightemperature );
            values.put("forecastday2lowtemperature", forecast1.lowtemperature);
            values.put("forecastday2temperaturerange", forecast1.temperaturerange);
            values.put("forecastday2windspeed", forecast1.windspeed);
            values.put("forecastday2winddirection",forecast1.winddirection );
            values.put("forecastday2wind", forecast1.wind);

            CityAndWeather.Forecast forecast2=cityAndWeather.mForecastList.get(2);
            values.put("forecastday3obsdate", forecast2.obsdate);
            values.put("forecastday3daycode", forecast2.daycode);
            values.put("forecastday3weathericon", forecast2.weathericon);
            values.put("forecastday3hightemperature", forecast2.hightemperature);
            values.put("forecastday3lowtemperature", forecast2.lowtemperature);
            values.put("forecastday3temperaturerange", forecast2.temperaturerange);
            values.put("forecastday3windspeed", forecast2.windspeed);
            values.put("forecastday3winddirection",forecast2.winddirection );
            values.put("forecastday3wind",forecast2.wind );

            CityAndWeather.Forecast forecast3=cityAndWeather.mForecastList.get(3);
            values.put("forecastday4obsdate",forecast3.obsdate );
            values.put("forecastday4daycode", forecast3.daycode);
            values.put("forecastday4weathericon", forecast3.weathericon);
            values.put("forecastday4hightemperature",forecast3.hightemperature );
            values.put("forecastday4lowtemperature",  forecast3.lowtemperature);
            values.put("forecastday4temperaturerange",forecast3.temperaturerange );
            values.put("forecastday4windspeed",forecast3.windspeed );
            values.put("forecastday4winddirection",forecast3.winddirection );
            values.put("forecastday4wind", forecast3.wind);

            result=DataSupport.updateAll(WeatherInfo.class, values, "cityname = ?", cityAndWeather.mCityName);
            LogUtils.i("m_tag","---lite--update------"+result+"--mCityName--"+(cityAndWeather.mCityName==null?"":cityAndWeather.mCityName));
        }catch (Exception e){
             LogUtils.i("m_tag","---lite--update------"+e.toString());
        }

        return result;
    }

    public int update(CityAndWeather mCityAndWeather,String cityName,String updateName){
        int result=0;
        try {
            if(mCityAndWeather==null) return result;

            CityAndWeather cityAndWeather=mCityAndWeather;
            cityAndWeather.mCityName=cityName;

            ContentValues values = new ContentValues();
            values.put("cityname", cityAndWeather.mCityName);
            values.put("time", cityAndWeather.mTime);
            values.put("weathericon", cityAndWeather.mWeatherIcon);
            values.put("windspeed", cityAndWeather.mWindSpeed);
            values.put("wind", cityAndWeather.mWind);
            values.put("temperaturerange",cityAndWeather.mTemperatureRange );
            values.put("updateinfo", cityAndWeather.mUpdateInfo);
            values.put("currenttemperature", cityAndWeather.mCurrentTemperature);
            values.put("lastupdatetime",cityAndWeather.mLastUpdateTime );

            CityAndWeather.Forecast forecast=cityAndWeather.mForecastList.get(0);
            values.put("forecastday1obsdate",forecast.obsdate );
            values.put("forecastday1daycode", forecast.daycode);
            values.put("forecastday1weathericon",forecast.weathericon );
            values.put("forecastday1hightemperature",forecast.hightemperature);
            values.put("forecastday1lowtemperature", forecast.lowtemperature);
            values.put("forecastday1temperaturerange",forecast.temperaturerange );
            values.put("forecastday1windspeed", forecast.windspeed);
            values.put("forecastday1winddirection", forecast.winddirection);
            values.put("forecastday1wind", forecast.wind);

            CityAndWeather.Forecast forecast1=cityAndWeather.mForecastList.get(1);
            values.put("forecastday2obsdate", forecast1.obsdate);
            values.put("forecastday2daycode", forecast1.daycode);
            values.put("forecastday2weathericon",forecast1.weathericon );
            values.put("forecastday2hightemperature",forecast1.hightemperature );
            values.put("forecastday2lowtemperature", forecast1.lowtemperature);
            values.put("forecastday2temperaturerange", forecast1.temperaturerange);
            values.put("forecastday2windspeed", forecast1.windspeed);
            values.put("forecastday2winddirection",forecast1.winddirection );
            values.put("forecastday2wind", forecast1.wind);

            CityAndWeather.Forecast forecast2=cityAndWeather.mForecastList.get(2);
            values.put("forecastday3obsdate", forecast2.obsdate);
            values.put("forecastday3daycode", forecast2.daycode);
            values.put("forecastday3weathericon", forecast2.weathericon);
            values.put("forecastday3hightemperature", forecast2.hightemperature);
            values.put("forecastday3lowtemperature", forecast2.lowtemperature);
            values.put("forecastday3temperaturerange", forecast2.temperaturerange);
            values.put("forecastday3windspeed", forecast2.windspeed);
            values.put("forecastday3winddirection",forecast2.winddirection );
            values.put("forecastday3wind",forecast2.wind );

            CityAndWeather.Forecast forecast3=cityAndWeather.mForecastList.get(3);
            values.put("forecastday4obsdate",forecast3.obsdate );
            values.put("forecastday4daycode", forecast3.daycode);
            values.put("forecastday4weathericon", forecast3.weathericon);
            values.put("forecastday4hightemperature",forecast3.hightemperature );
            values.put("forecastday4lowtemperature",  forecast3.lowtemperature);
            values.put("forecastday4temperaturerange",forecast3.temperaturerange );
            values.put("forecastday4windspeed",forecast3.windspeed );
            values.put("forecastday4winddirection",forecast3.winddirection );
            values.put("forecastday4wind", forecast3.wind);

            result=DataSupport.updateAll(WeatherInfo.class, values, "cityname = ?", updateName);
            LogUtils.i("m_tag","---lite--update------"+result+"--mCityName--"+(updateName==null?"":updateName));
        }catch (Exception e){
            LogUtils.i("m_tag","---lite--update------"+e.toString());
        }

        return result;
    }

    public int updateById(WeatherInfo mWeatherInfo,int id){
        int result=0;
        try {
            if(mWeatherInfo==null) return result;

            ContentValues values = new ContentValues();
            values.put("cityname", mWeatherInfo.getCityName());
            values.put("time", mWeatherInfo.getTime());
            values.put("weathericon", mWeatherInfo.getWeatherIcon());
            values.put("windspeed", mWeatherInfo.getWindSpeed());
            values.put("wind", mWeatherInfo.getWind());
            values.put("temperaturerange",mWeatherInfo.getTemperatureRange() );
            values.put("updateinfo", mWeatherInfo.getUpdateInfo());
            values.put("currenttemperature", mWeatherInfo.getCurrentTemperature());
            values.put("lastupdatetime",mWeatherInfo.getLastUpdateTime() );


            values.put("forecastday1obsdate",mWeatherInfo.getForecastDay1Obsdate() );
            values.put("forecastday1daycode", mWeatherInfo.getForecastDay1Daycode());
            values.put("forecastday1weathericon",mWeatherInfo.getForecastDay1WeatherIcon() );
            values.put("forecastday1hightemperature",mWeatherInfo.getForecastDay1HighTemperature());
            values.put("forecastday1lowtemperature", mWeatherInfo.getForecastDay1LowTemperature());
            values.put("forecastday1temperaturerange",mWeatherInfo.getForecastDay1TemperatureRange());
            values.put("forecastday1windspeed", mWeatherInfo.getForecastDay1WindSpeed());
            values.put("forecastday1winddirection", mWeatherInfo.getForecastDay1WindDirection());
            values.put("forecastday1wind", mWeatherInfo.getForecastDay1Wind());


            values.put("forecastday2obsdate", mWeatherInfo.getForecastDay2Obsdate() );
            values.put("forecastday2daycode", mWeatherInfo.getForecastDay2Daycode());
            values.put("forecastday2weathericon",mWeatherInfo.getForecastDay2WeatherIcon());
            values.put("forecastday2hightemperature",mWeatherInfo.getForecastDay2HighTemperature() );
            values.put("forecastday2lowtemperature", mWeatherInfo.getForecastDay2LowTemperature());
            values.put("forecastday2temperaturerange", mWeatherInfo.getForecastDay2TemperatureRange());
            values.put("forecastday2windspeed",mWeatherInfo.getForecastDay2WindSpeed());
            values.put("forecastday2winddirection",mWeatherInfo.getForecastDay2WindDirection() );
            values.put("forecastday2wind", mWeatherInfo.getForecastDay2Wind());


            values.put("forecastday3obsdate",mWeatherInfo.getForecastDay3Obsdate());
            values.put("forecastday3daycode", mWeatherInfo.getForecastDay3Daycode());
            values.put("forecastday3weathericon", mWeatherInfo.getForecastDay3WeatherIcon());
            values.put("forecastday3hightemperature", mWeatherInfo.getForecastDay3HighTemperature() );
            values.put("forecastday3lowtemperature", mWeatherInfo.getForecastDay3LowTemperature());
            values.put("forecastday3temperaturerange", mWeatherInfo.getForecastDay3TemperatureRange());
            values.put("forecastday3windspeed", mWeatherInfo.getForecastDay3WindSpeed());
            values.put("forecastday3winddirection",mWeatherInfo.getForecastDay3WindDirection() );
            values.put("forecastday3wind", mWeatherInfo.getForecastDay3Wind() );


            values.put("forecastday4obsdate",mWeatherInfo.getForecastDay4Obsdate());
            values.put("forecastday4daycode", mWeatherInfo.getForecastDay4Daycode());
            values.put("forecastday4weathericon", mWeatherInfo.getForecastDay4WeatherIcon());
            values.put("forecastday4hightemperature", mWeatherInfo.getForecastDay4HighTemperature() );
            values.put("forecastday4lowtemperature",   mWeatherInfo.getForecastDay4LowTemperature());
            values.put("forecastday4temperaturerange",mWeatherInfo.getForecastDay4TemperatureRange() );
            values.put("forecastday4windspeed",mWeatherInfo.getForecastDay4WindSpeed());
            values.put("forecastday4winddirection",mWeatherInfo.getForecastDay4WindDirection());
            values.put("forecastday4wind", mWeatherInfo.getForecastDay4Wind());

            result=DataSupport.updateAll(WeatherInfo.class, values, "id = ?", id+"");
            LogUtils.i("m_tag","---lite--update------"+result+"--id--"+"--city--"+mWeatherInfo.getCityName());
        }catch (Exception e){
            LogUtils.i("m_tag","---lite--update------"+e.toString());
        }

        return result;
    }

    public int delect(String cityName){
       return DataSupport.deleteAll(WeatherInfo.class, "cityname = ? ", cityName);
    }

    public int save(CityAndWeather weather,String cityName){
        int saveResult=0;
        WeatherInfo info = new WeatherInfo();
        if(weather==null) return saveResult;
      synchronized (LitePalUtils.class) {
          CityAndWeather mCityAndWeather = weather;

          info.setCityName(cityName);
          info.setTime(mCityAndWeather.mTime);
          info.setWeatherIcon(mCityAndWeather.mWeatherIcon);
          info.setWindDirection(mCityAndWeather.mWindDirection);
          info.setWindSpeed(mCityAndWeather.mWindSpeed);
          info.setWind(mCityAndWeather.mWind);
          info.setTemperatureRange(mCityAndWeather.mTemperatureRange);
          info.setCurrentTemperature(mCityAndWeather.mCurrentTemperature);
          info.setUpdateInfo(mCityAndWeather.mUpdateInfo);
          info.setLastUpdateTime(mCityAndWeather.mLastUpdateTime);

          CityAndWeather.Forecast forecast = mCityAndWeather.mForecastList.get(0);
          info.setForecastDay1Obsdate(forecast.obsdate);
          info.setForecastDay1Daycode(forecast.daycode);
          info.setForecastDay1WeatherIcon(forecast.weathericon);
          info.setForecastDay1HighTemperature(forecast.hightemperature);
          info.setForecastDay1LowTemperature(forecast.lowtemperature);
          info.setForecastDay1TemperatureRange(forecast.temperaturerange);
          info.setForecastDay1WindSpeed(forecast.windspeed);
          info.setForecastDay1WindDirection(forecast.winddirection);
          info.setForecastDay1Wind(forecast.wind);
          LogUtils.i("m_tag", "save--0--" + forecast.obsdate + "-----------" + forecast.weathericon);

          CityAndWeather.Forecast forecast1 = mCityAndWeather.mForecastList.get(1);
          info.setForecastDay2Obsdate(forecast1.obsdate);
          info.setForecastDay2Daycode(forecast1.daycode);
          info.setForecastDay2WeatherIcon(forecast1.weathericon);
          info.setForecastDay2HighTemperature(forecast1.hightemperature);
          info.setForecastDay2LowTemperature(forecast1.lowtemperature);
          info.setForecastDay2TemperatureRange(forecast1.temperaturerange);
          info.setForecastDay2WindSpeed(forecast1.windspeed);
          info.setForecastDay2WindDirection(forecast1.winddirection);
          info.setForecastDay2Wind(forecast1.wind);
          LogUtils.i("m_tag", "save--1--" + forecast.obsdate + "-----------" + forecast.weathericon);

          CityAndWeather.Forecast forecast2 = mCityAndWeather.mForecastList.get(2);
          info.setForecastDay3Obsdate(forecast2.obsdate);
          info.setForecastDay3Daycode(forecast2.daycode);
          info.setForecastDay3WeatherIcon(forecast2.weathericon);
          info.setForecastDay3HighTemperature(forecast2.hightemperature);
          info.setForecastDay3LowTemperature(forecast2.lowtemperature);
          info.setForecastDay3TemperatureRange(forecast2.temperaturerange);
          info.setForecastDay3WindSpeed(forecast2.windspeed);
          info.setForecastDay3WindDirection(forecast2.winddirection);
          info.setForecastDay3Wind(forecast2.wind);
          LogUtils.i("m_tag", "save--2--" + forecast.obsdate + "-----------" + forecast.weathericon);

          CityAndWeather.Forecast forecast3 = mCityAndWeather.mForecastList.get(3);
          info.setForecastDay4Obsdate(forecast3.obsdate);
          info.setForecastDay4Daycode(forecast3.daycode);
          info.setForecastDay4WeatherIcon(forecast3.weathericon);
          info.setForecastDay4HighTemperature(forecast3.hightemperature);
          info.setForecastDay4LowTemperature(forecast3.lowtemperature);
          info.setForecastDay4TemperatureRange(forecast3.temperaturerange);
          info.setForecastDay4WindSpeed(forecast3.windspeed);
          info.setForecastDay4WindDirection(forecast3.winddirection);
          info.setForecastDay4Wind(forecast3.wind);
          LogUtils.i("m_tag", "save--3--" + forecast.obsdate + "-----------" + forecast.weathericon);

          if (findWeatherInfo(cityName)) return 0;
          if(findAllWeatherInfoCount()>=6) return 0;
          boolean saveResultb = info.save();
          LogUtils.i("m_tag", "--lite-----saveResultb---" + saveResultb);
          return saveResult;
      }

    }

    public boolean saveWeatherInfo(WeatherInfo mWeatherInfo){
        boolean save=false;
        try {
            synchronized (LitePalUtils.class) {
                WeatherInfo saveWeatherInfo = new WeatherInfo();

                saveWeatherInfo.setCityName(mWeatherInfo.getCityName());
                saveWeatherInfo.setTime(mWeatherInfo.getTime());
                saveWeatherInfo.setWeatherIcon(mWeatherInfo.getWeatherIcon());
                saveWeatherInfo.setWindDirection(mWeatherInfo.getWindDirection());
                saveWeatherInfo.setWindSpeed(mWeatherInfo.getWindSpeed());
                saveWeatherInfo.setWind(mWeatherInfo.getWind());
                saveWeatherInfo.setTemperatureRange(mWeatherInfo.getTemperatureRange());
                saveWeatherInfo.setCurrentTemperature(mWeatherInfo.getCurrentTemperature());
                saveWeatherInfo.setUpdateInfo(mWeatherInfo.getUpdateInfo());
                saveWeatherInfo.setLastUpdateTime(mWeatherInfo.getLastUpdateTime());

                saveWeatherInfo.setForecastDay1Obsdate(mWeatherInfo.getForecastDay1Obsdate());
                saveWeatherInfo.setForecastDay1Daycode(mWeatherInfo.getForecastDay1Daycode());
                saveWeatherInfo.setForecastDay1WeatherIcon(mWeatherInfo.getForecastDay1WeatherIcon());
                saveWeatherInfo.setForecastDay1HighTemperature(mWeatherInfo.getForecastDay1HighTemperature());
                saveWeatherInfo.setForecastDay1LowTemperature(mWeatherInfo.getForecastDay1LowTemperature());
                saveWeatherInfo.setForecastDay1TemperatureRange(mWeatherInfo.getForecastDay1TemperatureRange());
                saveWeatherInfo.setForecastDay1WindSpeed(mWeatherInfo.getForecastDay1WindSpeed());
                saveWeatherInfo.setForecastDay1WindDirection(mWeatherInfo.getForecastDay1WindDirection());
                saveWeatherInfo.setForecastDay1Wind(mWeatherInfo.getForecastDay1Wind());

                saveWeatherInfo.setForecastDay2Obsdate(mWeatherInfo.getForecastDay2Obsdate());
                saveWeatherInfo.setForecastDay2Daycode(mWeatherInfo.getForecastDay2Daycode());
                saveWeatherInfo.setForecastDay2WeatherIcon(mWeatherInfo.getForecastDay2WeatherIcon());
                saveWeatherInfo.setForecastDay2HighTemperature(mWeatherInfo.getForecastDay2HighTemperature());
                saveWeatherInfo.setForecastDay2LowTemperature(mWeatherInfo.getForecastDay2LowTemperature());
                saveWeatherInfo.setForecastDay2TemperatureRange(mWeatherInfo.getForecastDay2TemperatureRange());
                saveWeatherInfo.setForecastDay2WindSpeed(mWeatherInfo.getForecastDay2WindSpeed());
                saveWeatherInfo.setForecastDay2WindDirection(mWeatherInfo.getForecastDay2WindDirection());
                saveWeatherInfo.setForecastDay2Wind(mWeatherInfo.getForecastDay2Wind());


                saveWeatherInfo.setForecastDay3Obsdate(mWeatherInfo.getForecastDay3Obsdate());
                saveWeatherInfo.setForecastDay3Daycode(mWeatherInfo.getForecastDay3Daycode());
                saveWeatherInfo.setForecastDay3WeatherIcon(mWeatherInfo.getForecastDay3WeatherIcon());
                saveWeatherInfo.setForecastDay3HighTemperature(mWeatherInfo.getForecastDay3HighTemperature());
                saveWeatherInfo.setForecastDay3LowTemperature(mWeatherInfo.getForecastDay3LowTemperature());
                saveWeatherInfo.setForecastDay3TemperatureRange(mWeatherInfo.getForecastDay3TemperatureRange());
                saveWeatherInfo.setForecastDay3WindSpeed(mWeatherInfo.getForecastDay3WindSpeed());
                saveWeatherInfo.setForecastDay3WindDirection(mWeatherInfo.getForecastDay3WindDirection());
                saveWeatherInfo.setForecastDay3Wind(mWeatherInfo.getForecastDay3Wind());

                saveWeatherInfo.setForecastDay4Obsdate(mWeatherInfo.getForecastDay4Obsdate());
                saveWeatherInfo.setForecastDay4Daycode(mWeatherInfo.getForecastDay4Daycode());
                saveWeatherInfo.setForecastDay4WeatherIcon(mWeatherInfo.getForecastDay4WeatherIcon());
                saveWeatherInfo.setForecastDay4HighTemperature(mWeatherInfo.getForecastDay4HighTemperature());
                saveWeatherInfo.setForecastDay4LowTemperature(mWeatherInfo.getForecastDay4LowTemperature());
                saveWeatherInfo.setForecastDay4TemperatureRange(mWeatherInfo.getForecastDay4TemperatureRange());
                saveWeatherInfo.setForecastDay4WindSpeed(mWeatherInfo.getForecastDay4WindSpeed());
                saveWeatherInfo.setForecastDay4WindDirection(mWeatherInfo.getForecastDay4WindDirection());
                saveWeatherInfo.setForecastDay4Wind(mWeatherInfo.getForecastDay4Wind());

                if (findWeatherInfo(mWeatherInfo.getCityName())) return false;
                if(findAllWeatherInfoCount()>=6) return false;
                save = saveWeatherInfo.save();
            }

        }catch (Exception e){

        }
        return save;
    }

    private int findAllWeatherInfoCount(){
        int count=0;
        try {
            List<WeatherInfo> list = DataSupport.findAll(WeatherInfo.class);
            if (list != null && list.size() != 0) {
                count = list.size();
            }
        }catch (Exception e){

        }
        return count;
    }

    public boolean findWeatherInfo(String cityName){
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

    public ForecastJson getForecastJson(String cityName){
        ForecastJson mForecastJson=null;
        try {
            List<WeatherInfo> infoList = DataSupport.where("cityname = ?", cityName).find(WeatherInfo.class);
            if (infoList != null && infoList.size() != 0) {

                WeatherInfo mWeatherInfo = infoList.get(0);
                mForecastJson = creatForecastJson(mWeatherInfo);
            }
        }catch (Exception e){

        }
        return mForecastJson;
    }

    public ForecastJson creatForecastJson(WeatherInfo mWeatherInfo){
        ForecastJson mForecastJson=new ForecastJson();
        mForecastJson.setCity(mWeatherInfo.getCityName());
        mForecastJson.setDate(mWeatherInfo.getForecastDay1Obsdate());

        List<ForecastJson.ForecastBean> list=new ArrayList<>();


       ForecastJson.ForecastBean mForecastBean=new ForecastJson.ForecastBean();
       mForecastBean.setDate(mWeatherInfo.getForecastDay1Obsdate());
       mForecastBean.setTemperarray(mWeatherInfo.getForecastDay1TemperatureRange());
       mForecastBean.setWeather(mWeatherInfo.getForecastDay1WeatherIcon());
       mForecastBean.setWeatherIcon(mWeatherInfo.getForecastDay1WeatherIcon());
       mForecastBean.setTemperature(mWeatherInfo.getCurrentTemperature());
       mForecastBean.setWind(mWeatherInfo.getForecastDay1Wind());
       list.add(mForecastBean);

        ForecastJson.ForecastBean mForecastBean1=new ForecastJson.ForecastBean();
        mForecastBean1.setDate(mWeatherInfo.getForecastDay2Obsdate());
        mForecastBean1.setTemperarray(mWeatherInfo.getForecastDay2TemperatureRange());
        mForecastBean1.setWeather(mWeatherInfo.getForecastDay2WeatherIcon());
        mForecastBean1.setWeatherIcon(mWeatherInfo.getForecastDay2WeatherIcon());
        mForecastBean1.setTemperature(mWeatherInfo.getCurrentTemperature());
        mForecastBean1.setWind(mWeatherInfo.getForecastDay2Wind());
        list.add(mForecastBean1);


        ForecastJson.ForecastBean mForecastBean2=new ForecastJson.ForecastBean();
        mForecastBean2.setDate(mWeatherInfo.getForecastDay3Obsdate());
        mForecastBean2.setTemperarray(mWeatherInfo.getForecastDay3TemperatureRange());
        mForecastBean2.setWeather(mWeatherInfo.getForecastDay3WeatherIcon());
        mForecastBean2.setWeatherIcon(mWeatherInfo.getForecastDay3WeatherIcon());
        mForecastBean2.setTemperature(mWeatherInfo.getCurrentTemperature());
        mForecastBean2.setWind(mWeatherInfo.getForecastDay3Wind());
        list.add(mForecastBean2);

        ForecastJson.ForecastBean mForecastBean3=new ForecastJson.ForecastBean();
        mForecastBean3.setDate(mWeatherInfo.getForecastDay4Obsdate());
        mForecastBean3.setTemperarray(mWeatherInfo.getForecastDay4TemperatureRange());
        mForecastBean3.setWeather(mWeatherInfo.getForecastDay4WeatherIcon());
        mForecastBean3.setWeatherIcon(mWeatherInfo.getForecastDay4WeatherIcon());
        mForecastBean3.setTemperature(mWeatherInfo.getCurrentTemperature());
        mForecastBean3.setWind(mWeatherInfo.getForecastDay4Wind());
        list.add(mForecastBean3);

        mForecastJson.setForecasts(list);
        return mForecastJson;
    }

    private LocalWeatherForecast weatherforecast;
    private List<LocalDayWeatherForecast> forecastlist = null;
    public CityAndWeather AmapWeatherToCityAndWeather(LocalWeatherForecastResult forecastResult, LocalWeatherLiveResult weatherLiveResult, String name){
        if(forecastResult!=null){
            try {
                synchronized (LitePalUtils.class) {

                    CityAndWeather mCityAndWeather = new CityAndWeather();
                    if(weatherLiveResult==null) return mCityAndWeather;
                     LogUtils.i(TAG,"------AmapWeatherToCityAndWeather  ----name="+weatherLiveResult.getLiveResult().getCity());
                    mCityAndWeather.mCityName = weatherLiveResult.getLiveResult().getCity();
                    weatherforecast = forecastResult.getForecastResult();
                    forecastlist= weatherforecast.getWeatherForecast();

                    mCityAndWeather.mTime = weatherLiveResult.getLiveResult().getReportTime();
                    mCityAndWeather.mWeatherIcon = weatherLiveResult.getLiveResult().getWeather();
                    mCityAndWeather.mWindDirection = weatherLiveResult.getLiveResult().getWindDirection();
                    mCityAndWeather.mWindSpeed = weatherLiveResult.getLiveResult().getWindPower();
                    mCityAndWeather.mWind = weatherLiveResult.getLiveResult().getWindDirection();
                    mCityAndWeather.mTemperatureRange = forecastlist.get(0).getNightTemp() + "℃" + " ~ " + forecastlist.get(0).getDayTemp();
                    mCityAndWeather.mCurrentTemperature = weatherLiveResult.getLiveResult().getTemperature();
                    mCityAndWeather.mUpdateInfo = weatherLiveResult.getLiveResult().getReportTime();
                    mCityAndWeather.mLastUpdateTime = weatherLiveResult.getLiveResult().getReportTime();

                    ArrayList<CityAndWeather.Forecast> list = new ArrayList<>();

                    CityAndWeather.Forecast mForecast1 = new CityAndWeather.Forecast();
                    LocalDayWeatherForecast mLocalDayWeatherForecast1=forecastlist.get(0);
                    mForecast1.obsdate = getForecastDayObsdateChineseFormat(mLocalDayWeatherForecast1.getDate());
                    mForecast1.daycode = getForecastDayDaycodeString(mLocalDayWeatherForecast1.getWeek());
                    mForecast1.weathericon = weatherLiveResult.getLiveResult().getWeather();
                    mForecast1.temperaturerange = mLocalDayWeatherForecast1.getNightTemp() + "℃" + " ~ " + mLocalDayWeatherForecast1.getDayTemp() + "℃";
                    mForecast1.windspeed =  weatherLiveResult.getLiveResult().getWindPower();
                    mForecast1.winddirection = weatherLiveResult.getLiveResult().getWindDirection();
                    mForecast1.wind = weatherLiveResult.getLiveResult().getWindDirection();
                    mForecast1.hightemperature = mLocalDayWeatherForecast1.getDayTemp();
                    mForecast1.lowtemperature = mLocalDayWeatherForecast1.getNightTemp();
                    list.add(mForecast1);

                    CityAndWeather.Forecast mForecast2 = new CityAndWeather.Forecast();
                    LocalDayWeatherForecast mLocalDayWeatherForecast2=forecastlist.get(1);
                    mForecast2.obsdate = getForecastDayObsdateChineseFormat(mLocalDayWeatherForecast2.getDate());
                    mForecast2.daycode = getForecastDayDaycodeString(mLocalDayWeatherForecast2.getWeek());
                    mForecast2.weathericon =mLocalDayWeatherForecast2.getDayWeather();
                    mForecast2.temperaturerange = mLocalDayWeatherForecast2.getNightTemp() + "℃" + " ~ " + mLocalDayWeatherForecast2.getDayTemp() + "℃";
                    mForecast2.windspeed = mLocalDayWeatherForecast2.getDayWindPower();
                    mForecast2.winddirection = mLocalDayWeatherForecast2.getDayWindDirection();
                    mForecast2.wind = mLocalDayWeatherForecast2.getDayWindDirection();
                    mForecast2.hightemperature = mLocalDayWeatherForecast2.getDayTemp();
                    mForecast2.lowtemperature = mLocalDayWeatherForecast2.getNightTemp();
                    list.add(mForecast2);

                    CityAndWeather.Forecast mForecast3 = new CityAndWeather.Forecast();
                    LocalDayWeatherForecast mLocalDayWeatherForecast3=forecastlist.get(2);
                    mForecast3.obsdate = getForecastDayObsdateChineseFormat(mLocalDayWeatherForecast3.getDate());
                    mForecast3.daycode = getForecastDayDaycodeString(mLocalDayWeatherForecast3.getWeek());
                    mForecast3.weathericon = mLocalDayWeatherForecast3.getDayWeather();
                    mForecast3.temperaturerange = mLocalDayWeatherForecast3.getNightTemp() + "℃" + " ~ " + mLocalDayWeatherForecast3.getDayTemp() + "℃";
                    mForecast3.windspeed = mLocalDayWeatherForecast3.getDayWindPower();
                    mForecast3.winddirection = mLocalDayWeatherForecast3.getDayWindDirection();
                    mForecast3.wind = mLocalDayWeatherForecast3.getDayWindDirection();
                    mForecast3.hightemperature = mLocalDayWeatherForecast3.getDayTemp();
                    mForecast3.lowtemperature = mLocalDayWeatherForecast3.getNightTemp();
                    list.add(mForecast3);

                    CityAndWeather.Forecast mForecast4 = new CityAndWeather.Forecast();
                    LocalDayWeatherForecast mLocalDayWeatherForecast4=forecastlist.get(3);
                    mForecast4.obsdate = getForecastDayObsdateChineseFormat(mLocalDayWeatherForecast4.getDate());
                    mForecast4.daycode = getForecastDayDaycodeString(mLocalDayWeatherForecast4.getWeek());
                    mForecast4.weathericon = mLocalDayWeatherForecast4.getDayWeather();
                    mForecast4.temperaturerange = mLocalDayWeatherForecast4.getNightTemp() + "℃" + " ~ " + mLocalDayWeatherForecast4.getDayTemp() + "℃";
                    mForecast4.windspeed = mLocalDayWeatherForecast4.getDayWindPower();
                    mForecast4.winddirection = mLocalDayWeatherForecast4.getDayWindDirection();
                    mForecast4.wind = mLocalDayWeatherForecast4.getDayWindDirection();
                    mForecast4.hightemperature = mLocalDayWeatherForecast4.getDayTemp();
                    mForecast4.lowtemperature = mLocalDayWeatherForecast4.getNightTemp();
                    list.add(mForecast4);

                    mCityAndWeather.mForecastList = list;

                    return mCityAndWeather;
                }
            }catch (Exception e){

            }
        }
        return null;
    }

    private String getWindText(String wind){
        String windResult="无风向";
        if(TextUtils.isEmpty(wind)) return windResult;
        if(wind.equals("东北")){
            windResult="东北风";
        }else if(wind.equals("东")){
            windResult="东风";
        }else if(wind.equals("东南")){
            windResult="东南风";
        }else if(wind.equals("南")){
            windResult="南风";
        }else if(wind.equals("西南")){
            windResult="西南风";
        }else if(wind.equals("西")){
            windResult="西风";
        }else if(wind.equals("西北")){
            windResult="西北风";
        }else if(wind.equals("北")){
            windResult="北风";
        }else if(wind.equals("旋转不定")){
            windResult="旋转不定";
        }
        return windResult;
    }

    private String getForecastDayDaycodeString(String forcastDay) {
        String forecastDayDaycodeString = "--";
        if (forcastDay.equals("1")) {
            forecastDayDaycodeString = mContext.getResources().getString(R.string.monday);
        } else if (forcastDay.equals("2")) {
            forecastDayDaycodeString = mContext.getResources().getString(R.string.tuesday);
        } else if (forcastDay.equals("3")) {
            forecastDayDaycodeString = mContext.getResources().getString(R.string.wednesday);
        } else if (forcastDay.equals("4")) {
            forecastDayDaycodeString = mContext.getResources().getString(R.string.thursday);
        } else if (forcastDay.equals("5")) {
            forecastDayDaycodeString = mContext.getResources().getString(R.string.friday);
        } else if (forcastDay.equals("6")) {
            forecastDayDaycodeString = mContext.getResources().getString(R.string.saturday);
        } else if (forcastDay.equals("7")) {
            forecastDayDaycodeString = mContext.getResources().getString(R.string.sunday);
        }
        return forecastDayDaycodeString;
    }

    private String getForecastDayObsdateChineseFormat(String mTime){
        String result="";
        if(!TextUtils.isEmpty(mTime)){
            String[] timeArray=mTime.split("-");
            if(timeArray!=null&&timeArray.length==3){
                String mMonty = mContext.getResources().getString(R.string.month);
                String  mDay = mContext.getResources().getString(R.string.day);
                result = timeArray[1]+mMonty+timeArray[2]+mDay;
            }
        }
        return result;
    }



    private int getWeatherIcon(String icon){
        int result=0;
        if(TextUtils.isEmpty(icon)) return result;
        if(icon.equals("晴")){

        }else if(icon.equals("多云")){

        }else if(icon.equals("阴")){

        }else if(icon.equals("阵雨")){

        }else if(icon.equals("雷阵雨")){

        }else if(icon.equals("雷阵雨并伴有冰雹")){

        }else if(icon.equals("雨夹雪")){

        }else if(icon.equals("小雨")){

        }else if(icon.equals("中雨")){

        }else if(icon.equals("大雨")){

        }else if(icon.equals("暴雨")){

        }else if(icon.equals("大暴雨")){

        }else if(icon.equals("特大暴雨")){

        }else if(icon.equals("阵雪")){

        }else if(icon.equals("小雪")){

        }else if(icon.equals("中雪")){

        }else if(icon.equals("大雪")){

        }else if(icon.equals("暴雪")){

        }else if(icon.equals("雾")){

        }else if(icon.equals("冻雨")){

        }else if(icon.equals("沙尘暴")){

        }else if(icon.equals("小雨-中雨")){

        }else if(icon.equals("中雨-大雨")){

        }else if(icon.equals("大雨-暴雨")){

        }else if(icon.equals("暴雨-大暴雨")){

        }else if(icon.equals("大暴雨-特大暴雨")){

        }else if(icon.equals("小雪-中雪")){

        }else if(icon.equals("中雪-大雪")){

        }else if(icon.equals("大雪-暴雪")){

        }else if(icon.equals("浮尘")){

        }else if(icon.equals("扬沙")){

        }else if(icon.equals("强沙尘暴")){

        }else if(icon.equals("飑")){

        }else if(icon.equals("龙卷风")){

        }else if(icon.equals("弱高吹雪")){

        }else if(icon.equals("轻雾")){

        }



        return result;
    }


}
