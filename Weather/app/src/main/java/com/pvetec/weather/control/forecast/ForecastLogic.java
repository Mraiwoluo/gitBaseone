package com.pvetec.weather.control.forecast;

import android.content.Context;

import com.pvetec.weather.control.cityadder.CityAdderLogic;
import com.pvetec.weather.model.cityadder.CityInfo;

import java.util.LinkedList;

/**
 * Created by zeu on 2017/1/3.
 */

public class ForecastLogic implements IForecastLogic {
    IForecastView mView;
    LinkedList<CityInfo> mCitys;
    CityAdderLogic mCityAdderLogic;
    Context mComtext;
   static ForecastLogic mForecastLogic;


    public static ForecastLogic getInstance(Context context){
        if(mForecastLogic==null){
            mForecastLogic=new ForecastLogic(context);
        }
        return mForecastLogic;
    }

    private ForecastLogic(Context context) {
        mComtext=context;
        mCitys = new LinkedList<>();

    }


    @Override
    public void requstAddCity() {
        //do none
    }

    @Override
    public void requstUpdateWeather(String cityEnPath) {

    }

    public CityInfo getCityInfo(String cityPath) {
        if (null != cityPath) {
            for (CityInfo city : mCitys) {
                if (null != city) {
                    String path;

                    return city;

                }
            }
        }
        return null;
    }
}
