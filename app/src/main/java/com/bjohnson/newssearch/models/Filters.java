package com.bjohnson.newssearch.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Filters implements Serializable {
    public String getSortOrder() {
        return sortOrder;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public ArrayList<String> getNewsDesk() {
        return newsDesk;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setNewsDesk(ArrayList<String> newsDesk) {
        this.newsDesk = newsDesk;
    }

    String sortOrder;
    String beginDate;
    ArrayList<String> newsDesk;

    public Filters() {
        sortOrder = "newest";
        beginDate = "";
        newsDesk = new ArrayList<String>();
    }

}
