package com.pvetec.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dalvik.system.PathClassLoader;

public class AndroidUtil {

    private static final String TAG = "AndroidUtil";

    private static final String CRASH_LOG_DIR = "crashLogs";

    /** 剩余空间 **/
    private static final long SURPLUS_AVAIL_SIZE = 100 * 1024 * 1024;// 100M

    public static final long MIN_PLAYSDK_SOTROAGRE_SIZE = 30 * 1000 * 1000;

    public static final String FORMAT_ALL_DATE = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String FORMAT_TIME = "HH:mm:ss";

    public static final String FORMAT_DATE = "yyyy-MM-dd";

    public static final String FORMAT_SIMPLE_DATE = "yyyyMMdd";

    private final static SimpleDateFormat sFORMAT = new SimpleDateFormat(FORMAT_ALL_DATE, Locale.CHINA);

    public static String getDocumentPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static String getSDCardDataPath(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    public static String getSDKVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        String pkgName = context.getPackageName();
        PackageInfo pkgInfo = null;
        String ret = "";
        try {
            pkgInfo = pm.getPackageInfo(pkgName, PackageManager.GET_CONFIGURATIONS);
            ret = pkgInfo.versionName;
        } catch (NameNotFoundException ex) {

        }
        return ret;
    }

    public static String generatePlayTime(long time) {
        if (time % 1000 >= 500) {
            time += 1000;
        }
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format(Locale.CHINA, "%02d:%02d:%02d", hours, minutes, seconds) : String.format(
                Locale.CHINA,
                "%02d:%02d",
                minutes,
                seconds);
    }

    /**
     * 用反射方式加载类
     * 
     * @param context
     * @param path
     * @return
     */
    public static Object loadClass(Context context, String path) {
        try {
            String dexPath = context.getApplicationInfo().sourceDir;
            PathClassLoader pathClassLoader = new PathClassLoader(dexPath, context.getClassLoader());
            Class<?> c = Class.forName(path, true, pathClassLoader);
            Object ret = c.newInstance();
            return ret;
        } catch (InstantiationException ex1) {
            ex1.printStackTrace();
        } catch (IllegalAccessException ex2) {
            ex2.printStackTrace();
        } catch (ClassNotFoundException ex3) {
            ex3.printStackTrace();
        }

        return null;
    }

