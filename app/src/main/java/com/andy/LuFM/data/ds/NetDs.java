package com.andy.LuFM.data.ds;

import com.andy.LuFM.Utils.NetKit;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.IDataOperation;
import com.andy.LuFM.data.IDataRecvHandler;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.andy.LuFM.handler.GsonHttpHandler;

import java.util.Map;

/**
 * Created by wanglu on 15/11/16.
 */
public class NetDs implements IDataOperation {
    private static NetDs instance;

    private NetDs() {

    }

    public synchronized static NetDs getInstance() {
        if (instance == null) {
            instance = new NetDs();
        }
        return instance;
    }

    @Override
    public String dataRequestName() {
        return "Net";
    }

    @Override
    public Result doCommand(DataCommand dataCommand, IDataRecvHandler iDataRecvHandler) {
        String type = dataCommand.getType();
        Map<String, Object> param = dataCommand.getParam();
        if (type.equals(RequestType.DATA_TYPE_GET_RECOMMEND)) {
            NetKit.getInstance().getRecommendInfo(param,(GsonHttpHandler) iDataRecvHandler);
        }
        return null;
    }
}
