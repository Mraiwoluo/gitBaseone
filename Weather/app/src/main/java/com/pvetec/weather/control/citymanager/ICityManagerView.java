package com.pvetec.weather.control.citymanager;

import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;

import java.util.List;

/**
 * Created by zeu on 2017/1/5.
 */

public interface ICityManagerView {
    void updateCityView(List<CityInfo> citys);
    void showDeleteIcon(boolean deleteMode);
    void gotoSpecialForcast(CityInfo info);
    void updateLocalCity(CityInfo info);
    void updateForecast(CityInfo info, ForecastJson json);
    void finishView();
}
