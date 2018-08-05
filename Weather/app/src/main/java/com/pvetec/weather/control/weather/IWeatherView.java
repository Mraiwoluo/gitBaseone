package com.pvetec.weather.control.weather;

import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;

import java.util.List;

/**
 * Created by zeu on 2017/1/3.
 */

public interface IWeatherView {

    void updateSelectedItem(CityInfo info);
    void updateWeather(CityInfo info, ForecastJson json);
    void updateWeatherStatus(boolean status);
    void updateCityList();
    void locationChange();
    void netWorkError();
    void initWeather(List<CityInfo> citys);
    void updateAddNewCity(CityInfo info);
    void updateDataBases();

}
