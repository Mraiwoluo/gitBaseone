package com.pvetec.common.dao;

public interface ThreadWriteDao<T> {

    /**
     * 写入数据
     */
    public void scheduleFlush();

    /**
     * 外部通知线程结束
     */
    public void scheduleQuit();
    
    /**
     * 写入日志
     * 
     * @return
     */
    public int scheduleWrite(T t);
    
    /**
     * 等待schedule执行完毕
     * 
     * @return
     */
    public void waitFinish(long time);
    
}
