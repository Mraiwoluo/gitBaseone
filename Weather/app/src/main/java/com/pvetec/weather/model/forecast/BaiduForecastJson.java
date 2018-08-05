package com.pvetec.weather.model.forecast;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by zeu on 2017/1/5.
 */

public class BaiduForecastJson {
    /**
     * error : 0
     * status : success
     * date : 2017-01-05
     * results : [{"currentCity":"北京","pm25":"318","index":[{"title":"穿衣","zs":"冷","tipt":"穿衣指数","des":"天气冷，建议着棉服、羽绒服、皮夹克加羊毛衫等冬季服装。年老体弱者宜着厚棉衣、冬大衣或厚羽绒服。"},{"title":"洗车","zs":"不宜","tipt":"洗车指数","des":"不宜洗车，未来24小时内有霾，如果在此期间洗车，会弄脏您的爱车。"},{"title":"旅游","zs":"较不宜","tipt":"旅游指数","des":"空气质量差，不适宜旅游"},{"title":"感冒","zs":"较易发","tipt":"感冒指数","des":"天气转凉，空气湿度较大，较易发生感冒，体质较弱的朋友请注意适当防护。"},{"title":"运动","zs":"较不宜","tipt":"运动指数","des":"有扬沙或浮尘，建议适当停止户外运动，选择在室内进行运动，以避免吸入更多沙尘，有损健康。"},{"title":"紫外线强度","zs":"最弱","tipt":"紫外线强度指数","des":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"}],"weather_data":[{"date":"周四 01月05日 (实时：2℃)","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/xiaoxue.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/mai.png","weather":"小雪转霾","wind":"北风微风","temperature":"2 ~ -4℃"},{"date":"周五","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/mai.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/mai.png","weather":"霾","wind":"南风微风","temperature":"4 ~ -2℃"},{"date":"周六","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/xiaoxue.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/xiaoxue.png","weather":"小雪","wind":"南风微风","temperature":"1 ~ -4℃"},{"date":"周日","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/qing.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/qing.png","weather":"晴","wind":"北风3-4级","temperature":"2 ~ -5℃"}]}]
     */

    private int error;
    private String status;
    private String date;
    private List<ResultsBean> results;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        /**
         * currentCity : 北京
         * pm25 : 318
         * index : [{"title":"穿衣","zs":"冷","tipt":"穿衣指数","des":"天气冷，建议着棉服、羽绒服、皮夹克加羊毛衫等冬季服装。年老体弱者宜着厚棉衣、冬大衣或厚羽绒服。"},{"title":"洗车","zs":"不宜","tipt":"洗车指数","des":"不宜洗车，未来24小时内有霾，如果在此期间洗车，会弄脏您的爱车。"},{"title":"旅游","zs":"较不宜","tipt":"旅游指数","des":"空气质量差，不适宜旅游"},{"title":"感冒","zs":"较易发","tipt":"感冒指数","des":"天气转凉，空气湿度较大，较易发生感冒，体质较弱的朋友请注意适当防护。"},{"title":"运动","zs":"较不宜","tipt":"运动指数","des":"有扬沙或浮尘，建议适当停止户外运动，选择在室内进行运动，以避免吸入更多沙尘，有损健康。"},{"title":"紫外线强度","zs":"最弱","tipt":"紫外线强度指数","des":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"}]
         * weather_data : [{"date":"周四 01月05日 (实时：2℃)","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/xiaoxue.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/mai.png","weather":"小雪转霾","wind":"北风微风","temperature":"2 ~ -4℃"},{"date":"周五","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/mai.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/mai.png","weather":"霾","wind":"南风微风","temperature":"4 ~ -2℃"},{"date":"周六","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/xiaoxue.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/xiaoxue.png","weather":"小雪","wind":"南风微风","temperature":"1 ~ -4℃"},{"date":"周日","dayPictureUrl":"http://api.map.baidu.com/images/weather/day/qing.png","nightPictureUrl":"http://api.map.baidu.com/images/weather/night/qing.png","weather":"晴","wind":"北风3-4级","temperature":"2 ~ -5℃"}]
         */

        private String currentCity;
        private String pm25;
        private List<IndexBean> index;
        private List<WeatherDataBean> weather_data;

        public String getCurrentCity() {
            return currentCity;
        }

        public void setCurrentCity(String currentCity) {
            this.currentCity = currentCity;
        }

        public String getPm25() {
            return pm25;
        }

        public void setPm25(String pm25) {
            this.pm25 = pm25;
        }

        public List<IndexBean> getIndex() {
            return index;
        }

        public void setIndex(List<IndexBean> index) {
            this.index = index;
        }

        public List<WeatherDataBean> getWeather_data() {
            return weather_data;
        }

        public void setWeather_data(List<WeatherDataBean> weather_data) {
            this.weather_data = weather_data;
        }

        public static class IndexBean {
            /**
             * title : 穿衣
             * zs : 冷
             * tipt : 穿衣指数
             * des : 天气冷，建议着棉服、羽绒服、皮夹克加羊毛衫等冬季服装。年老体弱者宜着厚棉衣、冬大衣或厚羽绒服。
             */

            private String title;
            private String zs;
            private String tipt;
            private String des;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getZs() {
                return zs;
            }

            public void setZs(String zs) {
                this.zs = zs;
            }

            public String getTipt() {
                return tipt;
            }

            public void setTipt(String tipt) {
                this.tipt = tipt;
            }

            public String getDes() {
                return des;
            }

            public void setDes(String des) {
                this.des = des;
            }
        }

        public static class WeatherDataBean {
            /**
             * date : 周四 01月05日 (实时：2℃)
             * dayPictureUrl : http://api.map.baidu.com/images/weather/day/xiaoxue.png
             * nightPictureUrl : http://api.map.baidu.com/images/weather/night/mai.png
             * weather : 小雪转霾
             * wind : 北风微风
             * temperature : 2 ~ -4℃
             */

            private String date;
            private String dayPictureUrl;
            private String nightPictureUrl;
            private String weather;
            private String wind;
            private String temperature;

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getDayPictureUrl() {
                return dayPictureUrl;
            }

            public void setDayPictureUrl(String dayPictureUrl) {
                this.dayPictureUrl = dayPictureUrl;
            }

            public String getNightPictureUrl() {
                return nightPictureUrl;
            }

            public void setNightPictureUrl(String nightPictureUrl) {
                this.nightPictureUrl = nightPictureUrl;
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
        }
    }

    public static BaiduForecastJson get(String json) {
        if (null != json) {
//            return new Gson().fromJson(json, new TypeToken<BaiduForecastJson>() {
//            }.getType());
           try {
               BaiduForecastJson mBaiduForecastJson = JSON.parseObject(json, BaiduForecastJson.class);

               return mBaiduForecastJson;
           }catch (Exception e){

           }
        }
        return null;

    }
}
