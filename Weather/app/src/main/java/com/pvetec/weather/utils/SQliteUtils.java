package com.pvetec.weather.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 数据库中 后面有AS 表示别名,如 SELECT COUNT(*) AS LoopCount
 * Created by admin on 2016/7/6.
 */
public class SQliteUtils {
    /**
     * 将多个字符串组合
     * @param names
     * @return
     * @eg 将name和code 组合成"name, code"
     */
    public static String makeNames(String... names) {
        String ret = "", name;
        for (int i = 0; i < names.length && (null != (name = names[i])); i++) {
            ret += (name + ((i < (names.length-1)) ? ", ": ""));
        }
        return ret;
    }

    public static String strCompose(String divider, String ...strs) {
        String ret = "";
        if (null != strs) {
            if (null == divider) divider = "";
            for (int i = 0; i < strs.length; i++) {
                String str = strs[i];
                if (null != str) {
                    if (!"".equals(ret)) {
                        ret += divider;
                    }
                    ret += str;
                }
            }
        }
        return ret;
    }

    /**
     * 将字符串分解
     * @param names
     * @return
     * @eg 将"name, code"  分解成, 组合成name和code
     */
    public static String[] sliptNames(String names) {
        //是否匹配类似于"xxx, www, yyy,"
        if (null != names && names.matches("^( *\\w+ *,)*( *\\w+ *)$")) {
            //移除空格和末尾的','
            return names.replaceAll("( +)|(, *$)", "").split(",");
        }
        return new String[0];
    }

    /**
     * 将操作组合成字符串
     * @param names
     * @return
     * @eg SQliteUtils.makeExpress("city, code", "=", "武汉", "888888") == "city = '武汉', code = '88888888'"
     */
    public static String makeCompound(String names, String operator, String... values) {
        String name, ret = "";
        if (null != values && 0 < values.length) {
            String[] slipts = sliptNames(names);
            if (null == operator) operator = "";
            if (slipts.length == values.length) {
                for (int i = 0; i < slipts.length && (null != (name = slipts[i])); i++) {
                    String value = values[i];
                    if (null == value) value = "''";
                    else if (!value.matches("^'\\w+'")){
                        value = value.replaceAll("'", "");
                        value = "'" + value + "'";
                    }
                    ret += (name + " " + operator + " " + value + ((i<(slipts.length-1))?", ": ""));
                }
            }
        }
        return ret;
    }

    /**
     * 创建或打开数据库
     * @param context
     * @param path 不指定, 则默认数据库存放路径为/data/data/YourPackageName[com.*.*]/*.db
     * @param mode
     * @return
     */
    public static SQLiteDatabase createDatabase(Context context, String path, int mode) throws SQLiteException {
        if (null != context && null != path && path.matches("^(/?[\\w.]+/)*\\w+.db$")) {
            try {
                File parentFile = new File(path).getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                SQLiteDatabase database = context.openOrCreateDatabase(path, mode, null);
                return database;
            } catch (SQLiteException e) {
                log("create database SQLiteException " + context, path);
                throw e;
            }
        } else log("create database faild " + context, path);
        return null;
    }

    /**
     * 执行数据库指令
     * @param db
     * @param sql
     * @return
     */
    public static boolean exec(SQLiteDatabase db, String sql) {
        if (null != db && null != sql) {
            try {
                int result=-1;
                log("exec ", sql);
                db.execSQL(sql);
                return true;
            } catch (SQLiteException e) {
                //e.printStackTrace();
                log("exec err " + e.toString());
            }
        }
        return false;
    }

    /**
     * 执行数据库指令
     * @param db
     * @param sql
     * @return
     */
    public static Cursor rawquery(SQLiteDatabase db, String sql) {
        Cursor cursor = null;
        if (null != db && null != sql) {
            try {
                log("rawquery ", sql);
                cursor = db.rawQuery(sql, null);
            } catch (SQLiteException e) {
                //e.printStackTrace();
                log("exec exception " + e.toString());
            }
        }
        return cursor;
    }

