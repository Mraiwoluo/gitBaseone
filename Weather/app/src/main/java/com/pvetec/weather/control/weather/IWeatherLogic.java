package com.pvetec.weather.control.weather;

import com.pvetec.weather.model.cityadder.CityInfo;

/**
 * Created by zeu on 2017/1/3.
 */

public interface IWeatherLogic {
    void requstAddCity(CityInfo mCityInfo);
    void requstUpdateWeather(CityInfo info);
    void updateSelectedItem(CityInfo info);
    void initWeather();
    void onLocationResult(boolean status);
    void updateDataBases();
}
