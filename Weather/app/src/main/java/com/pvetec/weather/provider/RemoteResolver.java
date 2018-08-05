package com.pvetec.weather.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;

/**
 * Created by zeu on 2017/1/13.
 */

public class RemoteResolver {
    public static final String URI_WEATHER_CITY = "content://com.zeu.weather.city";
    public static final String URI_WEATHER_LOCAL = "content://com.zeu.weather.local";
    ContentResolver mContentResolver;
    public RemoteResolver(final Context context) {
        mContentResolver = context.getContentResolver();
        ForecastProvider.getInstance(context).addForecastChangedListener(new ForecastProvider.ForecastChangedListener() {
            @Override
            public void onChanged(CityInfo info, ForecastJson json) {
                if (null != info && null != json) {
                    CityInfo city = LocationProvider.getInstance(context).getLocationCity();
                    if (info.equals(city)) {
                        mContentResolver.notifyChange(Uri.parse(URI_WEATHER_LOCAL), null);
                    } else {
                        mContentResolver.notifyChange(Uri.parse(URI_WEATHER_CITY), null);
                    }
                }
            }
        });
    }
}