    /**
     * 判断表是否存在
     * @param table
     * @return
     */
    public static String makeIsTableExists(String table) {
        return "select name from sqlite_master where name=\""+table+"\"";
    }

    /**
     * 创建表
     * SQliteUtils.createTable(db, TABLE, "name text primary key not null unique", "code text", "extra text");
     * 注:
     * 1.autoincrement必须和integer一起使用,否则会报
     * AUTOINCREMENT is only allowed on an INTEGER PRIMARY KEY
     * 2.'unique' 建议放在最后
     * @param table 表名
     * @param columns 包含字段名称 类型, 是否唯一, 是否为空， 是否为主键
     *                 如"name text primary key not null unique"和"code text" "extra text"
     */
    public static String makeCreateTable(String table, String... columns) {
        if (null != table && null != columns && 0 < columns.length) {
            String column, order = "create table " + table + "(\n";
            for (int i = 0; i < columns.length; i++) {
                if (null != (column = columns[i])) {
                    order += (column + ((i < (columns.length - 1)) ? ",\n" : ");"));
                }
            }
            return order;
        }
        return null;
    }

    /**
     * 插入值
     * SQliteUtils.insert(mSql, TABLE, "name, code, extra", "zerain", "88888888", "ahah");
     * @param table
     * @param columns 段名字 如"name, code, extra"
     * @param values 值 如"'zerain', '88888888', 'ahah'"
     */
    public static String makeInsert(String table, String columns, String... values) {
        if (null != table && null != columns && null != values && 0 < values.length) {
            String value;
            String order = "insert into " + table + "\n" + "(" + columns + ")\n";
            order += "values(";
            for (int i = 0; i < values.length; i++) {
                value = (null != values[i]) ? values[i]: "";
                order +=  ("'" + value +"'");
                if (i < (values.length-1)) order += ", ";
            }
            order += ");";
            return order;
        }
        return null;
    }

    /**
     * 从数据库中删除行
     * eg
     *  1.删除所有code = 'zerain', name = '888'的行
     *      SQliteUtils.delete_nopress(db, TABLE, "code, name", "zerain", "888");
     * @param table
     * @param cdtcolumns 行条件
     * @param cdtvalues 条件值
     * @return
     */
    public static String makeDelete(String table, String cdtcolumns, String... cdtvalues) {
        if (null != table) {
            String order = "delete_nopress from "  + table;
            String condition = makeCompound(cdtcolumns, "=", cdtvalues);
            if (!condition.equals("")) {
                return (order + " where " + condition);
            }
        }
        return null;
    }

    /**
     * 更新值
     * 更新 name = zerain 所在行的code和extra的值为9999999,00000
     * SQliteUtils.update(db, TABLE, "name = 'zerain'", "code, extra", "9999999", "00000");
     * @param table
     * @param conditions "name = 'zerain'"
     * @param columns "code, extra"
     * @param values "9999999", "00000"
     */
    public static String makeUpdate(String table, String conditions, String columns, String... values) {
        if (null != table) {
            String[] names = sliptNames(columns);
            //判断参数名和参数是否相等
            if (names.length > 0 && names.length == values.length) {
                //组合设置指令
                String order = "update " + table + " set\n", value;
                for (int i = 0; i < values.length; i++) {
                    value = (null != values[i]) ? values[i]: "''";

                    String name = names[i];
                    if (null != name) {
                        name = name.replaceAll(" ", "");
                        if (name.matches("^\\w+$")) {
                            order += makeCompound(name, "=", value);
                            if (i < (values.length - 1)) {
                                order += ", ";
                            }
                        }
                    }
                }
                order +=  ((null != conditions) ? (" where " + conditions + ";") : ";");
                return order;
            }
        }
        return null;
    }

