package com.pvetec.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pvetec.common.bean.PageBean;
import com.pvetec.common.bean.ResultBean;

import org.json.JSONException;
import org.json.JSONObject;

public  abstract class ResultParaser<T>
{
    public ResultBean<T> mData=new ResultBean<T>();
    
    public ResultParaser<T> parse(String resultJson)
    {
        if(resultJson==null) return this;
        JSONObject Json=null;
        try {
            Json = new JSONObject(resultJson);
            int code=Json.getInt("code");
            if(code!=0) return this;
            String message=Json.getString("message");
            String jsonData=Json.getString("data");
            String jsonPage=Json.getString("page");
            PageBean page=null;
            if(jsonPage!=null)
            {
                Gson g = new Gson();
                page = g.fromJson(jsonPage, new TypeToken<PageBean>() {
                }.getType());
                
            }
            T data= parseData(jsonData);
            mData=new ResultBean<T>();
            mData.setPage(page);
            mData.setCode(code);
            mData.setMessage(message);
            mData.setData(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
    public abstract T parseData(String data);
    
    public T getData()
    {
        if(mData==null) return null;
        return mData.getData();
    }
    
    public ResultBean<T>  getResult()
    {
        return mData;
    }
    
}
