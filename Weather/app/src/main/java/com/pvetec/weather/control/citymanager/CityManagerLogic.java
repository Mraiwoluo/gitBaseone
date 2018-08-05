package com.pvetec.weather.control.citymanager;

import android.content.Context;

import com.pvetec.weather.control.cityadder.CityAdderLogic;
import com.pvetec.weather.model.cityadder.CityInfo;

/**
 * Created by zeu on 2017/1/5.
 */

public class CityManagerLogic implements ICityManagerLogic {
    Context mContext;
    ICityManagerView mView;
    boolean mDeleteMode = false;
    CityAdderLogic mCityAdderLogic;
    static CityManagerLogic mCityManagerLogic;

    public static CityManagerLogic getInstance(Context context){
        if(mCityManagerLogic==null){
            mCityManagerLogic=new CityManagerLogic(context);
        }
        return mCityManagerLogic;
    }


    private  CityManagerLogic(Context context) {
        this.mContext=context;
        mCityAdderLogic=CityAdderLogic.getInstance(context);
    }

    public void setView(ICityManagerView mView){
        this.mView=mView;
    }

    @Override
    public void finishView() {
        if(mView!=null) mView.finishView();
    }

    @Override
    public void performLongClick() {
        //退出或进入删除模式
        mDeleteMode = !mDeleteMode;
        if (null != mView) {
            mView.showDeleteIcon(mDeleteMode);
        }
    }

    @Override
    public void performShortClick(CityInfo info) {
        if (mDeleteMode) {
            if (null != info) {
                mDeleteMode = !mDeleteMode;
                if (null != mView) {
                    mView.showDeleteIcon(mDeleteMode);
                }
            } else {
                mDeleteMode = false;
                if (null != mView) {
                    mView.showDeleteIcon(mDeleteMode);
                }
            }
        } else if (null != info){
            /*if (null != mWeatherLogic) {//会有闪动的问题
                mWeatherLogic.updateSelectedItem(info);
            }*/
            if (null != mView) {
                mView.gotoSpecialForcast(info);
            }
        }
    }

    @Override
    public void deleteCity(CityInfo info) {
        if (null != mCityAdderLogic && null != info) {
            //mCityAdderLogic.removeAddedCitys(info);
            mCityAdderLogic.removeCitys(info);
            if (null != mView) {
                mView.updateCityView(mCityAdderLogic.getAddedCitys());
            }
        }
    }
}