    /**
     * 更新值
     * @param table
     * @param cdtnames 条件名 "name, extra"
     * @param cdtvalues 条件值 new String[]{city, "aa"}, 与条件名拼凑的条件为"name='city', extra = 'a'"
     * @param columns 列名 "code, extra"
     * @param values 列值 "9999999", "00000"
     * @eg SQliteUtils.update(db, TABLE, "name, extra", new String[]{city, "aa"}, "code, extra", "9999999", "00000");
     */
    public static String makeUpdates(String table, String cdtnames, String[] cdtvalues, String columns, String... values) {
        if (null != table && null != cdtnames && null != cdtvalues) {
            String conditions = makeCompound(cdtnames, "=", cdtvalues);
            String[] names = sliptNames(columns);
            //判断参数名和参数是否相等
            if (names.length > 0 && names.length == values.length) {
                //组合设置指令
                String order = "update " + table + " set ", value;
                for (int i = 0; i < values.length; i++) {
                    value = (null != values[i]) ? values[i]: "''";

                    String name = names[i];
                    if (null != name) {
                        name = name.replaceAll(" ", "");
                        if (name.matches("^\\w+$")) {
                            order += makeCompound(name, "=", value);
                            if (i < (values.length - 1)) {
                                order += ", ";
                            }
                        }
                    }
                }
                order += (" where " + conditions + ";");
                return order;
            }
        }
        return null;
    }

    /**
     * 获取数据库, 获取所有code = 'zerain', name = '888'的行
     * SQliteUtils.get(db, TABLE, "code, extra", "code, name", "zerain", "888");
     * @param table
     * @param chooses 选中项, "code, extra"
     * @param extra 附加指令
     * @param cdtcolumns 选中项, "code, name"
     * @param cdtvalues 条件值"zerain", "888"
     * @return
     */
    public static String makeGetx(String table, String chooses, String extra, String cdtcolumns, String... cdtvalues) {
        if (null != table) {
            if (null == cdtvalues) cdtvalues = new String[0];
            if (null == chooses) chooses = "*";
            String order = "select " + chooses + " from "  + table;
            String[] names = sliptNames(cdtcolumns);
            if (0 < cdtvalues.length && cdtvalues.length == names.length) {
                order += " where ";
                for (int i = 0; i < cdtvalues.length; i++) {
                    String value = (null != cdtvalues[i]) ? cdtvalues[i]: "''";
                    order += makeCompound(names[i], "=", value);
                    if (i < (cdtvalues.length-1)) {
                        order += ",";
                    }
                }
            }
            order += (((null==extra)?(""):(" "+extra)) + ";");
            return order;
        }
        return null;
    }

    /**
     * 获取数据库
     * eg
     *  1.获取所有code = 'zerain', name = '888'的行
     *      SQliteUtils.get(db, TABLE, "code, extra", "code, name", "zerain", "888");
     *  2.获取表中的20数据, 并以dtime降序排列
     *      SQliteUtils.gets(mSql, TABLE, "*", "order by dtime desc limit 20", null, null)
     * @param table
     * @param chooses 选中项, "code, extra"
     * @param cdtcolumns 条件项"code, name"
     * @param cdtvalues 条件值"zerain", "888"
     * @return
     */
    public static String makeGets(String table, String chooses, String cdtcolumns, String... cdtvalues) {
        return makeGetx(table, chooses, null, cdtcolumns, cdtvalues);
    }

    /**
     * 获取数据行数
     * eg
     *  1.获取所有code = zerain, name = '888'的行数
     *      SQliteUtils.counter(db, TABLE, "code", "code, name", "zerain", "888");
     *      获取所有非code = zerain, name = '888'的行数
     *      SQliteUtils.counter(db, TABLE, "code", true, "code, name", "zerain", "888");
     *  2.获取总行数,
     *      SQliteUtils.counter(db, TABLE, "*", null); or SQliteUtils.counter(db, TABLE, null, null);
     * @param table
     * @param speciColumns 选中项, 如果为null 或者"*" 表示选取所有行, 一般用于获取总行
     * @param distinct 取反
     * @param cdtcolumns 条件项"code, name"
     * @param cdtvalues 条件值"zerain", "888"
     * @return
     */
    public static String makeCounter(String table, String speciColumns, boolean distinct, String cdtcolumns, String... cdtvalues) {
        if (null == speciColumns) speciColumns = "*";
        if (!speciColumns.equals("*") && distinct) speciColumns = ("DISTINCT " + speciColumns);
        return makeGetx(table, speciColumns, null, cdtcolumns, cdtvalues);
    }
    public static String makeCounter(String table, String speciColumns, String cdtcolumns, String... cdtvalues) {
        return makeCounter(table, speciColumns, false, cdtcolumns, cdtvalues);
    }


