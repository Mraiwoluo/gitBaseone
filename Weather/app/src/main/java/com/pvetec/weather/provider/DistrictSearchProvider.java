package com.pvetec.weather.provider;

import android.content.Context;
import android.text.TextUtils;

import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;

/**
 * Created by Administrator on 2017/10/25 0025.
 * 高德地图获取adcode 操作
 */

public class DistrictSearchProvider {

    private String TAG="DistrictSearchProvider";

    private static DistrictSearchProvider mDistrictSearchProvider;

    private Context mContext;

    private OnDistrictSearchResultListener mOnDistrictSearchResultListener;

    private DistrictSearchProvider(Context mContext){
        this.mContext=mContext;
    }

    public static DistrictSearchProvider getInstance(Context mContext){
        if(mDistrictSearchProvider==null){
            synchronized (DistrictSearchProvider.class){
                if(mDistrictSearchProvider==null){
                    mDistrictSearchProvider=new DistrictSearchProvider(mContext);
                }
            }
        }
        return mDistrictSearchProvider;
    }

    public void doDistrictSearchQuery(String cityName){
        if(mContext==null|| TextUtils.isEmpty(cityName)) return ;

        DistrictSearch search = new DistrictSearch(mContext);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(cityName);//传入关键字
        query.setShowBoundary(true);//是否返回边界值
        search.setQuery(query);
        search.setOnDistrictSearchListener(mOnDistrictSearchListener);//绑定监听器
        search.searchDistrictAnsy();//开始搜索


    }

    DistrictSearch.OnDistrictSearchListener mOnDistrictSearchListener=new DistrictSearch.OnDistrictSearchListener(){
        @Override
        public void onDistrictSearched(DistrictResult districtResult) {
            //在回调函数中解析districtResult获取行政区划信息
            //在districtResult.getAMapException().getErrorCode()=1000时调用districtResult.getDistrict()方法
            //获取查询行政区的结果，详细信息可以参考DistrictItem类。


            if(mOnDistrictSearchResultListener!=null) mOnDistrictSearchResultListener.OnDistrictSearchResult(districtResult);
        }
    };

   public DistrictSearchProvider setOnDistrictSearchResultListener(OnDistrictSearchResultListener mOnDistrictSearchResultListener){
       this.mOnDistrictSearchResultListener=mOnDistrictSearchResultListener;
       return mDistrictSearchProvider;
   }

    public interface OnDistrictSearchResultListener{
        void OnDistrictSearchResult(DistrictResult districtResult);
    }

}
