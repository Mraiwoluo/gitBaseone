package com.pvetec.weather.control.forecast;

import com.pvetec.weather.model.forecast.ForecastJson;

/**
 * Created by zeu on 2017/1/3.
 */

public interface IForecastView {
    void updateForcast(ForecastJson json);
}