    public static boolean isInsertExpress(String sql) {
        boolean is = false;
        if (null != sql) {
            is = sql.matches("^ *insert +into(.|\\n)*$");
        }
        return is;
    }

    public static boolean isDeleteExpress(String sql) {
        return (null != sql) ? sql.matches("^ *delete +from(.|\\n)*$") : false;
    }

    public static boolean isUpdateExpress(String sql) {
        return (null != sql) ? sql.matches("^ *update +(\\*|\\w+) +set(.|\\n)*$") : false;
    }

    public static boolean isGetExpress(String sql) {
        return (null != sql) ? sql.matches("^ *select +(\\w+|\\*) +from(.|\\n)*$") : false;
    }

    /**
     * 创建表
     * @param db
     * @param table 表名
     * @param columns 包含字段名称 类型, 是否唯一, 是否为空， 是否为主键
     *                 如"name text primary key not null unique"和"code text" "extra text"
     * @eg SQliteUtils.createTable(db, TABLE, "name text primary key not null unique", "code text", "extra text");
     */
    public static boolean createTable(SQLiteDatabase db, String table, String... columns) {
        return exec(db, makeCreateTable(table, columns));
    }

    /**
     * 插入值
     * SQliteUtils.insert(mSql, TABLE, "name, code, extra", "zerain", "88888888", "ahah");
     * @param db
     * @param table
     * @param columns 段名字 如"name, code, extra"
     * @param values 值 如"'zerain', '88888888', 'ahah'"
     */
    public static boolean insert(SQLiteDatabase db, String table, String columns, String... values) {
        return exec(db, makeInsert(table, columns, values));
    }

    public static boolean insert(SQLiteDatabase db, String table, String city,String forecast){
        boolean resule=false;
        if(!TextUtils.isEmpty(city)&&!TextUtils.isEmpty(forecast)){
            ContentValues values=new ContentValues();
            values.put("city",city);
            values.put("weather",forecast);
            long l=db.insert(table,null,values);
            if(l!=-1){
                resule=true;
            }
        }
        return resule;
    }

    /**
     *
     * @param db
     * @param table
     * @param nullColumnHack
     * 当values参数为空或者里面没有内容的时候，我们insert是会失败的（底层数据库不允许插入一个空行），为了防止这种情况，
     * 我们要在这里指定一个列名，到时候如果发现将要插入的行为空行时，就会将你指定的这个列名的值设为null，然后再向数据库中插入
     * @param values
     * @return
     */
    public static long insert(SQLiteDatabase db, String table, String nullColumnHack, ContentValues values) {
        if (null != db && null != values) {
            //
            return db.insert(table, nullColumnHack, values);
        }
        return -1;
    }

    public static long insert(SQLiteDatabase db, String table, ContentValues values) {
        return insert(db, table, null, values);
    }

    /**
     * 从数据库中删除行
     * eg
     *  1.删除所有code = 'zerain', name = '888'的行
     *      SQliteUtils.delete_nopress(db, TABLE, "code, name", "zerain", "888");
     * @param db
     * @param table
     * @param columns 行条件
     * @param values 条件值
     * @return
     */
    public static boolean delete(SQLiteDatabase db, String table, String columns, String... values) {
        return exec(db, makeDelete(table, columns, values));
    }

    public static int delete(SQLiteDatabase db, String table,String conditions){
        if (null != db) {
            return db.delete(table,"city=? ",new String[]{conditions});
        }
        return -1;
    }


