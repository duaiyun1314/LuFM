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
