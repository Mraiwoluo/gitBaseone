package com.pvetec.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pvetec.weather.WeatherService;
import com.pvetec.weather.view.widget.WidgetService;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class BootReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        //开启服务
        Intent intentStartService=new Intent(context, WeatherService.class);
        context.startService(intentStartService);

        //开启widget定时器
        context.startService(new Intent(context, WidgetService.class));
    }

}
