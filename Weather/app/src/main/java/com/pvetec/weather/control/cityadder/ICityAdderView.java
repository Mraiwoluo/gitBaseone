package com.pvetec.weather.control.cityadder;

import com.pvetec.weather.model.cityadder.CityInfo;

import java.util.List;

/**
 * Created by zeu on 2017/1/5.
 */

public interface ICityAdderView {
    void updateLocationState(int state);
    void updateLocationCity(CityInfo city);
    void updateCitySearchResult(List<CityInfo> citys);
    void updateHotCitys(List<CityInfo> citys);
    void onAddCityStatus(CityInfo city,boolean status);
}