    /**
     * 更新值
     * 更新 name = zerain 所在行的code和extra的值为9999999,00000
     * SQliteUtils.update(db, TABLE, "name = 'zerain'", "code, extra", "9999999", "00000");
     * SQliteUtils.update(db, TABLE, SQliteUtils.makeCompound("address", "=", address), "dtime, name", time, name);
     * @param db
     * @param table
     * @param conditions "name = 'zerain'"
     * @param columns "code, extra"
     * @param values "9999999", "00000"
     */
    public static boolean update(SQLiteDatabase db, String table, String conditions, String columns, String... values) {
        return exec(db, makeUpdate(table, conditions, columns, values));
    }

    /**
     *
     * @param db
     * @param table
     * @param values 更新的键值对
     * @param whereClause 条件
     * @param whereArgs 条件参数
     * @return
     */
    public static int update(SQLiteDatabase db, String table, ContentValues values, String whereClause, String[] whereArgs) {
        if (null != db) {
            return db.update(table, values, whereClause, whereArgs);
        }
        return -1;
    }

    /**
     * 更新值
     * @param db
     * @param table
     * @param cdtnames 条件名 "name, extra"
     * @param cdtvalues 条件值 new String[]{city, "aa"}, 与条件名拼凑的条件为"name='city', extra = 'a'"
     * @param columns 列名 "code, extra"
     * @param values 列值 "9999999", "00000"
     * @eg SQliteUtils.update(db, TABLE, "name, extra", new String[]{city, "aa"}, "code, extra", "9999999", "00000");
     */
    public static boolean updates(SQLiteDatabase db, String table, String cdtnames, String[] cdtvalues, String columns, String... values) {
        return exec(db, makeUpdates(table, cdtnames, cdtvalues, columns, values));
    }

    /**
     * 获取数据库
     *  1.获取所有code = 'zerain', name = '888'的行
     *      SQliteUtils.get(db, TABLE, "code, extra", "code, name", "zerain", "888");
     *  2.获取表中的20数据, 并以dtime降序排列
     *      SQliteUtils.gets(mSql, TABLE, "*", "order by dtime desc limit 20", null, null)
     * @param db
     * @param table
     * @param chooses 选中项, "code, extra"
     * @param cdtcolumns 条件项"code, name"
     * @param cdtvalues 条件值"zerain", "888"
     * @return
     */
    public static Cursor get(SQLiteDatabase db, String table, String chooses, String cdtcolumns, String... cdtvalues) {
        return rawquery(db, makeGets(table, chooses, cdtcolumns, cdtvalues));
    }

    /**
     *  1.获取所有code = 'zerain', name = '888'的行
     *      SQliteUtils.get(db, TABLE, "code, extra", "code, name", "zerain", "888");
     *  2.获取表中的20数据, 并以dtime降序排列
     *      SQliteUtils.gets(mSql, TABLE, "*", "order by dtime desc limit 20", null, null)
     * @param db
     * @param table
     * @param chooses 选中项, "code, extra"
     * @param cdtcolumns 选中项, "code, name"
     * @param cdtvalues 条件值"zerain", "888"
     * @return
     */
    public static Bundle getx(SQLiteDatabase db, String table, String chooses, String cdtcolumns, String... cdtvalues) {
        Bundle bundle = new Bundle();
        Cursor cursor = rawquery(db, makeGets(table, chooses, cdtcolumns, cdtvalues));
        if (null != cursor) {
            cursor.moveToNext();
            for (int i = 0; i < cursor.getCount(); i++) {
                String key = cursor.getColumnName(i);
                String obj = cursor.getString(i);
                if (null != key) {
                    bundle.putString(key, obj);
                }
            }
        }
        return bundle;
    }

    /**
     *  1.获取所有code = 'zerain', name = '888'的行
     *      SQliteUtils.get(db, TABLE, "code, extra", "code, name", "zerain", "888");
     *  2.获取表中的20数据, 并以dtime降序排列
     *      SQliteUtils.gets(mSql, TABLE, "*", "order by dtime desc limit 20", null, null)
     * @param db
     * @param table
     * @param chooses 选中项, "code, extra"
     * @param extra 附加指令 如排序 "order by dtime desc limit 20"
     * @param cdtcolumns 选中项, "code, name"
     * @param cdtvalues 条件值"zerain", "888"
     * @return
     */
    public static Cursor getc(SQLiteDatabase db, String table, String chooses, String extra, String cdtcolumns, String... cdtvalues) {
        return rawquery(db, makeGetx(table, chooses, extra, cdtcolumns, cdtvalues));
    }

