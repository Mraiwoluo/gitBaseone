package com.pvetec.weather.view;

import com.pvetec.weather.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zeu on 2017/1/9.
 */

public enum ForecastEnum {
    PartlyCloudy(R.drawable.weather_sun, R.drawable.bsun, R.string.app_name, "晴-多云", "Partly cloudy"),
    Sunny(R.drawable.weather_sun, R.drawable.bsun, R.string.app_name, "晴", "Sunny"),
    Cloudy(R.drawable.weather_cloudy, R.drawable.bnightcloud, R.string.app_name, "多云", "Cloudy"),
    Overcast(R.drawable.weather_overcast, R.drawable.bfrag, R.string.app_name, "阴", "Overcast"),
    ShowerRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "阵雨", "Shower"),
    Thundershower(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "雷阵雨", "Thunder showers"),
    ThundershowersHailstone(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "雷阵雨伴有冰雹", "Thundershowers hailstone"),
    Sleet(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "雨夹雪", "Sleet"),
    ightRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "小雨", "Slight rain"),
    ModerateRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "中雨", "Moderate rain"),
    HeavyRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "大雨", "Heavy rain"),
    TorrentialRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "暴雨", "Torrential rain"),
    Downpour(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "大暴雨", "Downpour"),
    SevereStorm(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "特大暴雨", "Severe storm"),
    SnowShower(R.drawable.weather_snow, R.drawable.bsnow, R.string.app_name, "阵雪", "Snow shower"),
    LightSnow(R.drawable.weather_snow, R.drawable.bsnow, R.string.app_name, "小雪", "Light snow "),
    ModerateSnow(R.drawable.weather_snow, R.drawable.bsnow, R.string.app_name, "中雪", "Moderate snow"),
    HeavySnow(R.drawable.weather_snow, R.drawable.bsnow, R.string.app_name, "大雪", "Heavy snow"),
    Snowstorm(R.drawable.weather_snow, R.drawable.bsnow, R.string.app_name, "暴雪", "Snowstorm"),
    Fog(R.drawable.weather_wind, R.drawable.bfrag, R.string.app_name, "雾", "Fog"),
    IceRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "冻雨", "Ice rain"),
    Sandstorm(R.drawable.weather_wind, R.drawable.bfrag, R.string.app_name, "沙尘暴", "Sandstorm"),
    Light2ModerateRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "小雨-中雨", "Light to moderate rain"),
    Moderate2HeavyRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "中雨-大雨", "Moderate to heavy rain"),
    Heavy2TorrentialRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "大雨-暴雨", "Heavy to torrential rain"),
    Torrential2TorrentialerRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "暴雨-大暴雨", "Heavy to torrentialer rain"),
    Torrential2TorrentialestRain(R.drawable.weather_rain, R.drawable.brain, R.string.app_name, "大暴雨-特大暴雨", "Heavy to torrentialest rain"),
    Light2ModerateSnow(R.drawable.weather_snow, R.drawable.bsnow, R.string.app_name, "小雪-中雪", "Light to moderate snow"),
    Moderate2HeavySnow(R.drawable.weather_snow, R.drawable.bsnow, R.string.app_name, "中雪-大雪", "Moderate to heavy snow"),
    Heavy2TorrentialSnow(R.drawable.weather_snow, R.drawable.bsnow, R.string.app_name, "大雪-暴雪", "Heavy to torrentialest snow"),
    Dust(R.drawable.weather_wind, R.drawable.bfrag, R.string.app_name, "浮尘", "Dust"),
    SandBlowing(R.drawable.weather_wind, R.drawable.bfrag, R.string.app_name, "扬沙", "Sand blowing"),
    StrongDustStorms(R.drawable.weather_wind, R.drawable.bfrag, R.string.app_name, "强沙尘暴", "Strong dust storms"),
    Haze(R.drawable.weather_haze, R.drawable.bfrag, R.string.app_name, "霾", "Haze");

    public final String name_cn;
    public final String name_en;
    public final int resid_text;
    public final int resid_icon;
    public final int resid_bg;


    ForecastEnum(int resid_icon, int resid_bg, int resid_text, String name_cn, String name_en) {
        this.resid_icon = resid_icon;
        this.resid_bg = resid_bg;
        this.resid_text = resid_text;

        this.name_cn = name_cn;
        this.name_en = name_en;
    }

    static Map<String, ForecastEnum> mCnForecasts;

    static {
        mCnForecasts = new HashMap<>();
        for (ForecastEnum forecast : ForecastEnum.values()) {
            mCnForecasts.put(forecast.name_cn, forecast);
        }
    }

    public static ForecastEnum get(String cn) {
        ForecastEnum forcast = null;
        if (null != cn) {
            forcast = mCnForecasts.get(cn);
            if (null == forcast) {
                if (cn.contains("-")) {
                    cn = cn.substring(cn.indexOf("-") + 1, cn.length());
                    forcast = mCnForecasts.get(cn);
                    if (forcast == null) {
                        //如果枚举中没有，设置默认
                        if(cn.contains("雨")){
                            forcast=mCnForecasts.get("小雨");
                        }else {
                            forcast = mCnForecasts.get("晴-多云");
                        }
                    }
                }else{
                    //如果枚举中没有，设置默认
                    if(cn.contains("雨")){
                        forcast=mCnForecasts.get("小雨");
                    }else {
                        forcast = mCnForecasts.get("晴-多云");
                    }
                }
            }
        } else {//设置默认
            forcast = mCnForecasts.get("晴-多云");
        }
        return forcast;
    }

    //根据温度获取背景
    public static ForecastEnum get(String cn, String temper) {
        ForecastEnum forcast = null;
        if (null != cn && null!= temper) {
            forcast = mCnForecasts.get(cn);
            if (null == forcast) {
                if (cn.contains("-")) {
                    cn = cn.substring(cn.indexOf("-") + 1, cn.length());
                    forcast = mCnForecasts.get(cn);
                    if (forcast == null) {
                        //如果枚举中没有，设置默认
                        if(cn.contains("雨")){
                            forcast=mCnForecasts.get("小雨");
                        }else {
                            forcast = mCnForecasts.get("晴-多云");
                        }
                    }
                }else{
                    //如果枚举中没有，设置默认
                    if(cn.contains("雨")){
                        forcast=mCnForecasts.get("小雨");
                    }else {
                        forcast = mCnForecasts.get("晴-多云");
                    }
                }
            }

            //根据温度转换
            String[] sTemper=temper.split("℃");
            if (null!=sTemper){
                if (sTemper.length==1){
                    int tmp=Integer.parseInt(sTemper[0]);
                    if (tmp <= 5){ //小于5度
                        forcast = mCnForecasts.get("小雪");
                    }
                }
            }

        } else {//设置默认
            forcast = mCnForecasts.get("晴-多云");
        }
        return forcast;
    }

}
