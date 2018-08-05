package com.pvetec.common.service.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.pvetec.common.Constants;
import com.pvetec.common.bean.AppLogBean;
import com.pvetec.common.dao.ThreadWriteDao;
import com.pvetec.common.dao.impl.SQLiteDaoImpl;
import com.pvetec.common.service.LogService;
import com.pvetec.common.service.UploadLogService;
import com.pvetec.common.utils.AndroidUtil;
import com.pvetec.common.utils.HttpUtil;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 日志上传类 非线程安全
 * 
 * @author Administrator
 */
public class LogServiceImpl implements LogService {

    /**
     * 版本号
     */
    private int mAppVersion;

    /**
     * 包名
     */
    private String mAppPackage;

    /**
     * 平台版本号
     */
    private String mPlatformVersion;

    /**
     * 系统版本号
     */
    private String mOsVersion;
    
    private String mDeviceId="-1";

    /**
     * 定时上传日志Timer;
     */
    private Timer mLogTimer = null;

    private Context mContext = null;

    private LogConfig mLogConig=null;
    
    private ThreadWriteDao<AppLogBean> mSqlitLogDao = null;
    
    private UploadLogService mUploadLogDao=new UploadLogServiceImpl();

    LogServiceImpl(Context context) {
        this(context,null);
    }
    
   public LogServiceImpl(Context context, LogConfig logConfig) {
        mContext = context;
        mLogConig=defultConfig(context,logConfig);
        this.initApp(context);
        this.initLogThread(context,mLogConig);
        this.initUploadTimer(context,mLogConig);
    }
    
    private LogConfig defultConfig(Context context, LogConfig config) {
        if(config==null) config = new LogConfig();
        if(config.getHttpHost()==null||config.getHttpHost().isEmpty())
        config.setHttpHost(Constants.HOST);
        if(config.getHttpLogAction()==null||config.getHttpLogAction().isEmpty())
        config.setHttpLogAction(Constants.PATH_UPLOAD_LOG);
        if(config.getHttpLogFileAction()==null||config.getHttpLogFileAction().isEmpty())
        config.setHttpLogFileAction(Constants.PATH_UPLOAD_LOG_DB);
        if(config.getCacheDir()==null)
        config.setCacheDir(AndroidUtil.getLogCacheDir(context));
        if(config.getUploadDir()==null)
        config.setUploadDir(AndroidUtil.getLogDir(context));
        if(config.getQuestFreq()<=0)
        config.setQuestFreq(1000 * 60 * 5);
        return config;
    }

