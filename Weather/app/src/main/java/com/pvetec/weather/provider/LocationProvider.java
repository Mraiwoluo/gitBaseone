package com.pvetec.weather.provider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.pvetec.weather.control.weather.WeatherLogic;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.module.LocationService;
import com.pvetec.weather.utils.LogUtils;
import com.pvetec.weather.view.widget.WeatherWidgetProvider;

/**
 * Created by zeyu on 2017/1/9.
 */

public class LocationProvider {
    private String TAG="LocationProvider";
    Context mContext;
    LocationService locationService; //定位服务
    SharedPreferences mSharedPreferences;
    private LatLonPoint latLonPoint;
    private GeocodeSearch geocoderSearch;

    public LocationProvider(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("LocalCity", Context.MODE_PRIVATE);

        // -----------location config ------------
        locationService = new LocationService(context);
        geocoderSearch = new GeocodeSearch(context);

        locationService.registerListener(locationListener);

    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                Log.e(TAG,"location.getErrorCode()---->"+location.getErrorCode());
                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    Log.e(TAG,"location.getLocationType()---->"+location.getLocationType());
                    if(location.getLocationType()==AMapLocation.LOCATION_TYPE_GPS||location.getLocationType()==AMapLocation.LOCATION_TYPE_WIFI||
                            location.getLocationType()==AMapLocation.LOCATION_TYPE_CELL||location.getLocationType()==AMapLocation.LOCATION_TYPE_SAME_REQ||
                            location.getLocationType()==AMapLocation.LOCATION_TYPE_FIX_CACHE||location.getLocationType()==AMapLocation.LOCATION_TYPE_LAST_LOCATION_CACHE||
                            location.getLocationType()==AMapLocation.LOCATION_TYPE_OFFLINE||location.getLocationType()==AMapLocation.LOCATION_TYPE_SAME_REQ) {
                        OnLocationResult(location);
                        LogUtils.e(TAG,"location ="+location);
                    }
                    sendBrocast(WeatherWidgetProvider.WIDGET_UPDATE_WEATHER);
                } else {
                    //定位失败
                    OnLocationResultFial();
                    sendBrocast(WeatherWidgetProvider.WIDGET_UPDATE_WEATHER);
                }

            } else {
                OnLocationResultFial();
            }
        }
    };

    public void sendBrocast(String action){
        try{
            if (mContext==null) return;
            Intent intentWidget=new Intent();
            intentWidget.setAction(action);
            mContext.sendBroadcast(intentWidget);
        }catch (Exception e){

        }
    }

    private void OnLocationResult(AMapLocation bdLocation){
        String city = bdLocation.getCity();
        String province=bdLocation.getProvince();
        if(province!=null&&province.contains("台湾")){
            //强制台湾
            city="台湾";
        }
        LogUtils.e(TAG,"city ="+city);
        if (TextUtils.isEmpty(city)){
            //获取经度、纬度
            double longitude = bdLocation.getLongitude();
            double latitude = bdLocation.getLatitude();
            LogUtils.e(TAG,"longitude ="+longitude+"latitude ="+latitude);
            latLonPoint = new LatLonPoint(latitude,longitude);
            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                    GeocodeSearch.AMAP);
            geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
            geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                @Override
                public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                    LogUtils.e(TAG,"rCode ="+rCode+"  result= "+result);
                    if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                        if (result != null&& result.getRegeocodeAddress() != null) {
                            String city = result.getRegeocodeAddress().getCity();
                            LogUtils.e(TAG,"geocoderSearch ="+city +"cityCode ="+result.getRegeocodeAddress().getCityCode());
                            cityName(city);
                        }
                    }
                }

                @Override
                public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                }
            });
        }else{
            cityName(city);
        }
    }


    private void cityName(String city){
        String cityName = city;
        //拿到定位的城市直接请求数据
        ForecastProvider.getInstance(mContext).requestForecastLocation(cityName);
        if (cityName.equals("香港特別行政區")){
            cityName = "香港";
        }
        if (cityName.equals("澳門特別行政區")){
            cityName = "澳门";
        }
        if (cityName.equals("湘西土家族苗族自治州")){
            cityName = "湘西";
        }
        if (cityName.equals("延边朝鲜族自治州")){
            cityName = "延边";
        }
        if (cityName.equals("恩施土家族苗族自治州")){
            cityName = "恩施";
        }
        if (cityName.equals("阿坝藏族羌族自治州")){
            cityName = "阿坝州";
        }
        if (cityName.equals("甘孜藏族自治州")){
            cityName = "甘孜州";
        }
        if (cityName.equals("凉山彝族自治州")){
            cityName = "凉山州";
        }
        if (cityName.equals("红河哈尼族彝族自治州")){
            cityName = "红河州";
        }
        if (cityName.equals("西双版纳傣族自治州")){
            cityName = "西双版纳";
        }
        if (cityName.equals("怒江傈僳族自治州")){
            cityName = "怒江州";
        }
        if (cityName.equals("德宏傣族景颇族自治州")){
            cityName = "德宏州";
        }
        if (cityName.equals("甘南藏族自治州")){
            cityName = "甘南州";
        }
        if (cityName.equals("黄南藏族自治州")){
            cityName = "黄南州";
        }
        if (cityName.equals("海北藏族自治州")){
            cityName = "海北州";
        }
        if (cityName.equals("海南藏族自治州")){
            cityName = "海南州";
        }
        if (cityName.equals("玉树藏族自治州")){
            cityName = "玉树";
        }
        if (cityName.equals("果洛藏族自治州")){
            cityName = "果洛州";
        }
        if (cityName.equals("海西蒙古族藏族自治州")){
            cityName = "海西州";
        }
        if (cityName.equals("伊犁哈萨克自治州")){
            cityName = "伊犁州";
        }
        if (cityName.equals("昌吉回族自治州")){
            cityName = "昌吉";
        }
        if (cityName.equals("博尔塔拉蒙古自治州")){
            cityName = "博尔塔拉";
        }
        if (cityName.equals("巴音郭楞蒙古自治州")){
            cityName = "巴音郭楞";
        }
        if (cityName.equals("克孜勒苏柯尔克孜自治州")){
            cityName = "克孜勒苏柯尔克孜";
        }
        if (cityName.equals("黔南布依族苗族自治州")){
            cityName = "黔南";
        }
        if (cityName.equals("黔东南苗族侗族自治州")){
            cityName = "黔东南";
        }
        if (cityName.equals("黔西南布依族苗族自治州")){
            cityName = "黔西南";
        }
        //保存定位的城市 带有城市单位 xx市--by shan
        saveLocationCity(cityName);
        LogUtils.e(TAG,"locationcity----"+"---"+cityName);
    }

    private void OnLocationResultFial(){
        WeatherLogic.getInstance(mContext).onLocationResult(false);
        LogUtils.e(TAG,"locationcity---null-");
    }

    static LocationProvider sLocationProvider;
    public static LocationProvider getInstance(Context context) {
        if (null == sLocationProvider) {
            sLocationProvider = new LocationProvider(context);
        }
        return sLocationProvider;
    }

    public void requestLocationCity() {
        if(null!=locationService){
            locationService.start();
        }
    }

    public void start(){
        if(null!=locationService){
            locationService.start();
        }
    }

    public void stop(){
        if(null!=locationService){
            locationService.stop();
        }
    }

    public  void unregisterListener(){
        if(null!=locationService){
            locationService.unregisterListener(locationListener);
        }
    }

    public  void registerListener(){
        if(null!=locationService){
            locationService.registerListener(locationListener);
        }
    }

    public void requestNormalLocationCity() {
        CityInfo cityInfo = getLocationCity();
        if (null != cityInfo) {
            //开始请求天气
            ForecastProvider.getInstance(mContext).requestForecast(cityInfo);
            LogUtils.e(TAG,"requestNormalLocationCity default---");
        }
    }

    public CityInfo getLocationCity() {
        CityInfo cityInfo = null;
        String mLocationCity=mSharedPreferences.getString("mLocationCity","");
        LogUtils.e(TAG,"  mLocationCity="+mLocationCity);
        if(TextUtils.isEmpty(mLocationCity)){
            //设置默认城市
            saveLocationCity("深圳市");
            mLocationCity="深圳市";
            LogUtils.e(TAG,"set location default---");
        }
        //数据库中没有xx市保存的格式
        String cityTemp=mLocationCity;
        if(mContext!=null)cityInfo=new CityInfo(cityTemp);
        return cityInfo;
    }

    public void saveLocationCity(String city) {//by shan
        if (null != city) {
            mSharedPreferences.edit().putString("mLocationCity", city).commit();
        }
    }

    public String getSpLocationCity(){
        String city="";
        if(mSharedPreferences!=null){
            city=mSharedPreferences.getString("mLocationCity","");
        }
        return city;
    }

}
