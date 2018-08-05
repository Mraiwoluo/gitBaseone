package com.pvetec.weather.view;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pvetec.weather.R;
import com.pvetec.weather.WeatherService;
import com.pvetec.weather.control.cityadder.CityAdderLogic;
import com.pvetec.weather.control.weather.IWeatherView;
import com.pvetec.weather.control.weather.WeatherLogic;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;
import com.pvetec.weather.provider.ControlProvider;
import com.pvetec.weather.provider.LocationProvider;
import com.pvetec.weather.utils.LogUtils;
import com.pvetec.weather.utils.NetWorkUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

public class WeatherView extends FragmentActivity implements IWeatherView, View.OnClickListener {
    private static final String TAG = WeatherView.class.getSimpleName();

    WeatherLogic mLogic;
    ViewPager mViewPager;
    ImageView mUpdateButton;
    TextView mTextView;
    TextView mTextViewDefault;

    CircleIndicator indicator;
    WeatherAdapter mWeatherAdapter;
    CityInfo selectAddCityInfo;
    int indexViewPage = 0;

    Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pvt_weather_view);
        //设置默认城市
        LocationProvider.getInstance(WeatherView.this).requestNormalLocationCity();
        //Litepal 数据库天气更新
        CityAdderLogic.getInstance(WeatherView.this).updateAllWeatherCityInfo();
        LocationProvider.getInstance(getApplicationContext()).requestLocationCity();
        //每次启动请求定位
        startService(new Intent(this, WeatherService.class).setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES));

        initView();
        ControlProvider.getInstall(this).registerIWeatherView(this);
        mLogic = WeatherLogic.getInstance(WeatherView.this);
        mLogic.setView(this);
        mLogic.checkNewWorkState();

        Cursor cursor = getContentResolver().query(Uri.parse("content://com.zeu.weather/local"), null, null, null, null);
        if (null != cursor) {
            System.out.print("aaaaa");
        }
    }

    public void initView() {
        findViewById(R.id.btn_city_add).setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTextView = (TextView) findViewById(R.id.text_loading);
        mTextViewDefault = (TextView) findViewById(R.id.textview_default);
        mUpdateButton = (ImageView) findViewById(R.id.update);
        mUpdateButton.setOnClickListener(this);

        indicator = (CircleIndicator) findViewById(R.id.indicator);
        mWeatherAdapter = new WeatherAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mWeatherAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                indexViewPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        indicator.setViewPager(mViewPager);
        mWeatherAdapter.addAdapterListener(new WeatherAdapter.AdapterListener() {
            @Override
            public void onAdd(int index, CityInfo info) {

            }

            @Override
            public void onRemove(CityInfo info) {

            }

            @Override
            public void onClear() {

            }
        });

        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void netWorkError() {
        showTextViewDefault(getResources().getString(R.string.none_city_tips));
        WeatherLogic.getInstance(WeatherView.this).initWeather();
    }

    @Override
    public void initWeather(List<CityInfo> citys) {
        if (citys == null || citys.size() == 0) {

            return;
        }
        goneTextViewDefault();
        //适配器刷新
        mWeatherAdapter.clear();
        mWeatherAdapter.addAll(citys);
        mWeatherAdapter.notifyDataSetChanged();
        gotoSelecteItemPager();
        //  从home 返回，在进入，修复指示器没有选中状态的修复
        setIndicatorIndex();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WeatherLogic.getInstance(WeatherView.this).initWeather();
        LogUtils.e(TAG, "--onResume---");
    }

    private void setIndicatorIndex() {
        WeatherView.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (indicator != null && mViewPager != null) {
                    indicator.setViewPager(mViewPager);
                }
            }
        });

    }

    /***
     * 首次安装，并且无网络状态下，不会加载得到天气，默认显示背景
     */
    private void showTextViewDefault(String str) {
        if (null != mTextViewDefault) {
            mTextViewDefault.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(str)) {
                mTextViewDefault.setText(str);
            }
        }
    }

    private void goneTextViewDefault() {
        if (null != mTextViewDefault) {
            mTextViewDefault.setVisibility(View.GONE);
        }
    }

    private void upDateFragment() {
        try {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            List<Fragment> fragments = fm.getFragments();
            if (fragments != null && fragments.size() > 0) {
                for (int i = 0; i < fragments.size(); i++) {
                    if (null != fragments.get(i)) {
                        ft.remove(fragments.get(i));
                    }
                }
            }
            ft.commitAllowingStateLoss();
        } catch (Exception e) {

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        indexViewPage = mViewPager.getCurrentItem();

    }

    @Override
    public void onBackPressed() {
        WeatherView.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocationProvider.getInstance(this).unregisterListener();
        LocationProvider.getInstance(this).stop();
        Glide.get(this).clearMemory();
        ControlProvider.getInstall(this).unRegisterIWeatherView(this);
        if (mLogic != null) mLogic.uninitHandler();
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
        //全部销毁Fragment
        upDateFragment();
    }

    @Override
    public void onClick(View v) {
        if (!viewPagerShowCityWeatherInfo()) {
            return; //没有显示城市天气，点击无效果
        }
        switch (v.getId()) {
            case R.id.btn_city_add:
                startCityManagerActivity();
                break;

            case R.id.update:
                if (null != mLogic) {
                    try {
                        if (mWeatherAdapter.getCount() == 0) return;
                        Object obj = mWeatherAdapter.getItem(mViewPager.getCurrentItem());
                        if (obj instanceof ForecastView) {
                            CityInfo info = ((ForecastView) obj).getCityInfo();
                            if (null != info) {
                                mLogic.requstUpdateWeather(info);
                                showLoading();
                                if (!NetWorkUtils.isNetworkConnected(this) || (NetWorkUtils.getConnectedType(this) == -1)) {
                                    WeatherView.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showLoadingResult(getResources().getString(R.string.upadte_fail));
                                            goneLoading();
                                        }
                                    });
                                }
                            }
                        }
                    } catch (Exception e) {
                        return;
                    }
                }
                break;
        }
    }

    private boolean viewPagerShowCityWeatherInfo() {
        boolean show = false;
        if (mViewPager != null) {
            if (mViewPager.getChildCount() != 0) {
                show = true;
            }
        }
        return show;
    }

    public void startCityManagerActivity() {
        startActivity(new Intent(this, CityManagerView.class));
    }

    //会有移动的闪动问题,因此采用Intent的方式
    @Override
    public void updateSelectedItem(CityInfo info) {
        int index = mWeatherAdapter.getIndex(info);
        if (index >= 0 && index < mWeatherAdapter.getCount()) {
            //防止越界
            indexViewPage = index;
        }
        gotoSelecteItemPager();
    }

    @Override
    public void updateAddNewCity(CityInfo info) {
        selectAddCityInfo = info;
        if (mLogic != null) mLogic.initWeather();
    }

    //碎片界面更新数据
    @Override
    public void updateWeather(CityInfo info, final ForecastJson json) {
        //LogUtils.e(TAG,"updateWeather-----");
        if (null != info && null != json) {
            synchronized (mWeatherAdapter) {
                int index = mWeatherAdapter.getIndex(info);
                if (-1 != index) {
                    try {
                        final Object obj = mWeatherAdapter.getItem(index);
                        if (obj instanceof ForecastView) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ForecastView) obj).updateForcast(json);
                                }
                            });
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
    }

    @Override
    public void updateWeatherStatus(final boolean status) {
        WeatherView.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtils.e(TAG, "status =" + status);
                if (status) {
                    showLoadingResult(getResources().getString(R.string.upadte_success));
                } else {
                    showLoadingResult(getResources().getString(R.string.upadte_fail));
                }
                goneLoading();
            }
        });
    }

    @Override
    public void updateCityList() {
        LogUtils.e(TAG, "--------updateCityList---------");
        upDateFragment();
    }

    @Override
    public void locationChange() {
        updateCityList();
        WeatherLogic.getInstance(WeatherView.this).initWeather();
    }

    @Override
    public void updateDataBases() {
        WeatherView.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWeatherAdapter != null && mWeatherAdapter.getCount() == 0 && mLogic != null)
                    mLogic.initWeather();
            }
        });


    }

    private void showLoading() {
        if (mTextView != null) {
            mTextView.setText(getResources().getString(R.string.loading_weather));
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    private void showLoadingResult(String str) {
        if (mTextView != null && !TextUtils.isEmpty(str)) {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(str);
        }
    }

    private void goneLoading() {
        LogUtils.e(TAG, "mTextView =" + mTextView);
        if (mTextView != null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTextView.setVisibility(View.GONE);
                }
            }, 2 * 1000);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public void gotoSelecteItemPager() {

        try {

            //检测是否有新的城市添加
            if (null != selectAddCityInfo) {
                int index = mWeatherAdapter.getIndex(selectAddCityInfo);
                if (index >= 0 && index < mWeatherAdapter.getCount()) {
                    //防止越界
                    mViewPager.setCurrentItem(index);
                } else {
                    indexViewPage = 0;
                    mViewPager.setCurrentItem(indexViewPage);
                }
                selectAddCityInfo = null;
            } else {
                if (indexViewPage >= mWeatherAdapter.getCount()) {
                    //默认显示0
                    indexViewPage = 0;
                }
                mViewPager.setCurrentItem(indexViewPage);
            }

            indexViewPage = mViewPager.getCurrentItem();

        } catch (Exception e) {
        }


    }

    /**
     * Created by zeu on 2017/1/3.
     */

    public static class WeatherAdapter extends FragmentPagerAdapter {
        CityInfo mLocationCity;
        AdapterListener mAdapterListener;
        Map<String, ForecastView> mFragments = new LinkedHashMap<>();
        List<CityInfo> cityInfoList = new ArrayList<>();
        // 可以删除这段代码看看，数据源更新而viewpager不更新的情况
        private int mChildCount = 0;

        public WeatherAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Collection<ForecastView> views;
            synchronized (mFragments) {
                views = mFragments.values();
            }
            if (position < views.size()) {
                //此处如果返回为null,会crash,此处不对索引超越边界检测
                ForecastView view = views.toArray(new ForecastView[]{})[position];
                return view;
            } else {
                ForecastView viewCrash = new ForecastView();
                return viewCrash;
            }

        }

        @Override
        public Object instantiateItem(View container, int position) {
            ForecastView view = (ForecastView) super.instantiateItem(container, position);
            if (position < cityInfoList.size()) {
                view.setCityInfo(cityInfoList.get(position));
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            try {

            } catch (Exception e) {

            }
        }

        @Override
        public void notifyDataSetChanged() {
            try {
                // 重写这个方法，取到子Fragment的数量，用于下面的判断，以执行多少次刷新
                mChildCount = getCount();
                super.notifyDataSetChanged();
            } catch (Exception e) {

            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try {
                super.finishUpdate(container);
            } catch (Exception nullPointerException) {

            }
        }

        @Override
        public int getItemPosition(Object object) {
            if (mChildCount > 0) {
                // 这里利用判断执行若干次不缓存，刷新
                mChildCount--;
                // 返回这个是强制ViewPager不缓存，每次滑动都刷新视图
                return POSITION_NONE;
            }
            // 这个则是缓存不刷新视图
            return super.getItemPosition(object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            try {
                super.setPrimaryItem(container, position, object);
            } catch (Exception e) {
                // TODO: handle exception
                LogUtils.e(TAG, "setPrimaryItem-------" + e.toString());
            }
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        //往碎片界面赋值数据
        public boolean add(CityInfo info/*city path contains country and city*/) {
            synchronized (mFragments) {
                if (null != info && null != info.getName() && !mFragments.containsKey(info.getName())) {
                    //mFragments.put(info.getEnPath(), new ForecastView(info));
                    ForecastView forecastView = new ForecastView();
                    forecastView.setCityInfo(info);
                    mFragments.put(info.getName(), forecastView);
                    //LogUtils.i(TAG, "mFragments---添加数据" + info.getName());
                    if (null != mAdapterListener) {
                        mAdapterListener.onAdd(getCount(), info);
                    }
                    return true;
                }
            }
            return false;
        }

        public boolean addAll(List<CityInfo> infos) {
            boolean ret = false;
            if (null != infos && infos.size() > 0) {
                ret = true;
                for (CityInfo info : infos) {
                    cityInfoList.add(info);
                    if (!add(info)) {
                        ret = false;
                    }
                }
            }
            return ret;
        }

        public boolean remove(CityInfo info) {
            synchronized (mFragments) {
                if (null != info && null != info.getName() && mFragments.containsKey(info.getName())) {
                    mFragments.remove(info.getName());
                    if (null != mAdapterListener) {
                        mAdapterListener.onRemove(info);
                    }
                    return true;
                }
            }
            return false;
        }

        public void addAdapterListener(AdapterListener listener) {
            mAdapterListener = listener;
        }

        public interface AdapterListener {
            void onAdd(int index, CityInfo info);

            void onRemove(CityInfo info);

            void onClear();
        }

        public boolean contains(String cityEnPath) {
            return (null != cityEnPath) ? mFragments.containsKey(cityEnPath) : false;
        }

        public boolean contains(CityInfo info) {
            return (null != info) ? contains(info.getName()) : false;
        }

        public void clear() {
            mFragments.clear();
            cityInfoList.clear();
            if (null != mAdapterListener) {
                mAdapterListener.onClear();
            }
        }

        public int getIndex(CityInfo info) {
            if (null != info) {
                try {
                    synchronized (mFragments) {
                        ForecastView forecastView = mFragments.get(info.getName());
                        if (null != forecastView) {
                            return new ArrayList<>(mFragments.values()).indexOf(forecastView);
                        }
                    }
                } catch (Exception e) {

                }
            }
            return -1;
        }

        public int getIndex(String citypath) {
            if (null != citypath) {
                synchronized (mFragments) {
                    ForecastView forecastView = mFragments.get(citypath);
                    if (null != forecastView) {
                        return new ArrayList<>(mFragments.values()).indexOf(forecastView);
                    }
                }
            }
            return -1;
        }

        public boolean addToFirst(CityInfo info) {
            if (null != info) {
                synchronized (mFragments) {
                    Map<String, ForecastView> back = new LinkedHashMap<>(mFragments);
                    mFragments.clear();
                    if (contains(info)) {
                        ForecastView forecastView = back.get(info.getName());
                        back.remove(info.getName());
                        mFragments.put(info.getName(), forecastView);
                    } else {
                        ForecastView forecastViewTemp = new ForecastView();
                        forecastViewTemp.setCityInfo(info);
                        mFragments.put(info.getName(), forecastViewTemp);
                        //mFragments.put(info.getEnPath(), new ForecastView(info));
                    }
                    mFragments.putAll(back);
                    return true;
                }
            }
            return false;
        }
    }
}