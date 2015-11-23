package com.andy.LuFM.data.ds;

import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.Utils.NetKit;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.IDataOperation;
import com.andy.LuFM.data.IDataRecvHandler;
import com.andy.LuFM.data.IResultRecvHandler;
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
    public Result doCommand(DataCommand dataCommand, IResultRecvHandler iResultRecvHandler) {
        String type = dataCommand.getType();
        Map<String, Object> param = dataCommand.getParam();
        if (type.equals(RequestType.DATA_TYPE_GET_RECOMMEND)) {
            String url = Constants.RECOMMEND_INFO_URL + (Integer) param.get("sectionId");
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler);
        } else if (type.equalsIgnoreCase(RequestType.GET_LIVE_CHANNEL_INFO)) {
            String url = Constants.LIVE_CHANNLE_INFO_URL + param.get("id");
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler);
        } else if (type.equalsIgnoreCase(RequestType.GET_VIRTUAL_CHANNEL_INFO)) {
            String url = Constants.VIRTUAL_CHANNLE_INFO_URL + param.get("id");
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler);
        }
        return null;
    }
}
