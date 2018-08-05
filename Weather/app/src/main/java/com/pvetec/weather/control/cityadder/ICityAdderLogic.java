package com.pvetec.weather.control.cityadder;

import com.pvetec.weather.model.cityadder.CityInfo;

import java.util.List;

/**
 * Created by zeu on 2016/12/30.
 */

public interface ICityAdderLogic {
    void init(ICityAdderView view);

    //城市添加
    void addCity(CityInfo info);
    //根据字符串搜索城市
    void searchCity(String city);

    boolean isCityAdded(Object cityInfo);
    List<CityInfo> getAddedCitys();
    void removeAddedCitys(CityInfo info);

    //使能gps
    void enableLocation(boolean enable);

    // 添加界面，添加新城市
    void addNewCity(CityInfo info);
    void removeCitys(CityInfo info);
}