    public static String generateTime(long time, boolean isLong) {
        Date date = new Date(time);
        sFORMAT.applyPattern(isLong ? FORMAT_ALL_DATE : FORMAT_TIME);
        String LgTime = null;
        try {
            LgTime = sFORMAT.format(date);
        } catch (Exception e) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(isLong ? FORMAT_ALL_DATE : FORMAT_TIME, Locale.CHINA);
                LgTime = format.format(new Date());
                e.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
                LgTime = "";
            }
        }
        return LgTime;
    }

    public static boolean isEmpty(String str) {
        return null == str || "".equals(str) || "NULL".equals(str.toUpperCase(Locale.CHINA));
    }

    /**
     * md5
     * 
     * @param str
     * @return
     */
    public static String md5(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            byte[] byteArray = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    sb.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    public static String getOSVersionInfo() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * Get mobile model, like GT-i9500 etc.
     *
     * @return
     */
    public static String getMobileModel() {
        return android.os.Build.MODEL;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getCPU() {
        return Build.CPU_ABI;
    }

    public static String getSDKRelease() {
        return Build.VERSION.RELEASE;
    }

    public static int getSDKInt() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取本地Ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null && wifiInfo.getIpAddress() != 0) {
            return android.text.format.Formatter.formatIpAddress(wifiInfo.getIpAddress());
        } else {
            try {
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                while (en.hasMoreElements()) {
                    NetworkInterface intf = en.nextElement();
                    Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().indexOf(":") == -1) {
                            String ipAddress = inetAddress.getHostAddress();
                            if (!TextUtils.isEmpty(ipAddress) && !ipAddress.contains(":")) {
                                return ipAddress;
                            }
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Cmwap网络是否已连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnectedByCmwap(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getExtraInfo() != null
                && networkInfo.getExtraInfo().toLowerCase().contains("cmwap");
    }

    /**
     * 连接的是否是2G网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnectedBy2G(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null && networkInfo.isConnected()) {
            int subtype = networkInfo.getSubtype();
            if (subtype == TelephonyManager.NETWORK_TYPE_GPRS || subtype == TelephonyManager.NETWORK_TYPE_EDGE
                    || subtype == TelephonyManager.NETWORK_TYPE_CDMA) {// 移动和联通2G
                return true;
            }
        }
        return false;
    }

    /**
     * 连接的是否是3G网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnectedBy3G(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null && networkInfo.isConnected()) {
            int subtype = networkInfo.getSubtype();
            if (subtype == TelephonyManager.NETWORK_TYPE_EVDO_A || subtype == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subtype == TelephonyManager.NETWORK_TYPE_UMTS || subtype == TelephonyManager.NETWORK_TYPE_HSPA) {// 电信或联通3G
                return true;
            }
        }
        return false;
    }

    /**
     * Wifi网络是否已连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnectedByWifi(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 网络是否已连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 是否开启飞行模式
     *
     * @param context
     * @return
     */
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    /**
     * 获取当前屏幕亮度，范围0-255
     *
     * @param context
     * @return 屏幕当前亮度值
     */
    public static int getScreenBrightness(Context context) {
        int rightnessValue = 0;
        try {
            rightnessValue = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return rightnessValue;
    }

    /**
     * 设置屏幕亮度（0-255）
     *
     * @param activity
     * @param screenBrightness
     */
    public static void setScreenBrightness(Activity activity, float screenBrightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = screenBrightness / 255f;
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 判断是否开启了自动亮度调节
     *
     * @param context
     * @return
     */
    public static boolean isAutomicBrightness(Context context) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 开启亮度自动调节
     *
     * @param context
     */
    public static void startAutoBrightness(Context context) {
        Settings.System
                .putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 停止自动亮度调节
     *
     * @param context
     */
    public static void stopAutoBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 跳转系统网络设置界面
     *
     * @param context
     */
    public static boolean startActivitySettingWireless(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 14) {
            intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent.setAction(Settings.ACTION_SETTINGS);
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 调整程序声音类型为媒体播放声音，并且与媒体播放声音大小一致
     *
     * @param context
     */
    public static void adjustVoiceToSystemSame(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, 0);
    }

    /**
     * sdcard是否可读写
     *
     * @return
     */
    public static boolean isSdcardReady() {
        try {
            return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        } catch (Exception e) {

            return false;
        }
    }

    public static File getDownLoadApkDir(Context context) {
        File externalCacheDir = getExternalCacheDir(context);
        if (externalCacheDir != null && !externalCacheDir.exists()) {
            externalCacheDir.mkdir();
        }
        if (externalCacheDir == null || !externalCacheDir.exists()) {
            externalCacheDir = context.getCacheDir();
            externalCacheDir.mkdir();
        }
        if (externalCacheDir == null) return null;
        String downPath = externalCacheDir.getAbsolutePath() + "/downapk/";
        File downDir = new File(downPath);
        if (!downDir.exists()) downDir.mkdir();
        return downDir;
    }

    public static void clearDownLoadApkDir(Context context) {
        File externalCacheDir = getDownLoadApkDir(context);
        try {
            deleteAllFiles(externalCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null) for (File f : files) {
            if (f.isDirectory()) { // 判断是否为文件夹
                deleteAllFiles(f);
                try {
                    f.delete();
                } catch (Exception e) {
                }
            } else {
                if (f.exists()) { // 判断是否存在
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public static File getDownLoadFileDir(Context context) {
        File externalCacheDir = getExternalCacheDir(context);
        if (externalCacheDir != null && !externalCacheDir.exists()) {
            externalCacheDir.mkdir();
        }
        if (externalCacheDir == null || !externalCacheDir.exists()) {
            externalCacheDir = context.getCacheDir();
            externalCacheDir.mkdir();
        }
        if (externalCacheDir == null) return null;
        String downPath = externalCacheDir.getAbsolutePath() + "/downfile/";
        File downDir = new File(downPath);
        if (!downDir.exists()) downDir.mkdir();
        return downDir;
    }

    public static File getExternalCacheDir(Context context) {
        if (context == null) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File path = context.getExternalCacheDir();
            // In some case, even the sd card is mounted,
            // getExternalCacheDir will return null
            // may be it is nearly full.
            if (path != null) {
                return path;
            }
        }
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * 获取存放日志的目录 用于上传 当前设备基本都有外部存储 context.getDir 基本不会调用到
     *
     * @param context
     * @return
     */
    public static File getLogDir(Context context) {
        if (context == null) return null;
        if (isSdcardReady()) return getExternalStorageLogDir();
        return context.getDir(CRASH_LOG_DIR, Context.MODE_PRIVATE);
    }

    /**
     * 获取外部App存放日志的目录用于上传
     *
     * @param context
     * @return
     */

    public static File getExternalStorageLogDir() {
        if (isSdcardReady()) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            File exLogDir = new File(sdcardDir.getAbsolutePath() + "/" + CRASH_LOG_DIR);
            if (!exLogDir.exists()) exLogDir.mkdir();
            return exLogDir;
        }
        return null;
    }

    /**
     * 获取存放日志缓存目录(先写进缓存 再存入) 存放位置 进程名相关
     *
     * @param context
     * @return
     */

    public static File getLogCacheDir(Context context) {
        if (context == null) return null;
        File cacheDir = null;
        if (isSdcardReady())
            cacheDir = getExternalCacheDir(context);
        else
            cacheDir = context.getCacheDir();
        if (cacheDir == null) return null;
        File logCache = new File(cacheDir.getAbsolutePath() + "/" + CRASH_LOG_DIR);
        if (!logCache.exists()) logCache.mkdir();
        return logCache;

    }

    public static boolean isSdcardAvailable(int fileSize) {
        if (isSdcardReady()) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long availCount = sf.getAvailableBlocks();
            long blockSize = sf.getBlockSize();
            long availSize = availCount * blockSize;
            if (availSize - SURPLUS_AVAIL_SIZE >= fileSize) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * 获取文件系统的剩余空间，单位：KB
     *
     * @return
     */
    public static long getFileSystemAvailableSize(File dirName) {
        long availableSize = -1;
        if (dirName != null && dirName.exists()) {
            StatFs sf = new StatFs(dirName.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availableBlocks = sf.getAvailableBlocks();
            availableSize = availableBlocks * blockSize / 1024;
        }
        return availableSize;
    }

    /**
     * 获取Context所在进程的名称
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 应用是否已经安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            // mContext.getPackageInfo(String packageName, int
            // flags)第二个参数flags为0：因为不需要该程序的其他信息，只需返回程序的基本信息。
            return context.getPackageManager().getPackageInfo(packageName, 0) != null;
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    /**
     * 网络是否手机网络连接
     *
     * @param context
     * @return
     */
    public static boolean isOnlyMobileType(Context context) {
        State wifiState = null;
        State mobileState = null;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        try {
            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (networkInfo != null) {
            wifiState = networkInfo.getState();
        }
        try {
            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (networkInfo != null) {
            mobileState = networkInfo.getState();
        }

        if (wifiState != null && mobileState != null && State.CONNECTED != wifiState && State.CONNECTED == mobileState) {
            // 手机网络连接成
            return true;
        }
        return false;
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            String packageName = context.getPackageName();
            List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            for (RunningAppProcessInfo appProcess : appProcesses) {
                // The name of the process that this object is associated with.
                if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static long getAvailableInternalRomSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        long availBlocks = stat.getAvailableBlocks();
        return availBlocks * blockSize;
    }

    private static String mTempString = null;

    public static String numToChina(int num) {
        int size = (num + "").length();
        if (size == 1) {
            mTempString = arabNumtoChinese(num);
        } else if (size == 2) {
            if (num / 10 == 1) {
                mTempString = "十";
            } else {
                mTempString = arabNumtoChinese(num / 10) + "十";
            }
            if (num % 10 != 0) {
                mTempString += arabNumtoChinese(num % 10);
            }
        } else if (size == 3) {
            mTempString = arabNumtoChinese(num / 100) + "百";
            if (num % 100 != 0) {
                int num1 = num % 100;
                int num2 = num1 / 10;
                mTempString += arabNumtoChinese(num1 / 10);
                if (num2 != 0) {
                    mTempString += "十";
                }
                if (num % 10 != 0) {
                    mTempString += arabNumtoChinese(num1 % 10);
                }
            }
        }
        return mTempString;
    }

    private static String arabNumtoChinese(int a) {
        StringBuffer sb = new StringBuffer();
        String[] str = new String[] { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
        return sb.append(str[a]).toString();
    }

    /**
     * 判断是否是本地url
     *
     * @return
     */
    public static boolean isLocalUrl(String url) {
        File file = Environment.getExternalStorageDirectory();
        if (AndroidUtil.isSdcardReady() && file != null && url.startsWith(file.getAbsolutePath())) {
            return true;
        }
        if (url.startsWith("http")) return false;
        return false;
    }

    public static boolean deleteFile(File dir, String name) {
        try {
            File file = new File(dir.getAbsolutePath() + "/" + name);
            if (!file.exists()) return false;
            file.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void InstallApk(Context context, String apkPath) {
        if (context == null || apkPath == null || apkPath.isEmpty()) return;
        String permission = "777";
        try {
            String command = "chmod " + permission + " " + apkPath;
            Log.i("InstallApk command=", command);
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkPath), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 卸载应用
     *
     * @param context
     *            应用上下文
     * @param pkgName
     *            包名
     */
    public static void uninstallApk(Context context, String pkgName) {
        try {
            Uri packageURI = Uri.parse("package:" + pkgName);
            Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户安装的应用列表
     */
    public static List<PackageInfo> getInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        final String ourPackageName = context.getPackageName();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        for (PackageInfo info : packages) {
            // 只返回非系统级应用
            if (((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) && !ourPackageName.equals(info.packageName)) {
                apps.add(info);
            }
        }
        return apps;
    }

    /**
     * 获取用户本机所有应用程序
     */
    public static List<PackageInfo> getInstalledAppsExceptSelf(Context context) {
        if (context == null) return null;
        PackageManager pm = context.getPackageManager();
        final String ourPackageName = context.getPackageName();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        for (PackageInfo info : packages) {
            if (!ourPackageName.equals(info.packageName)) apps.add(info);
        }
        return apps;
    }

    /**
     * 获取用户本机所有应用程序
     */
    public static Map<String, PackageInfo> getInstalledAppsExceptSelfMap(Context context) {
        if (context == null) return null;
        PackageManager pm = context.getPackageManager();
        final String ourPackageName = context.getPackageName();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        Map<String, PackageInfo> apps = new HashMap<String, PackageInfo>();
        for (PackageInfo info : packages) {
            if (!ourPackageName.equals(info.packageName)) apps.put(info.packageName, info);
        }
        return apps;
    }

    public static void openApk(Context context, String appPakage) {
        try {
            if (context == null || appPakage == null) return;
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(appPakage);
            if (intent == null) return;
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean checkNetworkAndPrompt(Context context, int testId) {
        if (!isNetworkConnected(context)) {
            if (testId > 0) Toast.makeText(context, testId, 1000).show();
            return false;
        }
        return true;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static Drawable getAppIcon(Context context, String appPakage) {
        if (appPakage == null || context == null) return null;
        ApplicationInfo info = null;
        Drawable icon = null;
        try {
            info = context.getPackageManager().getApplicationInfo(appPakage, 0);
            if (info == null) return null;
            icon = info.loadIcon(context.getPackageManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }

    public static void InstallApkSilent(Context context, final String apkPath) {
        if (context == null || apkPath == null || apkPath.isEmpty()) return;
        Intent intent = new Intent("android.intent.action.INSTALL_PACKAGE");
        intent.setDataAndType(Uri.parse("file://" + apkPath), "application/vnd.android.package-archive");
        Intent eintent = getExplicitIntent(context, intent);
        if (eintent == null) {
            InstallApk(context, apkPath);
        } else {
            context.startService(eintent);
        }
    }

    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {

        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    public static String getSha1Fingerprint(Context context) {
        String packageName = context.getPackageName();
        String fingerprint = getSha1Fingerprint(context, packageName);
        return fingerprint;
    }

    private static String getSha1Fingerprint(Context context, String packageName) {
        StringBuffer sb = null;
        String strCertificate;
        try {
            Signature[] signature = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
            strCertificate = getCertificateString(CertificateFactory.getInstance("X.509").generateCertificate(
                    new ByteArrayInputStream(signature[0].toByteArray())));
            sb = new StringBuffer();
            for (int i = 0; i < strCertificate.length(); i++) {
                sb.append(strCertificate.charAt(i));
                if ((i > 0) && (i % 2 == 1) && (i < -1 + strCertificate.length())) sb.append(":");
            }
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            strCertificate = "";
        } catch (CertificateException localCertificateException) {
            strCertificate = "";
        }
        return sb.toString();
    }

    static String getCertificateString(Certificate certificate) {
        try {
            String str = HexUtil.encodeToString(digestWithSha1(certificate.getEncoded()));
            return str;
        } catch (CertificateEncodingException localCertificateEncodingException) {
        }
        return null;
    }

    static byte[] digestWithSha1(byte[] data) {
        try {
            byte[] arrayOfByte = MessageDigest.getInstance("SHA1").digest(data);
            return arrayOfByte;
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
        }
        return null;
    }

    public static String getDeviceId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }

}