package com.pvetec.weather.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pvetec.weather.R;
import com.pvetec.weather.control.forecast.IForecastView;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;
import com.pvetec.weather.provider.ForecastProvider;
import com.pvetec.weather.utils.LitePalUtils;
import com.pvetec.weather.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/6/23.
 */
public class ForecastView extends Fragment implements IForecastView, View.OnClickListener{
    private static final String TAG=ForecastView.class.getSimpleName();

    CityInfo mCityInfo;
    ForecastJson mForecastJson;

    TextView mCityTextView;
    View mContentView;
    ImageView imageViewLocation;
    ImageView forecatImageViewBackground;
    SparseArray<View> mTodayForecastViews = new SparseArray<>();
    List<SparseArray<View>> mForecastViews = new ArrayList<>();

    String mLocationCity="";

    private static ForecastView mForecastView;

    public ForecastView() {}
//    public ForecastView(CityInfo info) {
//        setCityInfo(info);
//    }

    public static ForecastView getInstace(){
        if(mForecastView==null){
            mForecastView=new ForecastView();
        }
        return mForecastView;
    }


    @Override
    public void onClick(View v) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return createView(R.layout.pvt_forecast);
    }

    public View createView(int id) {
        View contentView = null;
        Activity context = getActivity();
        if (null != context) {
            LayoutInflater inflater = context.getLayoutInflater();
            if (null != inflater) {
                contentView = inflater.inflate(id, null);
                if (null != contentView) {
                    initView(contentView);
                }
            }
        }
        return contentView;
    }

    public void initView(View contentView) {
        mContentView = contentView;

        View forecastToday = contentView.findViewById(R.id.forecast_today);
        mTodayForecastViews.put(R.id.forecast, (forecastToday).findViewById(R.id.forecast));
        mTodayForecastViews.put(R.id.temper, (forecastToday).findViewById(R.id.temper));
        mTodayForecastViews.put(R.id.weather, (forecastToday).findViewById(R.id.weather));
        mTodayForecastViews.put(R.id.date, (forecastToday).findViewById(R.id.date));
        mTodayForecastViews.put(R.id.wind, (forecastToday).findViewById(R.id.wind));

        mCityTextView = (TextView) contentView.findViewById(R.id.city);
        imageViewLocation= (ImageView) contentView.findViewById(R.id.location);
        forecatImageViewBackground= (ImageView) contentView.findViewById(R.id.forecast_background);
        Object viewGroup = contentView.findViewById(R.id.forecast_layout);
        if (viewGroup instanceof LinearLayout) {
            int count = ((LinearLayout)viewGroup).getChildCount();
            for (int i = 0; i < count; i++) {
                Object obj = ((LinearLayout)viewGroup).getChildAt(i);
                if (obj instanceof LinearLayout) {
                    SparseArray<View> views = new SparseArray<>();
                    views.put(R.id.forecast, ((View)obj).findViewById(R.id.forecast));
                    views.put(R.id.temper_range, ((View)obj).findViewById(R.id.temper_range));
                    views.put(R.id.weather, ((View)obj).findViewById(R.id.weather));
                    views.put(R.id.date, ((View)obj).findViewById(R.id.date));
                    views.put(R.id.wind, ((View)obj).findViewById(R.id.wind));
                    mForecastViews.add(views);
                }
            }
        }

        mLocationCity=getActivity().getSharedPreferences("LocalCity", Context.MODE_PRIVATE).getString("mLocationCity","");

        if (null != mCityInfo && null != mCityTextView) {
            mCityTextView.setText(mCityInfo.getName());
            String cityName=mCityInfo.getName();
            LogUtils.i(TAG,"---mLocationCity---"+mLocationCity+"---cityName----"+cityName);
            //判断是否是定位城市,显示定位图标
            if (mLocationCity.contains(cityName)){
                imageViewLocation.setVisibility(View.VISIBLE);
            }else{
                imageViewLocation.setVisibility(View.GONE);
            }
        }
        updateForcast();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 初始化数据从数据库读取，，网络很慢的情况下刷新不出来
        if (null!=mCityInfo) {
            LogUtils.e(TAG,"onResume  ----init weather"+mCityInfo.getName());
            List<Object> results = ForecastProvider.getInstance(getActivity()).getForecastForCityName(mCityInfo.getName());
            if (results.size()!=0) {
                updateForcast((ForecastJson)results.get(1));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContentView = null;
        mForecastViews.clear();
    }

    public void setCityInfo(CityInfo info) {
        if (null != info) {
            mCityInfo = info;
            if (null != mCityInfo && null != mCityTextView) {
                mCityTextView.setText(mCityInfo.getName());
            }
        }
    }

    public CityInfo getCityInfo() {
        return mCityInfo;
    }

    @Override
    public void updateForcast(ForecastJson json) {
        if (null != json) {
            mForecastJson = json;
            updateForcast();
        }
    }

    public void updateForcastView(SparseArray<View> views, ForecastJson.ForecastBean forecastBean, boolean firstDay) {
        if (null != views && null != forecastBean) {
            ForecastEnum forecastEnum = ForecastEnum.get(forecastBean.getWeather());
            if (null != forecastEnum) {
                ImageView image = (ImageView) views.get(R.id.forecast);
                if (null != image) {
                    image.setBackgroundResource(forecastEnum.resid_icon);
                }
                if (firstDay) {
                    ForecastEnum forecastEnumTemper = ForecastEnum.get(forecastBean.getWeather(),forecastBean.getTemperature());//实时温度设置背景图片
                    // mContentView.setBackgroundResource(forecastEnum.resid_bg);//背景图片设置
                    if(null!=getActivity()) {
                        Glide.with(getActivity()).load(forecastEnumTemper.resid_bg).into(forecatImageViewBackground);
                    }
                }
            }else{
                LogUtils.e(TAG,"forecatImageViewBackground-----NULL----");
            }
            TextView weather = (TextView) views.get(R.id.weather);
            if (null != weather) {
                weather.setText(forecastBean.getWeather());
            }
            TextView temper = (TextView) views.get(R.id.temper);//实时温度
            if (null != temper) {
                temper.setText(forecastBean.getTemperature()+"℃");
            }
            TextView temperRange = (TextView) views.get(R.id.temper_range);
            if (null != temperRange) {
                temperRange.setText(forecastBean.getTemperarray());
            }
            TextView date = (TextView) views.get(R.id.date);
            if (null != date) {
                date.setText(forecastBean.getDate());
            }
            TextView wind = (TextView) views.get(R.id.wind);
            if (null != wind) {
                wind.setText(forecastBean.getWind());
                //LogUtils.i(TAG,"---set wind--"+forecastBean.getWind());
            }
        }
    }

    public void updateForcast() {
        if (null != mForecastJson && null != mContentView) {
            mForecastJson=LitePalUtils.getInstall(getActivity()).getForecastJson(mCityInfo.getName());
            if(mForecastJson==null) return;
            List<ForecastJson.ForecastBean> forecastBeans = mForecastJson.getForecasts();
            if(forecastBeans==null)return;
            if(forecastBeans.size()==0)return;
            for (int i = 0; i < forecastBeans.size() && i < mForecastViews.size(); i++) {
                ForecastJson.ForecastBean forecastBean = forecastBeans.get(i);
                SparseArray<View> views = mForecastViews.get(i);
                if (0 == i) {
                    updateForcastView(mTodayForecastViews, forecastBean, true);
                    updateForcastView(views, forecastBean, true);
                } else {
                    updateForcastView(views, forecastBean, false);
                }
            }
        }else{
            //没有数据的时候，背景为黑色，在这里设置默认背景
            if(null!=forecatImageViewBackground) {
                if(null!=getActivity()) {
                    Glide.with(getActivity()).load(R.drawable.bcloud).into(forecatImageViewBackground);
                }
            }
        }
    }
}
