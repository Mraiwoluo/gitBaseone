package com.pvetec.weather.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by zeu on 2017/1/10.
 * 为远程数据库提供支持
 */

public class RemoteProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        if ((uri.getPath()).contains("local")) {

                try {
                    //直接获取数据库第一条数据
                    cursor = ForecastProvider.getInstance(getContext()).getAllForecastCursor();
                }catch (Exception e){

                }

        } else if (uri.getPath().startsWith("/city")) {
            /*if (null != local) {
                cursor = ForecastProvider.getInstance(getContext()).getForecastCursor(local);
            }*/
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
