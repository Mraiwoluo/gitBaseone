package com.pvetec.weather.control.cityadder;

import android.content.Context;
import android.widget.Toast;

import com.pvetec.weather.R;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;
import com.pvetec.weather.module.CitySearcher;
import com.pvetec.weather.provider.ForecastProvider;
import com.pvetec.weather.provider.LocationProvider;
import com.pvetec.weather.utils.LogUtils;
import com.pvetec.weather.utils.NetWorkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zeu on 2016/12/30.
 */

public class CityAdderLogic implements ICityAdderLogic {
    Context mContext;
    ICityAdderView mView;
    List<CityInfo> mHotCitys;   //推荐的热门城市
    List<CityInfo> mAddedCitys; //已经添加的城市
    CitySearcher mCitySearcher;
    List<CityAddedListener> mCityAddedListeners;
    private static CityAdderLogic mCityAdderLogic;

    public static CityAdderLogic  getInstance(Context context){
        if(mCityAdderLogic==null){
            mCityAdderLogic=new CityAdderLogic(context);
        }
        return mCityAdderLogic;
    }

    private  CityAdderLogic (Context context) {
        mContext = context;
        mHotCitys = new ArrayList<>();
        mAddedCitys = new ArrayList<>();
        mCitySearcher = new CitySearcher(context);
        mCityAddedListeners = new ArrayList<>();
        if (null != context) {
            //获取推荐城市列表,并将城市信息转换成CityInfo,然后更新
            String[] hotCitys = context.getResources().getStringArray(R.array.hot_citys);
            if (null != hotCitys) {
                for (String name : hotCitys) {
                    CityInfo info = mCitySearcher.get(name);
                    if (null != info) {
                        mHotCitys.add(info);
                    }
                }
            }

            //添加本地城市
            addCity(LocationProvider.getInstance(context).getLocationCity());
            Map<CityInfo, ForecastJson> result = ForecastProvider.getInstance(context).getAllCity();
            for (CityInfo info : result.keySet()) {
                if (null != info) {
                    addCity(info);
                }
            }

            //加载默认城市
           if(mContext!=null) LocationProvider.getInstance(mContext).requestNormalLocationCity();
        }


    }

    public void updateAllWeatherCityInfo(){
        if(mContext==null) return;
        final Map<CityInfo, ForecastJson> result = ForecastProvider.getInstance(mContext).getAllCity();
        if(result==null) return;
        ForecastProvider.getInstance(mContext).updateAllWeather(result);
//        for (CityInfo info : result.keySet()) {
//              if (null != info) {
//                 //开始请求天气
//                 ForecastProvider.getInstance(mContext).requestForecast(info);
//              }
//        }

    }

    @Override
    public void init(ICityAdderView view) {
        if (null != view) {
            mView = view;
            mView.updateLocationState(0);
            mView.updateHotCitys(mHotCitys);
            mView.updateLocationCity(null);
        }
    }

    @Override
    public void addCity(CityInfo info) {
        if (null != info && info.getName() != null) {
            //判断是不是已经添加了的城市
            synchronized (mAddedCitys) {
                for (CityInfo city : mAddedCitys) {
                    if (null != city && city.equals(info)) {
                        if (null != mView) {
                            LogUtils.e("m_tag", "add city is add---" + city.getName());
                            //监听在添加界面，主要检测是否添加了重复的城市
                            mView.onAddCityStatus(info,false);
                        }
                        return;
                    }else if(!NetWorkUtils.isNetworkConnected(mContext)||(NetWorkUtils.getConnectedType(mContext)==-1)){
                        Toast.makeText(mContext,mContext.getResources().getString(R.string.city_state),Toast.LENGTH_SHORT).show();
                    }
                }
                mAddedCitys.add(info);
            }
            //通告,有新的城市添加
            for (CityAddedListener listener : mCityAddedListeners) {
                listener.onCityAdded(mAddedCitys, info);
            }

            //new add shan  通知首页该城市新添加
            if (null != cityNewAddListener) {
                cityNewAddListener.onCityNewAdd(info);
            }

            //开始请求天气
            ForecastProvider.getInstance(mContext).requestForecast(info);
        }
    }

