package com.pvetec.common.bean;

import com.google.gson.Gson;

/**
 * 
 * @author lcp 2016/1/15
 *
 */
public class ResultBean<T> {
	
	/**
	 * 返回码 0表示正确
	 */
	int code; 
	/**
	 * 返回错误信息
	 */
	String message;
	
	/**
	 * 返回信息数据
	 */
	T data;
	
	PageBean page;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public static <T> T parse(String resultJson, java.lang.reflect.Type type)
	{
	    if(resultJson==null) return null;
        Gson g=new Gson();
        ResultBean<T> rb= g.fromJson(resultJson,type);
        if(rb.getCode()!=0) return null;
        return rb.getData();
	}

}