    private void initApp(Context context) {
        mAppPackage = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(mAppPackage, 0);
            mAppVersion = packageInfo.versionCode;
            mAppPackage = packageInfo.packageName;
            mOsVersion = android.os.Build.VERSION.RELEASE;
            mDeviceId=AndroidUtil.getDeviceId(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLogThread(Context context, LogConfig logConfig) {
        if(context == null||logConfig==null) return;
        if(!logConfig.getUpload()) return;
        mSqlitLogDao = new SQLiteDaoImpl<AppLogBean>(logConfig.getCacheDir(),logConfig.getUploadDir(),AppLogBean.class);
    }

    private void initUploadTimer(Context context, LogConfig logConfig) {
        mLogTimer = new Timer();
        mLogTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (AndroidUtil.isNetworkConnected(mContext)) uploadLogFile();
            }
        }, 1000 * 60, logConfig.getQuestFreq());
    }

    @Override
    public int v(String tag, String msg) {
        return 0;
    }

    @Override
    public int d(String tag, String msg) {
        return 0;
    }

    @Override
    public int i(String tag, String msg) {
        return 0;
    }

    @Override
    public int w(String tag, String msg) {
        return println(Log.WARN, tag, msg);
    }

    @Override
    public int e(String tag, String msg) {
        return e(tag, msg, false);
    }

    @Override
    public int e(String tag, String msg, boolean immediate) {
        if (immediate)
            printlnImmediate(Log.ERROR, tag, msg);
        else
            return println(Log.ERROR, tag, msg);
        return 0;
    }
    
    @Override
    public int println(int priority, String tag, String msg) {
        if(mSqlitLogDao == null) return 0;
        AppLogBean logBean = new AppLogBean(tag, msg, mAppPackage, mAppVersion, mPlatformVersion, mOsVersion, new Date(), priority
                ,mDeviceId);
        return mSqlitLogDao.scheduleWrite(logBean);
    }

    /**
     * 立即上传到web后台 ，如果上传失败，则写入数据库 ,次函数一般在异常退出的被时候调用
     * 
     * @return
     */
    public int printlnImmediate(final int priority, final String tag, final String msg) {
        boolean result=AndroidUtil.isNetworkConnected(mContext)&&uploadLog(priority,tag,msg);
        if (!result) println(priority, tag, msg);
        return 0;
    }

    /**
     * 写文件到数据
     * 
     * @see com.pvetec.common.service.LogService#flush()
     */
    @Override
    public void flush() {
        if (mSqlitLogDao == null) return;
        mSqlitLogDao.scheduleFlush();
    }

    /**
     * //此处 mLogTread mLogTimer..等成员变量不是置0 多线程问题
     * 
     * @see com.pvetec.common.service.LogService#quit()
     */
    @Override
    public void quit() {
        try {
            if (mSqlitLogDao == null) return;
            mSqlitLogDao.scheduleQuit();
            mSqlitLogDao.waitFinish(1000*30);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLogTimer != null) mLogTimer.cancel();
    }

    /*
     * 上传日志到服务器
     */
    private void uploadLogFile() {
        try {
            File logDir = mLogConig.getUploadDir();
            if (logDir == null) return;
            File[] files = logDir.listFiles();
            if (files == null) return;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if(!AndroidUtil.isNetworkConnected(mContext)) return;
                if (HttpUtil.uploadFile(mLogConig.getHttpHost() + "/" +mLogConig.getHttpLogFileAction(), file)) file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /*
     * 上传日志到服务器
     */
    private boolean uploadLog(final int priority, final String tag, final String msg) {
        ExecutorService pool = Executors.newCachedThreadPool();
        Future<Boolean> future = pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean result = mUploadLogDao.uploadLogFile(
                        mLogConig.getHttpHost()+ "/" + mLogConig.getHttpLogAction(),
                        mAppPackage,
                        mAppVersion,
                        mPlatformVersion,
                        mOsVersion,
                        priority,
                        mDeviceId,
                        tag,
                        msg);
                return result;
            }
        });
        boolean result = false;
        try {
            result = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static class LogConfig
    {
        
        /**
         * 路径 /端口
         */
        private String mHttpHost;
        /**
         * 上传日志文本接口
         */
        private String mHttpLogAction;
        
         /**
          * 上传日志文件接口
          */
        private String mHttpLogFileAction;
        
        
        /**
         * 日志临时写入目录
         */
        
        private File mCacheDir;
        
        /**
         * 需要上传的日志的目录
         */
        private File mUploadDir;
        
        /**
         * 日志上传频率
         */
        private int mQuestFreq;
        
        private boolean mUpload=false;
        
        
        public boolean getUpload() {
            return mUpload;
        }
        
        public void setUpload(boolean upload) {
            mUpload=upload;
        }
        
        public String getHttpHost() {
            return mHttpHost;
        }
        
        public void setHttpHost(String mHttpUrl) {
            this.mHttpHost = mHttpUrl;
        }
        
        
        public String getHttpLogAction() {
            return mHttpLogAction;
        }
        
        
        public void setHttpLogAction(String mSrvLogAction) {
            this.mHttpLogAction = mSrvLogAction;
        }
        
        
        public String getHttpLogFileAction() {
            return mHttpLogFileAction;
        }
        
        
        public void setHttpLogFileAction(String mSrvLogFileAction) {
            this.mHttpLogFileAction = mSrvLogFileAction;
        }
        
        
        public File getCacheDir() {
            return mCacheDir;
        }
        
        public void setCacheDir(File mLogCacheDir) {
            this.mCacheDir = mLogCacheDir;
        }
        
        
        public File getUploadDir() {
            return mUploadDir;
        }
        
        
        public void setUploadDir(File mLogUploadDir) {
            this.mUploadDir = mLogUploadDir;
        }
        
        
        public int getQuestFreq() {
            return mQuestFreq;
        }
        
        
        public void setQuestFreq(int mUploadFreq) {
            this.mQuestFreq = mUploadFreq;
        }
        
        
    }
       
     
}