    @Override
    public void addNewCity(CityInfo info) {

        if (null != info && info.getName() != null) {
            //判断是不是已经添加了的城市
            synchronized (mAddedCitys) {
                for (CityInfo city : mAddedCitys) {
                    if (null != city && city.equals(info)) {
                        if (null != mView) {
                            LogUtils.e("CityAdderView", "add city is add---" + city.getName());
                            //监听在添加界面，主要检测是否添加了重复的城市
                            mView.onAddCityStatus(info,false);
                        }
                        return;
                    }else if(!NetWorkUtils.isNetworkConnected(mContext)||(NetWorkUtils.getConnectedType(mContext)==-1)){
                        Toast.makeText(mContext,mContext.getResources().getString(R.string.city_state),Toast.LENGTH_SHORT).show();
                    }
                }
                //mAddedCitys.add(info); 改方法移至添加成功后在添加

            }

            //new add shan  通知首页该城市新添加
            if (null != cityNewAddListener) {
                cityNewAddListener.onCityNewAdd(info);
            }
            LogUtils.e("CityAdderView", "add new city---" + info.getName());
            //开始请求天气
            ForecastProvider.getInstance(mContext).requestNewAddCityForecast(info);
        }

    }

    @Override
    public List<CityInfo> getAddedCitys() {
        return mAddedCitys;
    }

    @Override
    public void removeAddedCitys(CityInfo info) {
        if (info != null) {
            //判断是不是已经添加了的城市
            synchronized (mAddedCitys) {
                for (CityInfo city : mAddedCitys) {
                    if (null != city && city.equals(info)) {
                        mAddedCitys.remove(city);
                        //数据库删除
                        ForecastProvider.getInstance(mContext).delectCity(info);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void removeCitys(CityInfo info) {
        if (info != null) {
            if (null != info) {
                mAddedCitys.remove(info);
                //数据库删除
                ForecastProvider.getInstance(mContext).delectCity(info);
                return;
            }
        }
    }

    @Override
    public void searchCity(String city) {
        if (null != mView) {
            List<CityInfo> citys = mCitySearcher.search(city);
            mView.updateCitySearchResult(citys);
        }
    }

    @Override
    public boolean isCityAdded(Object cityInfo) {
        if (cityInfo != null) {
            String cityName = (String) cityInfo;
            //判断是不是已经添加了的城市
            synchronized (mAddedCitys) {
                for (CityInfo city : mAddedCitys) {
                    if (null != city && city.equals(cityName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void enableLocation(boolean enable) {

    }

    public void addCityAddedListener(CityAddedListener listener) {
        if (null != listener && !mCityAddedListeners.contains(listener)) {
            mCityAddedListeners.add(listener);
            listener.onCityAdded(mAddedCitys, null);
        }
    }

    public interface CityAddedListener {
        void onCityAdded(List<CityInfo> addedCitys, CityInfo info);
    }


    public CityNewAddListener getCityNewAddListener() {
        return cityNewAddListener;
    }

    public void setCityNewAddListener(CityNewAddListener cityNewAddListener) {
        this.cityNewAddListener = cityNewAddListener;
    }

    public CityNewAddListener cityNewAddListener;

    public interface CityNewAddListener {
        void onCityNewAdd(CityInfo info);
    }


    public OnAddCityStatus getOnAddCityStatus() {
        return onAddCityStatus;
    }

    public void setOnAddCityStatus(OnAddCityStatus onAddCityStatus) {
        this.onAddCityStatus = onAddCityStatus;
    }

    public OnAddCityStatus onAddCityStatus;

    public interface OnAddCityStatus {
        void onAddStatus(boolean status);
    }


}
