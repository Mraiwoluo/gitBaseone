package com.pvetec.common.core;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.pvetec.common.utils.CommonUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。 如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框 实现该接口并注册为程序中的默认未捕获异常处理 这样当未捕获异常发生时，就可以做些异常处理操作 例如：收集异常信息，发送错误报告 等。
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 */
public class CrashSystem implements UncaughtExceptionHandler {

    public static final String TAG = "CrashSystem";

    private Context mContext;

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private String appName = null;

    private CrashSystem(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public static CrashSystem sCrashSystem = null;

    public static CrashSystem getInstance(Context ctx) {
        if (sCrashSystem != null) return sCrashSystem;
        synchronized (CrashSystem.class) {
            if (sCrashSystem == null) sCrashSystem = new CrashSystem(ctx);
        }
        return sCrashSystem;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     * 
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(final Throwable ex) {

        try {
            if (appName != null && mContext != null) {
                CommonUtils.sendAppExit(mContext, appName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        saveCrashInfo2File(ex);
        // 使用Toast来显示异常信息
        /*
         * new Thread() {
         * @Override public void run() { // Toast 显示需要出现在一个线程的消息队列中 Looper.prepare(); Toast.makeText(mContext, "亲!程序异常,我们会尽快修复哦",
         * Toast.LENGTH_SHORT).show(); Looper.loop(); } }.start();
         */
        try {
            LogPrint.e(TAG, ex.getMessage(), ex, true);
            LogPrint.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        String result = writer.toString();
        sb.append(result);
        // 保存文件
        long timetamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String fileName = "crash-" + time + "-" + timetamp + ".log";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            try {
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash");
                Log.i("CrashHandler", dir.toString());
                if (!dir.exists()) dir.mkdir();
                FileOutputStream fos = new FileOutputStream(new File(dir, fileName));
                fos.write(sb.toString().getBytes());
                fos.close();
                return fileName;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
