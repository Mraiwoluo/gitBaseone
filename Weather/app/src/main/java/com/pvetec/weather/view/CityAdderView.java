package com.pvetec.weather.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.pvetec.weather.R;
import com.pvetec.weather.control.cityadder.CityAdderLogic;
import com.pvetec.weather.control.cityadder.ICityAdderLogic;
import com.pvetec.weather.control.cityadder.ICityAdderView;
import com.pvetec.weather.control.citymanager.CityManagerLogic;
import com.pvetec.weather.control.weather.WeatherLogic;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.model.forecast.ForecastJson;
import com.pvetec.weather.module.CitySearcher;
import com.pvetec.weather.provider.ForecastProvider;
import com.pvetec.weather.utils.LogUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by zeu on 2017/1/3.
 */

public class CityAdderView extends Activity implements ICityAdderView{
    EditText mEditText;
    LinearLayout mSearchLayout;
    GridView mGridView;
    ListView mSearchListView;
    CridAdapter mCridAdapter;
    PopupWindow mPopupWindow;
    SearchAdpter mSearchAdpter;
    ImageButton mSearchButton;
    ICityAdderLogic mLogic;
    Toast mToast;

    private static final String TAG=CityAdderView.class.getSimpleName();
    private static final int HANDLER_SUCCESS_ADD=0x001;
    private static final int HANDLER_FIAD_ADD=0x002;
    private static final int HANDLER_EXISTENCE_ADD=0x003;

