package com.andy.LuFM.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public class DataManager {
    private static DataManager instance;
    private Map<String, IDataOperation> requests = new HashMap<>();

    private DataManager() {

    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void addRequests(IDataOperation operation) {
        requests.put(operation.dataRequestName(), operation);
    }

    /**
     * 主要的获取数据（网络、数据库、本地file）的方法
     *
     * @param requestName 请求类型（net、db、file）
     * @param handler     result回调
     * @param dataCommand 请求命令，包含请求类型的具体指向、相关参数
     * @return result
     */
    public Result getData(String requestName, IResultRecvHandler handler, DataCommand dataCommand) {
        Result result = new Result();
        IDataOperation iDataOperation = requests.get(requestName.trim());
        if (iDataOperation == null) {
            result.setSuccess(false);
        } else {
            result = iDataOperation.doCommand(dataCommand, handler);
        }
        return result;
    }
}
