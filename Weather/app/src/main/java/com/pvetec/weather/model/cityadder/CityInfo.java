package com.pvetec.weather.model.cityadder;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by zeu on 2017/1/3.
 */

public class CityInfo implements Parcelable {

    public static final String CITY_PROVINCE  = "province"; //city province

    public static final String NAME     = "name";       //city location name

    public static final String CITY_ID    = "cityid";       //city code

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String mProvince) {
        this.mProvince = mProvince;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getCityId() {
        return mCityId;
    }

    public void setCityId(String mCityId) {
        this.mCityId = mCityId;
    }

    String mProvince;
    String mName;
    String mCityId;

    public CityInfo() {}

    public CityInfo(String name) {
        this.mName = name;
    }

    public CityInfo(Cursor bundle) {
        set(bundle);
    }


    public void set(Bundle bundle) {
        if (null != bundle) {
            mProvince=bundle.getString(CITY_PROVINCE);
            mName = bundle.getString(NAME);
            mCityId= bundle.getString(CITY_ID);
        }
    }

    public void set(Cursor cursor) {
        if (null != cursor) {
            mProvince = cursor.getString(cursor.getColumnIndex(CITY_PROVINCE));
            mName = cursor.getString(cursor.getColumnIndex(NAME));
            mCityId = cursor.getString(cursor.getColumnIndex(CITY_ID));
        }
    }

    /**
     * 已英文城市路径为标准,当英文城市路径相等,则两个变量相等,如果都为null,反悔false
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof String) {
            return obj.equals(CITY_ID);
        } else if (obj instanceof CityInfo) {
            String enpath = ((CityInfo)obj).getName();
            if (null != enpath) {
                return enpath.equals(mName);
            }
        }
        return true;
    }

    protected CityInfo(Parcel in) {
        mProvince = in.readString();
        mName = in.readString();
        mCityId= in.readString();
    }

    public static final Creator<CityInfo> CREATOR = new Creator<CityInfo>() {
        @Override
        public CityInfo createFromParcel(Parcel in) {
            return new CityInfo(in);
        }

        @Override
        public CityInfo[] newArray(int size) {
            return new CityInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mProvince);
        dest.writeString(mName);
        dest.writeString(mCityId);

    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("mProvince", mProvince);
            obj.put("mName", mName);
            obj.put("mCityId", mCityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static CityInfo fromJson(String json) {
        CityInfo info = null;
        if (null != json) {
//            Gson gson = new Gson();
//            info = gson.fromJson(json, new TypeToken<CityInfo>(){}.getType());
            try {
                info = JSON.parseObject(json, CityInfo.class);
            }catch (Exception e){

            }
        }
        return info;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
