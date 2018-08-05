package com.pvetec.common.dao.impl;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.pvetec.common.dao.ThreadWriteDao;
import com.pvetec.common.utils.AndroidUtil;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;

public class SQLiteDaoImpl<T> extends HandlerThread implements ThreadWriteDao<T>{
    
    /**
     * 插入数据得类型
     */
    Class<?> mTClass;
    /**
     * 写日志请求
     */
    public static final int MESSAGE_WHAT_WRITE = 1;

    /**
     * 退出线程请求
     */
    public static final int MESSAGE_WHAT_QUIT = 2;

    /**
     * 日志写入文件系统请求
     */
    public static final int MESSAGE_WHAT_FLUSH = 3;

    /**
     * 数据库管理
     */
    private DbManager mSQLManager = null;

    private Handler mMessageHandler = null;

    /**
     * 当前日志记录的条数
     */
    private int mRecordCount = 0;
    
    /**
     * 日志临时写入目录
     */
    private File mCacheDir;
    
    /**
     * 需要上传的日志的目录
     */
    private File mUploadDir;
    

    public static final int MAX_LOGS_COUNT = 1000;

    /**
     * @param 存放日志缓存的目录
     * @param 存放将要上传日志的目录
     * @param downLatch
     */
    public SQLiteDaoImpl(File cacheDir, File uploadDir, Class<?> classt) {
        super("LogThread");
        mCacheDir=cacheDir;
        mUploadDir=uploadDir;
        mTClass=classt;
        this.start();
    }
    /**
     * 注意此处线程
     * 
     * @see android.os.HandlerThread#onLooperPrepared()
     */
    protected void onLooperPrepared() {
        this.realFlush();
        this.initHandler();
    }

    /**
     * 写入数据
     */
    @Override
    public synchronized void scheduleFlush() {
        try {
            if (mMessageHandler == null) return;
            mMessageHandler.sendEmptyMessage(MESSAGE_WHAT_FLUSH);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 外部通知线程结束
     */
    @Override
    public synchronized void scheduleQuit() {
        try {
            if (mMessageHandler == null) return;
            mMessageHandler.sendEmptyMessage(MESSAGE_WHAT_QUIT);
            mMessageHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 写入日志
     * 
     * @return
     */
    @Override
    public synchronized int scheduleWrite(T bean) {
        try {
            if (mMessageHandler == null||bean==null) return 0;
            Message msgLog = Message.obtain(mMessageHandler);
            msgLog.obj = bean;
            msgLog.what = MESSAGE_WHAT_WRITE;
            mMessageHandler.sendMessage(msgLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void waitFinish(long time) {
        try {
            this.wait(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 注意在run中调用
     */
    private void initHandler() {
        mMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                handleLogMessage(msg);
                super.handleMessage(msg);
            }
        };
    }

    /**
     * //注意所属LogThread线程
     * 
     * @param msg
     */
    private void handleLogMessage(Message msg) {
        if (msg.what == MESSAGE_WHAT_QUIT) {
            realQuit();
        } else if (msg.what == MESSAGE_WHAT_FLUSH) {
            realFlush();
        } else if (msg.what == MESSAGE_WHAT_WRITE) {
            realWrite(msg);
        }
    }

    private void realQuit() {
        try {
            mMessageHandler = null;
            closeDb();
            moveCache();
            this.quitSafely();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void realFlush() {
        try {
            closeDb();
            moveCache();
            resetSqlite();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void realWrite(Message msg) {
        Object bean = msg.obj;
        boolean resetDb = false;
        if (bean == null) return;
        try {
            if (mSQLManager != null) mSQLManager.save(bean);
        } catch (Exception e) {
            e.printStackTrace();
            resetSqlite();
            resetDb = true;
        }
        try {
            if (resetDb && mSQLManager != null) mSQLManager.save(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRecordCount++;
        if (mRecordCount > MAX_LOGS_COUNT) {
            this.realFlush();
        }
    }

    /**
     * 日志数据库中的.db全部移动到日志上传目录。删除"-journal"文件
     */
    private void moveCache() {
        if (mCacheDir != null && mUploadDir != null) {
            File[] fils = mCacheDir.listFiles();
            if (fils == null) return;
            for (int i = 0; i < fils.length; i++) {
                try {
                    if (!fils[i].isFile()) continue;
                    String fileName = fils[i].getName();
                    if (fileName != null && fileName.contains("-journal")) {
                        fils[i].delete();
                        continue;
                    }
                    File dstFile = new File(mUploadDir.getAbsolutePath() + "/" + fils[i].getName());
                    fils[i].renameTo(dstFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void resetSqlite() {
        try {
            closeDb();
            mRecordCount = 0;
            if (!mCacheDir.exists()) mCacheDir.mkdir();
            if (!mUploadDir.exists()) mUploadDir.mkdir();
            String dbName = "log" + System.currentTimeMillis() + ".db";
            DbManager.DaoConfig daoConfig = new DbManager.DaoConfig().setDbName(dbName).setDbDir(mCacheDir).setDbVersion(1)
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

                        }
                    });
            mSQLManager = x.getDb(daoConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果表中不存在日志就是删除数据
     * 
     * @return
     */
    private void closeDb() {
        try {
            if (mSQLManager != null) {
                T log =  (T)mSQLManager.findFirst(mTClass);
                if (log == null) {
                    mSQLManager.dropDb(); // 如果是空数据 就删除数据库
                    mSQLManager.close();
                    //删除空数据文件
                    AndroidUtil.deleteFile(mSQLManager.getDaoConfig().getDbDir(), mSQLManager.getDaoConfig().getDbName());
                } else {
                    mSQLManager.close();
                }
            }
            mSQLManager = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   

}
