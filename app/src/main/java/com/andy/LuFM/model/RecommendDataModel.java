package com.andy.LuFM.model;

/**
 * Created by wanglu on 15/11/16.
 */
public class RecommendDataModel {
    private int errorno;
    private String errormsg;
    private String data;

    public int getErrorno() {
        return errorno;
    }

    public void setErrorno(int errorno) {
        this.errorno = errorno;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
