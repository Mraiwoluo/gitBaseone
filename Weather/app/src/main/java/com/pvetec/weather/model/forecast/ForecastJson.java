package com.pvetec.weather.model.forecast;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zeu on 2017/1/3.
 {
 "currentCity": "北京市",
 "date": "2013-07-17",
 "forecasts": [
 {
 "date": "周三(今天, 实时：24℃)",
 "weather": "多云",
 "wind": "微风",
 "temperature": "23℃~ 15℃"
 },
 {
 "date": "明天（周四）",
 "weather": "雷阵雨转中雨",
 "wind": "微风",
 "temperature": "29～22℃"
 },
 {
 "date": "后天（周五）",
 "weather": "阴转多云",
 "wind": "微风",
 "temperature": "31～23℃"
 },
 {
 "date": "大后天（周六）",
 "weather": "多云",
 "wind": "微风",
 "temperature": "31～24℃"
 },
 {
 "date": "大后天（周六）",
 "weather": "多云",
 "wind": "微风",
 "temperature": "31～24℃"
 }
 ]
 }
 */

public class ForecastJson {
    public static class ForecastBean {
        /**
         * date : 周三(今天, 实时：24℃)
         * weather : 多云
         * wind : 微风
         * temperature : 23℃~ 15℃
         */
        private String date;
        private String weather;
        private String weathericon;
        private String wind;
        private String temperature;
        private String temperarray;


        public String getWeatherIcon() {
            return weathericon;
        }

        public void setWeatherIcon(String weatherIcon) {
            this.weathericon = weatherIcon;
        }


        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }

        public String getWind() {
            return wind;
        }

        public void setWind(String wind) {
            this.wind = wind;
        }

        public String getTemperature() {
            return temperature;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }

        public String getTemperarray() {
            return temperarray;
        }

        public void setTemperarray(String temperarray) {
            this.temperarray = temperarray;
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("date", this.date);
                json.put("weather", this.weather);
                json.put("wind", this.wind);
                json.put("weathericon",this.weathericon);
                json.put("temperature", this.temperature);
                json.put("temperarray", this.temperarray);
            } catch (JSONException e) {
            }
            return json;
        }

        @Override
        public String toString() {
            return toJson().toString();
        }
    }

    /**
     * currentCity : 北京市
     * date : 2013-07-17
     * forecasts : [{"date":"周三(今天, 实时：24℃)","weather":"多云","wind":"微风","temperature":"23℃~ 15℃"},{"date":"明天（周四）","weather":"雷阵雨转中雨","wind":"微风","temperature":"29～22℃"},{"date":"后天（周五）","weather":"阴转多云","wind":"微风","temperature":"31～23℃"},{"date":"大后天（周六）","weather":"多云","wind":"微风","temperature":"31～24℃"},{"date":"大后天（周六）","weather":"多云","wind":"微风","temperature":"31～24℃"}]
     */

    private String city;
    private String date;
    private List<ForecastBean> forecasts;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ForecastBean> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<ForecastBean> forecasts) {
        this.forecasts = forecasts;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("city", city);
            obj.put("date", date);
            JSONArray array = new JSONArray();
            for (ForecastBean bean : forecasts) {
                if (null != bean) {
                    array.put(bean.toJson());
                }
            }
            obj.put("forecasts", array);
        } catch (JSONException e) {
        }
        return obj;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    public static List<ForecastJson> from(BaiduForecastJson gson) {
        List<ForecastJson> jsons = new ArrayList<>();
        if (null != gson) {
            List<BaiduForecastJson.ResultsBean> results = gson.getResults();
            if (null != results) {
                for (BaiduForecastJson.ResultsBean  result : results) {
                    if (null !=  result) {
                        ForecastJson json = new ForecastJson();
                        json.setDate(gson.getDate());
                        json.setCity( result.getCurrentCity());

                        List<BaiduForecastJson.ResultsBean.WeatherDataBean> dataBeans =  result.getWeather_data();
                        List<ForecastBean> forecastBeens = new ArrayList<>();
                        if (null != dataBeans) {
                            for (BaiduForecastJson.ResultsBean.WeatherDataBean dataBean : dataBeans) {
                                if (null != dataBean) {
                                    ForecastBean forecastBeen = new ForecastBean();
                                    String date = dataBean.getDate();
                                    if (null != date) {
                                        Pattern pattern = Pattern.compile(" *\\(? *,? *实时：-?\\d{1,2} * *℃ *\\)?");
                                        Matcher matcher = pattern.matcher(date);
                                        String temper = matcher.find() ? matcher.group() : null;
                                        if (null != temper) {
                                            String dat = date.replaceAll(" *\\(? *,? *实时：-?\\d{1,2} * *℃ *\\)?", "");
                                            dat += " (今天)";
                                            forecastBeen.setDate(dat);
                                            Pattern patt = Pattern.compile("-?\\d{1,2} *℃");
                                            Matcher matc = patt.matcher(temper);
                                            String tem = matc.find() ? matc.group(): "";
                                            forecastBeen.setTemperature(tem);
                                            forecastBeen.setTemperarray(dataBean.getTemperature());
                                        } else {
                                            forecastBeen.setDate(date);
                                            forecastBeen.setTemperature(dataBean.getTemperature());
                                            forecastBeen.setTemperarray(dataBean.getTemperature());
                                        }
                                    }
                                    forecastBeen.setWeather(dataBean.getWeather());
                                    forecastBeen.setWind(dataBean.getWind());
                                    forecastBeens.add(forecastBeen);
                                }
                            }
                        }
                        json.setForecasts(forecastBeens);
                        jsons.add(json);
                    }
                }
            }

        }
        return jsons;
    }


    public static ForecastJson fromJson(String str) {
        ForecastJson json = null;
        if (null != str) {
//            Gson gson = new Gson();
//            json = gson.fromJson(str, new TypeToken<ForecastJson>(){}.getType());
         try {
             json = JSON.parseObject(str, ForecastJson.class);
         }catch (Exception E){

         }
        }
        return json;
    }


    /**
     * 先转换成Gson对象，然后再转换成Json对象
     * @param str
     * @return
     */
    public static List<ForecastJson> fromGson(String str) {
        if (null != str) {
            try {
//                Gson gson = new Gson();
//                BaiduForecastJson baiduForecastJson = gson.fromJson(str, new TypeToken<BaiduForecastJson>() {
//                }.getType());
                BaiduForecastJson baiduForecastJson=JSON.parseObject(str, BaiduForecastJson.class);
                return from(baiduForecastJson);
            }catch (Exception e){
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }
}
