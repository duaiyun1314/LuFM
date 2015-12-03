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
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler, param);
        } else if (type.equalsIgnoreCase(RequestType.GET_LIVE_CHANNEL_INFO)) {
            String url = Constants.LIVE_CHANNLE_INFO_URL + param.get("id");
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler, param);
        } else if (type.equalsIgnoreCase(RequestType.GET_VIRTUAL_CHANNEL_INFO)) {
            String url = Constants.VIRTUAL_CHANNLE_INFO_URL + param.get("id");
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler, param);
        } else if (type.equalsIgnoreCase(RequestType.GET_PODCASTER_BASEINFO)) {
            String url = Constants.GET_PODCASTER_BASE_INFO + param.get("id");
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler, param);
        } else if (type.equalsIgnoreCase(RequestType.RELOAD_VIRTUAL_PROGRAMS_SCHEDULE)) {//刷新获得节目单
            String url = Constants.ROOT_URL + "/channelondemands/" + param.get("id") + "/programs/order/0/curpage/" + param.get("page") + "/pagesize/" + param.get("pagesize");
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler, param);
        } else if (type.equalsIgnoreCase(RequestType.GET_VIRTUAL_PROGRAM_SCHEDULE)) {//获得节目单
            String url = Constants.ROOT_URL + "/channelondemands/" + param.get("id") + "/programs/order/0/curpage/" + param.get("page") + "/pagesize/" + param.get("pagesize");
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler, param);
        } else if (type.equalsIgnoreCase(RequestType.GET_LIST_MEDIACENTER)) {
            String url = Constants.ROOT_URL + "/mediacenterlist";
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler, param);
        } else if (type.equalsIgnoreCase(RequestType.GET_SPECIAL_TOPIC_CHANNELS)) {
            String url = Constants.ROOT_URL + "/topics/" + param.get("id");
            NetKit.getInstance().getNormalNetInfo(url, type, iResultRecvHandler, param);

        }
        return null;
    }
}
