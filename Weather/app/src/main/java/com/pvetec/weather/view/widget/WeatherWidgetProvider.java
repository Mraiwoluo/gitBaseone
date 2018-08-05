package com.pvetec.weather.view.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.pvetec.weather.R;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;
import com.pvetec.weather.provider.ForecastProvider;
import com.pvetec.weather.utils.LogUtils;
import com.pvetec.weather.view.ForecastEnum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/6 0006.
 */

public class WeatherWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_UPDATE_TIME="com.pvetec.weather.widget.update.time";

    public static final String WIDGET_UPDATE_WEATHER="com.pvetec.weather.widget.update.weather";

    Calendar mCalendar;
    String[] mWeekNames;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        context.startService(new Intent(context, WidgetService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action=intent.getAction();
        if(TextUtils.isEmpty(action)) return;

        if(AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)){

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pvt_weather_widget_layout);
            updateUI(context, views,true);

            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            gm.updateAppWidget(new ComponentName(context, WeatherWidgetProvider.class), views);

        }else if(WIDGET_UPDATE_TIME.equals(action)){

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pvt_weather_widget_layout);
            updateUI(context, views,false);

            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            gm.updateAppWidget(new ComponentName(context, WeatherWidgetProvider.class), views);

        }else if(WIDGET_UPDATE_WEATHER.equals(action)){

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.pvt_weather_widget_layout);
            updateUI(context, views,true);

            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            gm.updateAppWidget(new ComponentName(context, WeatherWidgetProvider.class), views);

        }

    }

    private void updateUI(Context context,RemoteViews views,boolean updateForecast){

        Intent intentClick = new Intent();
        intentClick.setPackage("com.pvetec.weather");
        intentClick.setClassName("com.pvetec.weather", "com.pvetec.weather.view.WeatherView");
        views.setOnClickPendingIntent(R.id.temp_layout, PendingIntent.getActivity(context, 0, intentClick, 0));

        Intent intentWeatherClick = new Intent();
        intentWeatherClick.setPackage("com.pvetec.weather");
        intentWeatherClick.setClassName("com.pvetec.weather", "com.pvetec.weather.view.WeatherView");
        views.setOnClickPendingIntent(R.id.img_weather, PendingIntent.getActivity(context, 111, intentWeatherClick, 0));

        Intent intentTimeClick = new Intent();
        intentTimeClick.setPackage("com.android.settings");
        intentTimeClick.setClassName("com.android.settings", "com.android.settings.Settings$DateTimeSettingsActivity");
        views.setOnClickPendingIntent(R.id.layout_time, PendingIntent.getActivity(context, 110, intentTimeClick, PendingIntent.FLAG_CANCEL_CURRENT));

        //set weather
        refreshTime(context,views);

        if(updateForecast) refreshForecast(context,views);

    }

    private void refreshTime(Context context,RemoteViews views){
        if(views==null||context==null) return;
        try {
            mWeekNames = context.getResources().getStringArray(R.array.week);
            mCalendar = Calendar.getInstance(); // by shan 2017/040/06 修正日期获取错误
            if (null != views) {
                String timeFormat = getDateHourFormat(context);
                if (!TextUtils.isEmpty(timeFormat)) {
                    if (timeFormat.equals("kk:mm")) {
                        timeFormat = "hh:mm";
                    } else {
                        timeFormat = "HH:mm";
                    }
                }
                //LogUtils.i("weather_widget","---settime---"+getTimeShort(timeFormat));
                views.setTextViewText(R.id.time, getTimeShort(timeFormat));
            }

            if (null != mWeekNames && mWeekNames.length >= 7) {
                int week = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
                if (week < 0) week = 0;
                //mWeekView.setText(mWeekNames[week]);
                String date = getTimeShort("yyyy/MM/dd");
                String weekDay = mWeekNames[week];
                if (!TextUtils.isEmpty(weekDay)) {
                    views.setTextViewText(R.id.date, date + "   " + weekDay);
                    //LogUtils.i("weather_widget","---setdate---");
                }
            }
        }catch (Exception e){

        }

    }


    private void refreshForecast(Context context,RemoteViews views){
        ForecastJson mForecastJson=getForecastJson(context);
        if(mForecastJson==null||views==null) return;

        List<ForecastJson.ForecastBean> beans = mForecastJson.getForecasts();
        if(beans==null) return;
        if(beans.size()==0) return;

        ForecastJson.ForecastBean bean = beans.get(0);
        if(bean==null) return;
        ForecastEnum forecastEnum = ForecastEnum.get(bean.getWeather());
        if (null != forecastEnum) {
            views.setImageViewResource(R.id.img_weather,forecastEnum.resid_icon);
        }

        String temp=bean.getTemperature();
        if(!TextUtils.isEmpty(temp)){
            views.setTextViewText(R.id.temp,temp+"℃");
        }

        String cityInfo="";
        if(!TextUtils.isEmpty(mForecastJson.getCity())){
            cityInfo=mForecastJson.getCity();
        }

        if(!TextUtils.isEmpty(bean.getWeather())){
            cityInfo=cityInfo+" | "+bean.getWeather();
        }

        views.setTextViewText(R.id.weather,cityInfo);

    }

    private ForecastJson getForecastJson(Context context){
        ForecastJson mForecastJson=null;
        try{
            //数据库中加载
            Map<CityInfo, ForecastJson> result = ForecastProvider.getInstance(context).getAllCity();
            List<CityInfo> citys =new ArrayList<CityInfo>();

            if(result!=null){
                for (CityInfo info : result.keySet()) {
                    if (null != info) {
                        citys.add(info);
                    }
                }
                if(citys.size()!=0){
                    //比較是否第一個位置是不是定位城市
                    mForecastJson=ForecastProvider.getInstance(context).getForecast(citys.get(0));
                }
            }

        }catch (Exception e){

        }
        return mForecastJson;
    }

    /**
     * 获取时间 小时:分;秒 HH:mm(24 小时) hh:mm (12 小时)
     *
     * @return
     */
    public static String getTimeShort(String Format) {
        String dateString="";
        try {
            if(!TextUtils.isEmpty(Format)) {
                SimpleDateFormat formatter = new SimpleDateFormat(Format);
                Date currentTime = new Date();
                dateString = formatter.format(currentTime);
            }
        }catch (Exception e){
            return dateString;
        }
        return dateString;
    }

    //获取时间格式  12 小时或者24小时
    private String getDateHourFormat(Context context) {
        boolean is12 = getDateTimeFormat(context).equals("12");
        return is12 ? "kk:mm" : "h:mm";
    }

    public static String getDateTimeFormat(Context context) {
        String format = "";
        if (null != context) {
            format = Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24);//获取系统12 还是24 小时制
        }
        if (null == format) {
            format = "";
        }

        return format;
    }

    //
    private Typeface getCustomTypeface(Context context){
        Typeface typeface=null;
        try{
            if(context!=null){
                typeface= Typeface.createFromAsset(context.getAssets(),"HELVETICAINSERAT-ROMAN-SEMIB.TTF");
                LogUtils.i("CustomFontTextView","----getCustomTypeface---ok-");
            }
        }catch (Exception e){

        }
        return typeface;
    }


}
