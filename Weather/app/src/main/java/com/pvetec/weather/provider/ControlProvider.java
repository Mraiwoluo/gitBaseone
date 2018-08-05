package com.pvetec.weather.provider;

import android.content.Context;

import com.pvetec.weather.control.weather.IWeatherView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/14 0014.
 */

public class ControlProvider {

    private Context mContext;

    private static ControlProvider mControlProvider;

    private List<IWeatherView> mIWeatherViewList=new ArrayList<>();


    private ControlProvider(Context mContext){
        this.mContext=mContext;
    }

    public static ControlProvider getInstall(Context mContext){
        if(mControlProvider==null){
            mControlProvider=new ControlProvider(mContext);
        }
        return mControlProvider;
    }

    //通知主界面更新fragment
    public void notifyIWeatherViewCityList(){
        if(mIWeatherViewList!=null){
            for (IWeatherView mIWeatherView:mIWeatherViewList) {
                 if(mIWeatherView!=null) mIWeatherView.updateCityList();
            }
        }
    }

    //通知主界面更新fragment
    public void notifyIWeatherViewLocationChange(){
        if(mIWeatherViewList!=null){
            for (IWeatherView mIWeatherView:mIWeatherViewList) {
                if(mIWeatherView!=null) mIWeatherView.locationChange();
            }
        }
    }

    public void registerIWeatherView(IWeatherView mIWeatherView){
        if(null!=mIWeatherView&&mIWeatherViewList!=null){
            mIWeatherViewList.add(mIWeatherView);
        }
    }

    public void unRegisterIWeatherView(IWeatherView mIWeatherView){
        if(null!=mIWeatherView&&mIWeatherViewList!=null){
            try{
                mIWeatherViewList.remove(mIWeatherView);
            }catch (Exception e){

            }
        }
    }

}
