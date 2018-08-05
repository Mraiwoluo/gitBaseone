package com.pvetec.weather.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pvetec.weather.R;
import com.pvetec.weather.control.citymanager.CityManagerLogic;
import com.pvetec.weather.control.citymanager.ICityManagerView;
import com.pvetec.weather.control.weather.WeatherLogic;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;
import com.pvetec.weather.provider.ControlProvider;
import com.pvetec.weather.provider.ForecastProvider;
import com.pvetec.weather.provider.LocationProvider;
import com.pvetec.weather.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zeu on 2017/1/3.
 */

public class CityManagerView extends Activity implements ICityManagerView, View.OnClickListener {

    private static final String TAG=CityManagerView.class.getSimpleName();

    private static final int ITEM_CLICK=0x001;
    private static final int CLICK_FINSH=0x002;
    private static final int ITEM_CLICK_ADD=0x003;

    TextView mTextViewTitle;
    GridView mGridView;
    CridAdapter mCridAdapter;
    CityManagerLogic mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pvt_city_manager);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.layout_back).setOnClickListener(this);
        mTextViewTitle= (TextView) findViewById(R.id.textview_title);
        mGridView = (GridView) findViewById(R.id.citys);
        mCridAdapter = new CridAdapter(this);
        mGridView.setAdapter(mCridAdapter);
        mGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mLogic != null) {
                            mLogic.performShortClick(null);
                        }
                        break;
                }
                return false;
            }
        });
        mCridAdapter.setItemClickListener(new CridAdapter.ItemClickListener() {
            @Override
            public void onClick(boolean longClick, CityInfo cityInfo) {
                if (mLogic != null) {
                    if (longClick) {
                        mLogic.performLongClick();
                    } else if (null != cityInfo){
                        final CityInfo cityInfoTemp=cityInfo;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Message msg=handler.obtainMessage(ITEM_CLICK);
                                msg.sendToTarget();
                                mLogic.performShortClick(cityInfoTemp);
                            }
                        },5);
                        //if(null!=mCridAdapter) mCridAdapter.notifyDataSetChanged();
                       // mLogic.performShortClick(cityInfo);
                    }else if(null==cityInfo){
                        //添加点击
                        if(countCityList()) {
                            Toast.makeText(CityManagerView.this,getResources().getString(R.string.city_mix_count),Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Message msg=handler.obtainMessage(ITEM_CLICK);
                                    msg.sendToTarget();
                                }
                            },5);
                        }else {
                            startCityAdderActivity();
                        }
                    }
                }
            }

            //删除城市
            @Override
            public void onDelete(CityInfo info) {
                if (mLogic != null && null != info) {
                    mLogic.deleteCity(info);
                    ControlProvider.getInstall(CityManagerView.this).notifyIWeatherViewCityList();
                    WeatherLogic.getInstance(CityManagerView.this).initWeather();
                }
            }
        });

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ITEM_CLICK:
                    if(null!=mCridAdapter) mCridAdapter.notifyDataSetChanged();
                    break;
                case CLICK_FINSH:
                    setTitleTextColor(getResources().getColor(R.color.white));
                    CityManagerView.this.finish();
                    break;
                case ITEM_CLICK_ADD:

                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        mLogic=CityManagerLogic.getInstance(CityManagerView.this);
        mLogic.setView(this)
        ;
        //重新加载数据
        initCityList();
    }

    private void initCityList() {
        //数据库中加载
        Map<CityInfo, ForecastJson> result = ForecastProvider.getInstance(CityManagerView.this).getAllCity();
        List<CityInfo> citys = new ArrayList<CityInfo>();

        if (result != null) {
            for (CityInfo info : result.keySet()) {
                if (null != info) {
                    citys.add(info);
                }
            }
        }
        //处理天气列表
        if(citys!=null&&citys.size()>=1) {
            //获取定位城市的position
            int position = 0;
            String location = LocationProvider.getInstance(this).getSpLocationCity();
            LogUtils.e(TAG,"   locationcity="+location);
            for (int i = 0; i < citys.size(); i++) {
                CityInfo info = citys.get(i);
                if (info != null) {
                    if (location.contains(info.getName()) || location.equals(info.getName())) {
                        position = i;
                        LogUtils.e(TAG,"   position="+position);
                        break;
                    }
                }
            }
            if (position != 0 && position < citys.size()) {
                //移动位置
                Collections.swap(citys, 0, position);
            }

        }

        //适配器刷新
        mCridAdapter.clear();
        mCridAdapter.addAll(citys);
        mCridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                if(countCityList()) {
                    Toast.makeText(CityManagerView.this,getResources().getString(R.string.city_mix_count),Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Message msg=handler.obtainMessage(ITEM_CLICK);
                            msg.sendToTarget();
                        }
                    },5);
                }else {
                    startCityAdderActivity();
                }
                break;
            case R.id.back:
                setTitleTextColor(getResources().getColor(R.color.black));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Message msg=handler.obtainMessage(CLICK_FINSH);
                        msg.sendToTarget();
                    }
                },5);

                break;
            case R.id.layout_back:
                setTitleTextSelect();
                finish();
                break;
        }
    }

    /***
     * 设置字体点击效果
     */
    private void setTitleTextSelect(){
       if(null!=mTextViewTitle){

           mTextViewTitle.setPressed(true);
           mTextViewTitle.setPressed(false);
       }
    }

    private void setTitleTextColor(int color){
        if(null!=mTextViewTitle){
            mTextViewTitle.setTextColor(color);
        }
    }


    private boolean countCityList(){
        boolean isMixCity=false;
        if(null!=mCridAdapter){
            int count=mCridAdapter.getCount();
            if(count>=7){
                isMixCity=true;
            }
        }
        return isMixCity;
    }

    public void startCityAdderActivity() {
        startActivity(new Intent(this, CityAdderView.class));
    }

    //删除城市，
    @Override
    public void updateCityView(List<CityInfo> citys) {
        if (null != citys && !citys.isEmpty())
            ControlProvider.getInstall(CityManagerView.this).notifyIWeatherViewCityList();
            WeatherLogic.getInstance(CityManagerView.this).initWeather();
            initCityList();
    }

    @Override
    public void showDeleteIcon(boolean deleteMode) {
        if (mCridAdapter.setDeleteMode(deleteMode)) {
            mCridAdapter.notifyDataSetChanged();
        }
    }

    //返回主界面
    @Override
    public void gotoSpecialForcast(CityInfo info) {
        if (null != info) {
            WeatherLogic.getInstance(CityManagerView.this).updateSelectedItem(info);
            finish();
            startActivity(new Intent(this, WeatherView.class).putExtra("selected", info));
        }
    }

    @Override
    public void updateLocalCity(CityInfo info) {
        if (mCridAdapter.setLocalCity(info)) {
            mCridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void updateForecast(CityInfo info, ForecastJson json) {
        if (mCridAdapter.updateForecast(info, json)) {
            mCridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void finishView() {
        CityManagerView.this.finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    static class CridAdapter extends BaseAdapter implements View.OnClickListener, View.OnLongClickListener{
        boolean mDeleteMode = false;
        CityInfo mLocalCity = null;
        LayoutInflater mInflater;
        ItemClickListener mItemClickListener;
        LinkedList<ItemInfo> mItems = new LinkedList<>();
        SharedPreferences mSharedPreferences=null;
        String mLocationCity="";
        Context mContext;

        public CridAdapter(Context context) {
            mDeleteMode = false;
            mLocalCity = null;
            mContext=context;
            if (null != context) {
                mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mSharedPreferences = context.getSharedPreferences("LocalCity", Context.MODE_PRIVATE);
                mLocationCity=mSharedPreferences.getString("mLocationCity","");
            }
        }

        @Override
        public int getCount() {
            return mItems.size()+1;
        }

        @Override
        public Object getItem(int position) {
            try {
                return mItems.get(position);
            }catch (Exception e){
                return new ItemInfo();
            }

        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object info = getItem(position);
            if (info instanceof ItemInfo) {
                ItemInfo item = (ItemInfo)info;
                if (null == convertView) {
                    convertView = mInflater.inflate(R.layout.pvt_city_manager_item, null);
                    if (null != convertView) {
                        convertView.setOnClickListener(this);
                        convertView.setOnLongClickListener(this);
                        //convertView.setBackgroundResource(R.drawable.city_manager_item); //点击效果在onClick 中实现

                        View view = convertView.findViewById(R.id.delete);
                        if (null != view) {
                            convertView.setTag(R.id.delete, view);
                            view.setOnClickListener(this);
                        }
                        view = convertView.findViewById(R.id.city);
                        if (null != view) convertView.setTag(R.id.city, view);
                        view = convertView.findViewById(R.id.weather);
                        if (null != view) convertView.setTag(R.id.weather, view);
                        view = convertView.findViewById(R.id.temper_range);
                        if (null != view) convertView.setTag(R.id.temper_range, view);
                        view = convertView.findViewById(R.id.temper_low);
                        if (null != view) convertView.setTag(R.id.temper_low, view);
                        view = convertView.findViewById(R.id.temper_high);
                        if (null != view) convertView.setTag(R.id.temper_high, view);
                        view=convertView.findViewById(R.id.layout_background);
                        if (null != view) convertView.setTag(R.id.layout_background, view);
                    }
                }

                if (null != convertView&&item!=null) {
                    convertView.setTag(info);
                    if (null != item.city) {
                        TextView cityView = (TextView) convertView.getTag(R.id.city);
                        cityView.setVisibility(View.VISIBLE);
                        if (null != cityView) cityView.setText(item.city.getName());
                    }
                    /*View layoutBackground = (View) convertView.getTag(R.id.layout_background);
                    if (null != layoutBackground) {
                        layoutBackground.setBackgroundResource(R.drawable.city_add_weather);
                    }*/

                    View deleteBtn = (View) convertView.getTag(R.id.delete);
                    if (null != deleteBtn) {
                        deleteBtn.setTag(info);
                        if (mDeleteMode&&item.city!=null) {
                            //new add by shan , 地图返回城市名有“xx市”
                            String city=item.city.getName();
//                            if (!city.contains("市")){//有些热门城市不知道哪里来的市
//                                city=city+"市";
//                            }
                            if(mLocationCity.equals(city)||position==0||getCount()==(position+1)){
                                //定位城市不允许删除,或者第一位
                                deleteBtn.setVisibility(View.GONE);
                            }else {
                                deleteBtn.setVisibility(View.VISIBLE);
                            }
                        } else {
                            deleteBtn.setVisibility(View.GONE);
                        }
                    }

                    if (null != item.forecast) {
                        List<ForecastJson.ForecastBean> beans = item.forecast.getForecasts();
                        if (null != beans && !beans.isEmpty()) {
                            //LogUtils.e("m_tag","set_imageviewweather1"+(item.city.getName()==null?"":item.city.getName()));
                            ForecastJson.ForecastBean bean = beans.get(0);
                            if (null != bean) {
                                //LogUtils.e("m_tag","set_imageviewweather2"+(item.city.getName()==null?"":item.city.getName()));
                                ImageView weatherView = (ImageView) convertView.getTag(R.id.weather);
                                if (null != weatherView) {
                                    weatherView.setVisibility(View.VISIBLE);
                                    ForecastEnum forecastEnum = ForecastEnum.get(bean.getWeather());
                                    if (null != forecastEnum) {
                                        weatherView.setBackgroundResource(forecastEnum.resid_icon);
                                    }
                                }
                                //LogUtils.e(TAG,"temper_range----"+bean.getTemperarray());
                                TextView temper = (TextView) convertView.getTag(R.id.temper_range);
                                temper.setVisibility(View.VISIBLE);
                                if (null != temper) temper.setText(bean.getTemperarray());
                            }
                        }
                    }else{
                        //LogUtils.e(TAG,"--forecast  NULL----"+item.city.toString());
                    }

                    LogUtils.i(TAG,"gridview----+++++++++++++++++"+position);
                    //显示+号
                    if(getCount()==1||getCount()==(position+1)){

                        /*View layoutBackgroundadd = (View) convertView.getTag(R.id.layout_background);
                        if (null != layoutBackgroundadd) {
                            layoutBackgroundadd.setBackgroundResource(R.drawable.city_add_weather);
                        }*/
                        TextView cityViewT = (TextView) convertView.getTag(R.id.city);
                        if(cityViewT!=null) cityViewT.setVisibility(View.GONE);
                        ImageView weatherView = (ImageView) convertView.getTag(R.id.weather);
                        if (null != weatherView) {
                            weatherView.setBackgroundResource(R.drawable.gridview_add_item_icon_);
                        }
                        TextView temper = (TextView) convertView.getTag(R.id.temper_range);
                        if(temper!=null) temper.setVisibility(View.GONE);

                    }
                }
            }
            return convertView;
        }

        public CridAdapter add(CityInfo city,ForecastJson forecastJson) {
            if (null != city) {
                mItems.add(new ItemInfo(city,forecastJson));
                //LogUtils.e(TAG,"CridAdapter----add--"+city.getName()+"==="+mItems.size()+"---forecastJson---"+(forecastJson == null?"":forecastJson.toString()));
            }

            return this;
        }

        public CridAdapter addAll(List<CityInfo> citys) {
            if (null != citys) {
                for (CityInfo city : citys) {
                    //LogUtils.e(TAG," citylist------"+city.getName());
                    ForecastJson forecastJson=ForecastProvider.getInstance(mContext).getForecast(city);
                    if (forecastJson!=null) {
                        add(city,forecastJson);
                    }else{
                        //根据城市名称查询
                        List<Object> results =ForecastProvider.getInstance(mContext).getForecastForCityName(city.getName());
                        if (results.size()==2){
                            add((CityInfo)results.get(0),(ForecastJson)results.get(1));
                        }
                        //LogUtils.e(TAG," citylist----读取数据库天气null--"+city.toString());
                    }
                }
            }
            return this;
        }

        public int indexOf(CityInfo info) {
            return (null != info) ? mItems.indexOf(info) : -1;
        }

        public boolean updateForecast(CityInfo info, ForecastJson json) {
            ItemInfo item = get(info);
            if (null != item) {
                item.setForecast(json);
                return true;
            }
            return false;
        }

        public ItemInfo get(CityInfo info) {
            if (null != info) {
                for (ItemInfo item : mItems) {
                    if (null != item && item.equals(info)) {
                        return item;
                    }
                }
            }
            return null;
        }

        public boolean contains(CityInfo info) {
            return  (null != get(info));
        }

        public void clear() {
            mItems.clear();
        }

        public boolean setLocalCity(CityInfo info) {
            if (null != info) {
                if (!info.equals(mLocalCity)) {
                    mLocalCity = info;
                    addToFirst(info);
                    return true;
                }
            }
            return false;
        }

        public boolean addToFirst(CityInfo info) {
            if (null != info) {
                synchronized (mItems) {
                    ItemInfo item = get(info);
                    if (null != item) {
                        mItems.remove(item);
                        mItems.addFirst(item);
                    } else {
                        //读取天气信息
                        List<Object> resultForecasJson = ForecastProvider.getInstance(mContext).getForecastForCityName(info.getName());
                        if (null!=resultForecasJson&&resultForecasJson.size()>0) {
                            mItems.addFirst(new ItemInfo((CityInfo) resultForecasJson.get(0),(ForecastJson) resultForecasJson.get(1)));
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        public boolean setDeleteMode(boolean deleteMode) {
            if (mDeleteMode ^ deleteMode) {
                mDeleteMode = deleteMode;
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            Object obj = v.getTag();
            if (obj instanceof ItemInfo) {
                switch (v.getId()) {
                    case R.id.delete:
                        if (null != mItemClickListener) {
                            mItemClickListener.onDelete(((ItemInfo) obj).city);
                        }
                        break;
                    default:
                        if (null != mItemClickListener) {
                            //设置背景
                            try {
                                /*View background = (View) v.getTag(R.id.layout_background);
                                if (null != background) {
                                    background.setBackgroundResource(R.drawable.city_add_weather_pre);
                                }*/

                            mItemClickListener.onClick(false, ((ItemInfo) obj).city);

                            }catch (Exception e){

                            }
                        }
                        break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (null != v) {
                Object obj = v.getTag();
                if (obj instanceof ItemInfo) {
                    if (null != mItemClickListener) {
                        mItemClickListener.onClick(true, ((ItemInfo) obj).city);
                        return true;
                    }
                }
            }
            return false;
        }

        public void setItemClickListener(ItemClickListener listener) {
            mItemClickListener = listener;
        }

        public interface ItemClickListener {
            void onClick(boolean longClick, CityInfo cityInfo);
            void onDelete(CityInfo info);
        }

        public static class ItemInfo {
            CityInfo city;
            ForecastJson forecast;

            public ItemInfo() {
                this(null, null);
            }

            public ItemInfo(CityInfo city, ForecastJson forecast) {
                setCity(city);
                setForecast(forecast);
            }

            public void setForecast(ForecastJson forecast) {
                this.forecast = forecast;
            }

            public void setCity(CityInfo city) {
                this.city = city;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof CityInfo) {
                    return obj.equals(this.city);
                } else if (obj instanceof ItemInfo) {
                    CityInfo city = ((ItemInfo) obj).city;
                    return (null != city) ? city.equals(this.city) : false;
                }
                return false;
            }
        }
    }
}
