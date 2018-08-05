package com.pvetec.common.bean;

public class PageBean {

    /** 当前页 */
    int curPage;

    /** 每页大小 */
    int pageSize=20;

    /** 一共有多少条数 */
    int maxCount;

    /** 最大多少页 */
    int maxPage;

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curpage) {
        this.curPage = curpage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pagesize) {
        this.pageSize = pagesize;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public void setPage(PageBean o) {
        if(o==null) return;
        this.curPage = o.curPage;
        this.pageSize = o.maxCount;
        this.maxCount = o.maxCount;
        this.maxPage = o.maxPage;
    }

}