    public static List<Bundle> gets(SQLiteDatabase db, String table, String chooses, String extra, String cdtcolumns, String... cdtvalues) {
        List<Bundle> result = new ArrayList<>();
        Cursor cursor = rawquery(db, makeGetx(table, chooses, extra, cdtcolumns, cdtvalues));
        if (null != cursor) {
            while (cursor.moveToNext()) {
                Bundle bundle = new Bundle();
                for (int i = 0; i < cursor.getCount(); i++) {
                    bundle.putString(cursor.getColumnName(i), cursor.getString(i));
                }
                if (!bundle.isEmpty()) {
                    result.add(bundle);
                }
            }
        }
        return result;
    }


    /**
     * 获取数据行数
     * eg
     *  1.获取所有code = zerain, name = '888'的行数
     *      SQliteUtils.counter(db, TABLE, "code", "code, name", "zerain", "888");
     *      获取所有非code = zerain, name = '888'的行数
     *      SQliteUtils.counter(db, TABLE, "code", true, "code, name", "zerain", "888");
     *  2.获取总行数,
     *      SQliteUtils.counter(db, TABLE, "*", null); or SQliteUtils.counter(db, TABLE, null, null);
     * @param table
     * @param speciColumns 选中项, 如果为null 或者"*" 表示选取所有行, 一般用于获取总行
     * @param distinct 取反
     * @param cdtcolumns 条件项"code, name"
     * @param cdtvalues 条件值"zerain", "888"
     * @return
     */
    public static int counter(SQLiteDatabase db, String table, String speciColumns, boolean distinct, String cdtcolumns, String... cdtvalues) {
        Cursor cursor = rawquery(db, makeCounter(table, speciColumns, distinct, cdtcolumns, cdtvalues));
        if (null != cursor && cursor.moveToNext()) {
            String cnt = cursor.toString();
            if (null != cnt && cnt.matches("^\\d+$")) {
                return Integer.valueOf(cnt);
            }
        }
        return -1;
    }

    public static int counter(SQLiteDatabase db, String table, String speciColumns, String cdtcolumns, String... cdtvalues) {
        return counter(db, table, speciColumns, false, cdtcolumns, cdtvalues);
    }

    /**
     * 判断表是否存在
     * @param db
     * @param table
     * @return
     */
    public static boolean isTableExists(SQLiteDatabase db, String table) {
        Cursor cursor = rawquery(db, makeIsTableExists(table));
        return  (null != cursor && cursor.moveToNext());
    }

    /**
     * 检测符合条件的列是否存在,如检测列code = '长沙', extra = '9999' 的列是否存在
     * @param db
     * @param table
     * @param columns "code, extra"
     * @param conditions "长沙", "9999"
     * @return
     * @eg  检测列code = '长沙', extra = '9999' 的列是否存在
     *      SQliteUtils.isColumnExists(db, TABLE, null, "code, extra", "长沙", "9999");
     */
    public static boolean isColumnExists(SQLiteDatabase db, String table, String selection, String columns, String... conditions) {
        Cursor cursor = rawquery(db, makeGets(table, selection, columns, conditions));
        return  (null != cursor) ? cursor.moveToNext(): false;
    }

    /**
     * 事务处理,只能与 DML 命令 INSERT、UPDATE 和 DELETE 一起使用
     * 当一个事务创建后, 可设置超时处理, 即在延时期间用户可以增加多条语句,在超时后统一处理, 在处理数据后并回调监听
     */
    public static class SqlTransaction {
        public final static int FILTER_INSERT = 1;
        public final static int FILTER_DELETE = 2;
        public final static int FILTER_UPDATE = 4;
        private final static int FILTER_MASK = 7;

