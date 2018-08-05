package com.pvetec.common.core;

import android.app.Application;

import com.pvetec.common.service.LogService;
import com.pvetec.common.service.impl.LogServiceImpl;

public class LogPrint  {

    /**
     * 日志接口负责上传日志
     */
    private static LogService mLog=null;
    
    public static boolean DEBUG = false;
    
    /**
     *  请在Application中初始化
     */
    public static void init(Application app) {
        init(app,null);
    }
    
    /**
     *  请在Application中初始化
     */
    public static void init(Application app, LogServiceImpl.LogConfig config) {
        if (mLog == null) {
            synchronized (LogPrint.class) {
                if (mLog == null) mLog = new LogServiceImpl(app,config);
            }
        }
    }
    
    public static int v(String tag, String msg) {
        if (mLog != null) mLog.v(tag, msg);
        if(DEBUG) return android.util.Log.v(tag, msg);
        return 0;
    }

    public static void setDebug(boolean debug)
    {
        DEBUG=debug;
    }
    public static  int v(String tag, String msg, Throwable tr) {
        if (mLog != null) mLog.v(tag, msg + '\n' + android.util.Log.getStackTraceString(tr));
        if(DEBUG) return android.util.Log.v(tag, msg, tr);
        return 0;
    }

    public static int d(String tag, String msg) {
        if (mLog != null) mLog.d(tag, msg);
        if(DEBUG) return android.util.Log.d(tag, msg);
        return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (mLog != null) mLog.d(tag, msg + '\n' + android.util.Log.getStackTraceString(tr));
        if(DEBUG) return android.util.Log.d(tag, msg, tr);
        return 0;
    }

    public static int i(String tag, String msg) {
        if (mLog != null) mLog.i(tag, msg);
        if(DEBUG) return android.util.Log.i(tag, msg);
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (mLog != null) mLog.i(tag, msg + '\n' + android.util.Log.getStackTraceString(tr));
        if(DEBUG) return android.util.Log.i(tag, msg, tr);
        return 0;
    }

    public static int w(String tag, String msg) {
        if (mLog != null) mLog.w(tag, msg);
        return android.util.Log.w(tag, msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (mLog != null) mLog.w(tag, msg + '\n' + android.util.Log.getStackTraceString(tr));
        return android.util.Log.w(tag, msg, tr);
    }

    public static int w(String tag, Throwable tr) {
        if (mLog != null) mLog.w(tag, android.util.Log.getStackTraceString(tr));
        return android.util.Log.w(tag, tr);
    }

    public static int e(String tag, String msg) {
        if (mLog != null) mLog.e(tag, msg);
        return android.util.Log.e(tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (mLog != null) mLog.e(tag, msg + '\n' + android.util.Log.getStackTraceString(tr));
        return android.util.Log.e(tag, msg, tr);
    }
    

    public static int e(String tag, String msg, boolean immediate) {
        if (mLog != null) mLog.e(tag, msg,immediate);
        return android.util.Log.e(tag, msg);
    }

    public static int e(String tag, String msg, Throwable tr, boolean immediate) {
        if (mLog != null) mLog.e(tag, msg + '\n' + android.util.Log.getStackTraceString(tr),immediate);
        return android.util.Log.e(tag, msg, tr);
    }

    public static int println(int priority, String tag, String msg) {
        if (mLog != null) mLog.println(priority, tag, msg);
        return android.util.Log.println(priority, tag, msg);
    }

    /**
     * 把当前保存在内存的数据写入文件
     */
    public static void flush() {
        if (mLog != null) mLog.flush();
    }

    /**
     * 程序异常退出时调用,调用后将不再记录日志。
     */
    public static void quit() {
        if (mLog != null) mLog.quit();
    }

}
