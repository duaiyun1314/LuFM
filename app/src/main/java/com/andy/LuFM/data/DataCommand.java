package com.andy.LuFM.data;

import java.util.Map;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public class DataCommand {
    String type;
    Map<String, Object> param;

    public DataCommand(String type, Map<String, Object> param) {
        this.type = type;
        this.param = param;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