    private long lastTouchGridViewItem=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pvt_city_adder);

        mGridView = (GridView) findViewById(R.id.citys);
        mCridAdapter = new CridAdapter(this);
        mGridView.setAdapter(mCridAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = mCridAdapter.getItem(position);
                if (obj instanceof CityInfo) {
                    if (null != mLogic) {
                        //mLogic.addCity((CityInfo)obj);
                        if(getCityCountMax()){
                            showToast(getResources().getString(R.string.city_mix_count));
                        }else {
                            if(lastTouchGridViewItem==0||(lastTouchGridViewItem-System.currentTimeMillis()>3*1000)) {
                                mLogic.addNewCity((CityInfo) obj);
                                lastTouchGridViewItem=System.currentTimeMillis();
                            }
                        }

                    }
                }
            }
        });

        mSearchButton = (ImageButton) findViewById(R.id.search);
        mSearchLayout= (LinearLayout) findViewById(R.id.search_layout);
        mSearchListView= (ListView) findViewById(R.id.listview_search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mLogic && null != mEditText) {
                    String city = mEditText.getText().toString();
                    if (city.equals("台湾")){
                        Toast.makeText(CityAdderView.this,getResources().getString(R.string.city_data),Toast.LENGTH_SHORT).show();
                    }else {
                        mLogic.searchCity(city);
                    }
                }
            }
        });

        mSearchAdpter = new SearchAdpter(this);
        mPopupWindow = new PopupWindow(getLayoutInflater().inflate(R.layout.search_result_list, null), dip2px(this, 800), ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = mPopupWindow.getContentView();
        mSearchListView.setAdapter(mSearchAdpter);
        if (view instanceof ListView) {
            ListView listView = (ListView)view;
            listView.setAdapter(mSearchAdpter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPopupWindow.dismiss();
                    Object obj = mSearchAdpter.getItem(position);
                    if (obj instanceof CityInfo) {
                        CityInfo info = (CityInfo) obj;
                        if(info==null) return;
                        mEditText.setText(info.getName());

                        //通知添加城市CityActivity.onCityMngModelEvent
                        if (null != mLogic) {
                            if(getCityCountMax()){
                                showToast(getResources().getString(R.string.city_mix_count));
                            }else {
                                //mLogic.addCity(info);
                                if(lastTouchGridViewItem==0||(lastTouchGridViewItem-System.currentTimeMillis()>3*1000)) {
                                    mLogic.addNewCity(info);
                                    lastTouchGridViewItem=System.currentTimeMillis();
                                }
                            }
                        }
                        //finish();
                    }
                }
            });
        }

        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object obj = mSearchAdpter.getItem(position);
                if (obj instanceof CityInfo) {
                    CityInfo info = (CityInfo) obj;
                    if(info==null) return;
                    mEditText.setText(info.getName());

                    //通知添加城市CityActivity.onCityMngModelEvent
                    if (null != mLogic) {
                        if(getCityCountMax()){
                            showToast(getResources().getString(R.string.city_mix_count));
                        }else {
                            //mLogic.addCity(info);
                            if(lastTouchGridViewItem==0||(lastTouchGridViewItem-System.currentTimeMillis()>3*1000)) {
                                mLogic.addNewCity(info);
                                lastTouchGridViewItem=System.currentTimeMillis();
                            }
                        }
                    }

                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = "北京市";
                String mDatabasePath = "/data/data/com.pvetec.weather/databases/city.db";
                final CityInfo mCityInfo= CitySearcher.getInstance(CityAdderView.this).get(name);
                LogUtils.e(TAG,"mCityInfo ="+mCityInfo);
                if (mCityInfo == null){
                    CitySearcher.getInstance(CityAdderView.this).moveRawDatabaseToPath(CityAdderView.this,mDatabasePath,true);
                }
            }
        }).start();
        mEditText = (EditText) findViewById(R.id.edit);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (null != mLogic) {
                        mLogic.searchCity(v.getText().toString());
                    }
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });
        mEditText.addTextChangedListener(watcher);
        //添加是否成功回调
        ForecastProvider.getInstance(CityAdderView.this).setRequestCallbackSaveDataBase(new ForecastProvider.RequestCallbackSaveDataBase() {
            @Override
            public boolean onResult(CityInfo mCityInfo,boolean sucess) {
                //添加是否成功回调
                Message msg=new Message();
                if (sucess){
                    msg.what=HANDLER_SUCCESS_ADD;
                    WeatherLogic.getInstance(CityAdderView.this).requstAddCity(mCityInfo);
                    CityManagerLogic.getInstance(CityAdderView.this).finishView();
                }else{
                    msg.what=HANDLER_FIAD_ADD;
                }
                handler.sendMessage(msg);

                return false;
            }
        });

        mLogic=CityAdderLogic.getInstance(CityAdderView.this);
        if(mLogic!=null) mLogic.init(this);
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

            if (null != mLogic && null != mEditText) {
                String cityString=mEditText.getText().toString();
                if(TextUtils.isEmpty(cityString)) {
                    if(mSearchListView!=null) mSearchListView.setVisibility(View.GONE);
                    return;
                }
                mLogic.searchCity(cityString);
            }

        }
    };

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_SUCCESS_ADD:
                    CityAdderView.this.finish();
                    LogUtils.e(TAG,"添加成功----");
                   break;
                case HANDLER_FIAD_ADD:

                    showToast(getResources().getString(R.string.toast_add_fail));
                    break;
                case HANDLER_EXISTENCE_ADD:

                    CityInfo info= (CityInfo) msg.obj;
                    if(info==null) return;
                    WeatherLogic.getInstance(CityAdderView.this).updateSelectedItem(info);
                    CityManagerLogic.getInstance(CityAdderView.this).finishView();
                    CityAdderView.this.finish();
                    break;
            }
        }
    };

    private boolean getCityCountMax(){
        boolean isMax=false;
        //数据库中加载
        Map<CityInfo, ForecastJson> result = ForecastProvider.getInstance(CityAdderView.this).getAllCity();
        if(null!=result){
            if(result.size()>=6){
                isMax=true;
            }
        }
        return isMax;
    }

    private void showToast(String str){
        cancelToast();
        mToast=Toast.makeText(CityAdderView.this,str,Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void cancelToast(){
        if (null!=mToast){
            mToast.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销回调
        ForecastProvider.getInstance(CityAdderView.this).setRequestCallbackSaveDataBase(null);
        cancelToast();
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void updateLocationState(int state) {

    }

    @Override
    public void updateLocationCity(CityInfo city) {

    }

    @Override
    public void onAddCityStatus(CityInfo city,boolean status) {
        if (!status){ //该城市已经存在
            Message msg=new Message();
            msg.what=HANDLER_EXISTENCE_ADD;
            msg.obj=city;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void updateCitySearchResult(List<CityInfo> citys) {
        mSearchAdpter.clear();
        mSearchAdpter.add(citys);
        mSearchAdpter.notifyDataSetChanged();
        if(mSearchListView!=null){
            if(citys==null) {
                mSearchListView.setVisibility(View.GONE);
                return;
            }else if(citys.size()==0){
                mSearchListView.setVisibility(View.GONE);
            }else{
                mSearchListView.setVisibility(View.VISIBLE);
            }

        }
        //mPopupWindow.showAsDropDown(mSearchLayout);

    }

    @Override
    public void updateHotCitys(List<CityInfo> citys) {
        mCridAdapter.clear();
        mCridAdapter.addAll(citys);
        mCridAdapter.notifyDataSetChanged();
    }

    static class CridAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        LinkedList<CityInfo> mCitys = new LinkedList<>();

        public CridAdapter(Context context) {
            if (null != context) {
                mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
        }

        @Override
        public int getCount() {
            return mCitys.size();
        }

        @Override
        public Object getItem(int position) {
            return mCitys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object item = getItem(position);
            if (item instanceof CityInfo) {
                CityInfo info = (CityInfo)item;
                if (null == convertView) {
                    convertView = mInflater.inflate(R.layout.pvt_city_adder_item, null);
                }
                if (null != convertView) {
                    ((TextView) convertView).setText(info.getName());
                }
            }
            return convertView;
        }

        public CridAdapter add(CityInfo city) {
            if (null != city && !mCitys.contains(city)) {
                mCitys.add(city);
            }
            return this;
        }

        public CridAdapter addAll(List<CityInfo> citys) {
            if (null != citys) {
                for (CityInfo city : citys) {
                    add(city);
                }
            }
            return this;
        }

        public void clear() {
            mCitys.clear();
        }
    }

    static class SearchAdpter extends BaseAdapter{
        Context mContext;
        ArrayList<CityInfo> mData = new ArrayList<>();

        public SearchAdpter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object obj = getItem(position);
            TextView textView = null;
            ViewHolder viewHolder=null;
            if (obj instanceof CityInfo) {
                CityInfo item = (CityInfo) obj;
                if (null == convertView) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.search_result_item,parent,false);
                    viewHolder=new ViewHolder();
                    viewHolder.mTextView= (TextView) view.findViewById(R.id.textview);
                    convertView = view;
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                if(viewHolder.mTextView!=null) {
                    viewHolder.mTextView.setText(item.getName());
                }
            }
            return convertView;
        }


        public SearchAdpter add(CityInfo city) {
            if (null != city && !mData.contains(city)) {
                mData.add(city);
            }
            return this;
        }

        public SearchAdpter add(List<CityInfo> data) {
            if (null != data) {
                for (CityInfo dat: data) {
                    add(dat);
                }
            }
            return this;
        }

        public SearchAdpter clear() {
            mData.clear();
            return this;
        }

        static class ViewHolder{
            public TextView mTextView;
        }

    }
}