        int mFilter = FILTER_INSERT|FILTER_DELETE|FILTER_UPDATE;
        int mExecAfterDelay = 1000; //默认1s内
        SQLiteDatabase mDatabase;
        SQLiteTransactionListener mTransactionListener;
        LinkedList<Expression> mSqlData = new LinkedList<>();

        Handler mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1://启动事务, 如果没有启动事务, 则在mExecAfterDelay后执行事务
                        if (!mHandler.hasMessages(2)) {
                            mHandler.sendEmptyMessageDelayed(2, mExecAfterDelay);
                        }
                        break;

                    case 2: //事务处理
                        if (null != mDatabase) {
                            if (null != mTransactionListener) {
                                mDatabase.beginTransactionWithListener(mTransactionListener);
                            } else {
                                mDatabase.beginTransaction();
                            }

                            boolean success = false;
                            LinkedList<Expression> listeners = new LinkedList<>(mSqlData);
                            try {
                                synchronized (mSqlData) {
                                    log("Transaction exec starting");
                                    for (Expression expression : mSqlData) {
                                        if (null != expression && null != expression.sql) {//insert, update, delete_nopress
                                            exec(mDatabase, expression.sql);
                                        }
                                    }
                                    mSqlData.clear();
                                }
                                mDatabase.setTransactionSuccessful();
                                success = true;
                                log("Transaction exec completed");
                            } catch(SQLiteException e){
                                log("Transaction exec error " + e.toString());
                            } finally {
                                mDatabase.endTransaction();
                            }

                            for (int i = 0; i < listeners.size(); i++) {
                                Expression listener = listeners.get(i);
                                if (null != listener && null != listener.sql && null != listener.listener) {
                                    //当success==false, 造成错误的语句为最后一条
                                    boolean causedby = (success ? true : (i == (listeners.size() - 1)));
                                    listener.listener.expression(listener.sql, success, causedby);
                                }
                            }
                        }
                        break;
                }
            }
        };

        /**
         * @param db
         */
        public SqlTransaction(SQLiteDatabase db) {
            mDatabase = db;
        }

        public SqlTransaction(SQLiteDatabase db, int filter) {
            mDatabase = db;
            mFilter = (filter & FILTER_MASK);
        }

        public void setTransactionListener(SQLiteTransactionListener listener, int doAfterDelay) {
            if (mExecAfterDelay >= 0) {
                mExecAfterDelay = doAfterDelay;
            }
            mTransactionListener = listener;
        }

        public Cursor transactExpression(String sql, ExpressionListener listener) {
            if (null != mDatabase && null != sql) {
                int filter = 0;
                if (isInsertExpress(sql)) {
                    filter |= FILTER_INSERT;
                } else if (isDeleteExpress(sql)) {
                    filter |= FILTER_DELETE;
                } else if (isUpdateExpress(sql)) {
                    filter |= FILTER_UPDATE;
                }
                if (filter != 0) {
                    if ((mFilter & filter) != 0) {
                        synchronized (mSqlData) {
                            mSqlData.add(new Expression(sql, listener));
                            mHandler.sendEmptyMessage(1);
                        }
                    } else {//立即执行
                        exec(mDatabase, sql);
                    }
                } else if (isGetExpress(sql)) {//立即执行
                    return rawquery(mDatabase, sql);
                }
            }
            return null;
        }

        private class Expression {
            String sql;
            ExpressionListener listener;
            public Expression(String sql, ExpressionListener listener) {
                this.sql = sql; this.listener = listener;
            }
        }

        /**
         * 表达式执行监听
         */
        public interface ExpressionListener {
            void expression(String sql, boolean success, boolean causedby);
        }
    }

    public static void log(String ...msgs) {
        String str = "";
        if (null != msgs && msgs.length > 0) {
            for (String msg: msgs) {
                if (null != msg) {
                    str += " ";
                    str += msg;
                }
            }
        }
        System.out.print(TAG + " " + str);
    }
    public final static String TAG = SQliteUtils.class.getSimpleName();
}
