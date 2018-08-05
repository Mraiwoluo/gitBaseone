package com.pvetec.weather.module;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.pvetec.weather.R;
import com.pvetec.weather.model.cityadder.CityInfo;
import com.pvetec.weather.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeu on 2016/12/30.
 */

public class CitySearcher {
    public static final String CITY_PROVINCE  = "province";       //城市省份
    public static final String CITY_NAME  = "name";                //城市名
    public static final String CITY_ID       = "cityid";           //城市代码

    public static final String TABLENAME="city";
    private Context mContext;

    String mDatabasePath;
    SQLiteDatabase mSQLiteDatabase;
    public CitySearcher(Context context) {
        this.mContext = context;
        if (null != context) {
            //mDatabasePath = Environment.getExternalStorageDirectory().getPath() + File.separator + context.getPackageName() +File.separator + "world.db";
            mDatabasePath="/data/data/com.pvetec.weather/databases/city.db";
            mSQLiteDatabase = openRawDatabase(context, mDatabasePath, TABLENAME);
        }
    }

    static CitySearcher sCitySearcher;
    public static CitySearcher getInstance(Context context) {
        if (null == sCitySearcher) {
            sCitySearcher = new CitySearcher(context);
        }
        return sCitySearcher;
    }

    /**
     * 判断表是否存在
     * @param db
     * @param table
     * @return
     */
    private static boolean isTableExists(SQLiteDatabase db, String table) {
        boolean ret = false;
        if (null != db && null != table) {
            Cursor cursor = db.rawQuery("select name from sqlite_master where name = \""+table+"\"", null);
            if (cursor.moveToNext()) {
                ret = true;
            }
            cursor.close();
        }
        return ret;
    }

    /**
     * 将raw类型的数据库移动到指定路径下面
     * @param path
     * @param deleteExist
     * @return
     */
    public String moveRawDatabaseToPath(Context context, String path, boolean deleteExist) {
        if (null != context && null != path) {
            File file = new File(path);
            try {
                if (file.exists()) {
                    if (deleteExist) {
                        file.delete();
                        file.createNewFile();
                    } else {
                        return path;
                    }
                } else {
                    String parent = file.getParent();
                    if (null != parent && !parent.equals("") && !parent.equals("/")) {
                        File pfile = new File(parent);
                        if (!pfile.exists() && !pfile.mkdirs()) {
                            return null;
                        }
                    }
                    file.createNewFile();
                }

                InputStream is = context.getResources().openRawResource(R.raw.city);
                FileOutputStream fos = new FileOutputStream(path);
                int remain;
                byte[] buffer = new byte[1024 * 10];
                while ((remain = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, remain);
                }
                fos.close();
                is.close();
                return path;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 打开数据库
     * @param context
     * @param dbName
     * @param tableName
     * @return
     */
    private SQLiteDatabase openRawDatabase(Context context, String dbName, String tableName) {
        //1.数据库是存放目录为/data/data/PackageName(com.*.*)/*.db,这个需要root权限,因此一般写在sdcard/PackageName(com.*.*)/*.db下面
        //2.将.db文件放到项目源码的res/raw目录下；
        //3.用InputStream读取原数据；
        //4.用FileOutPutStream把读取到的数据写入目录
        if (null == context) {
            System.out.print("请输入非空的上下文Context");
            return null;
        }
        if (null == dbName) {
            System.out.print("请输入非空有效的文件名");
        } else if (!dbName.matches("^(/?[\\w.]+/)*\\w+.db$")) {
            System.out.print("请输入非空有效的文件, 如*.db");
        } else {
            String path = moveRawDatabaseToPath(context, dbName, true);
            if (null != path) {
                File parentFile = new File(path).getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
                if ((null == tableName) || isTableExists(db, tableName)) {
                    return db;
                }
            }
        }
        return null;
    }

    /**
     * 搜索城市,得到一个城市信息的Bundle
     * @param city
     * @return
     */
    public List<CityInfo> search(String city) {
        List<CityInfo> results = new ArrayList<>();
        if (null != city && null != mSQLiteDatabase) {
            StringBuffer sb = new StringBuffer();
            sb.append("select * from city where ");
            sb.append("name like \"" + city + "%\" ");
            String is = sb.toString();
            Cursor cursor = mSQLiteDatabase.rawQuery(is, null);
            LogUtils.e("number ="+cursor.getCount());

            if (null != cursor) {
                try {
                    while (cursor.moveToNext()) {
                        CityInfo searchInfo = new CityInfo(cursor);
                        results.add(searchInfo);
                    }
                } catch (Exception e){
                    cursor.close();
                }
                finally {
                    cursor.close();
                }
            }
        }
        return results;
    }

    public CityInfo get(String city) {
        if(TextUtils.isEmpty(city)) return null;
        List<CityInfo> list = search(city);
        return (null != list && !list.isEmpty()) ? list.get(0) : null;
    }
}
