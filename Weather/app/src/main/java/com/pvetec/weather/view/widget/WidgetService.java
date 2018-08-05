package com.pvetec.weather.view.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.provider.ForecastProvider;
import com.pvetec.weather.provider.LocationProvider;
import com.pvetec.weather.utils.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/6/6 0006.
 * Widgwt 定时更新
 */

public class WidgetService extends Service{

    private String TAG="WidgetService";

    private static final int ALARM_DURATION  = 1000*60*60; // service 自启间隔
    private static final int UPDATE_DURATION = 1000*60*60;     // Widget 更新间隔
    private static final int UPDATE_MESSAGE  = 1000;
    private static final int UPDATE_WEATHER_CITY=1000*60*60;  //一个小时
    private static long UPDATE_WEATHER_TIME=0;//保存上次更新城市天气的时间

    private UpdateHandler updateHandler; // 更新 Widget 的 Handler

    private AlarmManager manager;

    private PendingIntent pendingIntent;

    private Timer mTimer = null;

    private TimerTask mTimerTask = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 每个 ALARM_DURATION 自启一次
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getBaseContext(), WidgetService.class);
        pendingIntent = PendingIntent.getService(getBaseContext(), 0,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.set(AlarmManager.RTC,
                SystemClock.elapsedRealtime() + ALARM_DURATION, pendingIntent);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        updateHandler = new UpdateHandler();
        Message message = updateHandler.obtainMessage();
        message.what = UPDATE_MESSAGE;
        updateHandler.sendMessageDelayed(message, UPDATE_DURATION);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAlarm();
        stopTimer();
        LogUtils.i(TAG,"-----onDestroy-----");
    }

    private void cancelAlarm(){
        try{
            if(manager!=null&&pendingIntent!=null){
                manager.cancel(pendingIntent);
            }
        }catch (Exception e){

        }
    }


    private void updateWidget() {
        // 更新 Widget
        sendUpdateWidget(WeatherWidgetProvider.WIDGET_UPDATE_TIME);
        long current=System.currentTimeMillis();
        if(current-UPDATE_WEATHER_TIME>=(UPDATE_WEATHER_CITY)){
            //sendUpdateWidget(WeatherWidgetProvider.WIDGET_UPDATE_WEATHER);
            if(UPDATE_WEATHER_TIME==0){
                //开启定时定位最新位置
                startTimer(WidgetService.this);
            }
            UPDATE_WEATHER_TIME=current;
        }

        // 发送下次更新的消息
        Message message = updateHandler.obtainMessage();
        message.what = UPDATE_MESSAGE;
        updateHandler.sendMessageDelayed(message, UPDATE_DURATION);
    }

    private void sendUpdateWidget(String action){
        try{

            Intent intentWidgetUpdate=new Intent();
            intentWidgetUpdate.setAction(action);
            WidgetService.this.sendBroadcast(intentWidgetUpdate);

        }catch (Exception e){

        }

    }


    protected final class UpdateHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_MESSAGE:
                    updateWidget();
                    break;
                default:
                    break;
            }
        }
    }

    /***
     * 启动定时器
     */
    private void startTimer(final Context context) {
        LogUtils.e(TAG, "  startTimer ");
        stopTimer();
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    //重新启动定位
                    if(context==null) return;
                    LogUtils.e(TAG, "  startTimer -----LocationCity--");
                    UpDataFroecastLocation(context);
                    LocationProvider.getInstance(context).stop();
                    LocationProvider.getInstance(context).unregisterListener();
                    LocationProvider.getInstance(context).registerListener();
                    LocationProvider.getInstance(context).requestLocationCity();
                }
            };
        }

        if (mTimer != null && mTimerTask != null) {
            mTimer.schedule(mTimerTask, 0, UPDATE_WEATHER_CITY);
        }

    }

    //更新第一个位置城市
    private void UpDataFroecastLocation(Context context){
        try{
            //获取数据库第一条记录
           CityInfo mCityInfo= ForecastProvider.getInstance(context).getCityInfoFirst(context);
            if(mCityInfo!=null){
                ForecastProvider.getInstance(context).requestForecast(mCityInfo);
                LogUtils.e(TAG, "  startTimer -----updatarequestLocation--");
            }
        }catch (Exception e){

        }
    }


    /***
     * 定时器的停止
     */
    private void stopTimer() {
        LogUtils.e(TAG, "  stopTimer ");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

    }



}
