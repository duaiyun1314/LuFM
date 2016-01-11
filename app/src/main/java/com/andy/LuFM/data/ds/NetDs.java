package com.andy.LuFM.data.ds;

import android.util.Log;

import com.andy.LuFM.network.NormalFactory;
import com.andy.LuFM.network.NormalGetAPI;
import com.andy.LuFM.Utils.NetParse;
import com.andy.LuFM.data.DataCommand;
import com.andy.LuFM.data.IDataOperation;
import com.andy.LuFM.data.IResultRecvHandler;
import com.andy.LuFM.data.RequestType;
import com.andy.LuFM.data.Result;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.Map;

import retrofit.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    public Result doCommand(DataCommand dataCommand, final IResultRecvHandler iResultRecvHandler) {
        try {
            Observable observable = null;
            NormalGetAPI getAPI = NormalFactory.getNbaplus();
            final String type = dataCommand.getType();
            final Map<String, Object> param = dataCommand.getParam();
            if (type.equals(RequestType.DATA_TYPE_GET_RECOMMEND)) {
                String sectionId = (Integer) param.get("sectionId") + "";
                Log.i("Syncc", "type:" + type + " id:" + sectionId);
                observable = getAPI.getRecommendInfo(sectionId);
            } else if (type.equalsIgnoreCase(RequestType.GET_LIVE_CHANNEL_INFO)) {
                String id = param.get("id") + "";
                observable = getAPI.getLiveChannelInfo(id);
            } else if (type.equalsIgnoreCase(RequestType.GET_VIRTUAL_CHANNEL_INFO)) {
                String id = param.get("id") + "";
                observable = getAPI.getVirtualChannelInfo(id);
            } else if (type.equalsIgnoreCase(RequestType.GET_PODCASTER_BASEINFO)) {
                String id = param.get("id") + "";
                observable = getAPI.getPodcasterBaseInfo(id);
            } else if (type.equalsIgnoreCase(RequestType.RELOAD_VIRTUAL_PROGRAMS_SCHEDULE)) {//刷新获得节目单
                observable = getAPI.getReloadVirtualProgramsSchedule(param.get("id") + "", param.get("page") + "", param.get("pagesize") + "");
            } else if (type.equalsIgnoreCase(RequestType.GET_VIRTUAL_PROGRAM_SCHEDULE)) {//获得节目单
                observable = getAPI.getVirtualProgramSchedule(param.get("id") + "", param.get("page") + "", param.get("pagesize") + "");
            } else if (type.equalsIgnoreCase(RequestType.GET_LIST_MEDIACENTER)) {
                observable = getAPI.getListMediacenter();
            } else if (type.equalsIgnoreCase(RequestType.GET_SPECIAL_TOPIC_CHANNELS)) {
                String id = param.get("id") + "";
                observable = getAPI.getSpecialTopicChannels(id);

            } else if (type.equalsIgnoreCase(RequestType.GET_RECOMMEND_PLAYING)) {
                String day = param.get("day") + "";
                observable = getAPI.getRecommendPlaying(day);
            }
            observable.subscribeOn(Schedulers.io())
                    .map(new Func1<Response<ResponseBody>, Result>() {
                        @Override
                        public Result call(retrofit.Response<ResponseBody> responseBodyResponse) {
                            String responseString = null;
                            try {
                                responseString = responseBodyResponse.body().string();
                                Result result = NetParse.getInstance().parseMethod(responseString, type);
                                return result;
                            } catch (IOException e) {
                                e.printStackTrace();
                                Result result = new Result();
                                result.setSuccess(false);
                                return result;
                            }

                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Result>() {
                        @Override
                        public void call(Result result) {
                            iResultRecvHandler.onRecvResult(result, type, param);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.i("Sync", "加载失败");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
