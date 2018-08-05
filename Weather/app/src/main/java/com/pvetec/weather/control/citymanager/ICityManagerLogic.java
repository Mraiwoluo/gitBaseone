package com.pvetec.weather.control.citymanager;

import com.pvetec.weather.model.cityadder.CityInfo;

/**
 * Created by zeu on 2017/1/5.
 */

public interface ICityManagerLogic {
    void performLongClick();
    void performShortClick(CityInfo info); //进入正常模式
    void deleteCity(CityInfo info);
    void finishView();
}
