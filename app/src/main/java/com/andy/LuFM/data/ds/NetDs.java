package com.andy.LuFM.data.ds;

import android.util.Log;
import android.widget.Toast;

import com.andy.LuFM.Utils.Constants;
import com.andy.LuFM.Utils.NetKit;
import com.andy.LuFM.app.PlayApplication;
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

import cz.msebera.android.httpclient.util.NetUtils;
import retrofit.Response;
import roboguice.util.Ln;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 处理网络请求
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
            final String type = dataCommand.getType();
            final Map<String, Object> param = dataCommand.getParam();
            if (!NetKit.isNetworkConnected()) {
                Toast.makeText(PlayApplication.from(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Result result = new Result();
                result.setSuccess(false);
                result.setMessage("网络连接失败");
                iResultRecvHandler.onRecvResult(result, type, param);
            }
            Observable observable = null;
            NormalGetAPI getAPI = NormalFactory.getNbaplus();
            if (type.equals(RequestType.DATA_TYPE_GET_RECOMMEND)) {
                String sectionId = (Integer) param.get("sectionId") + "";
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
            } else if (type.equalsIgnoreCase(RequestType.GET_ADVERTISEMENT_ADDRESS)) {
                observable = getAPI.getAddress(Constants.AD_ADDRESS);
            } else if (type.equalsIgnoreCase(RequestType.GET_LIVE_PROGRAM_SCHEDULE)) {
                String id = param.get("id") + "";
                String day = param.get("day") + "";
                observable = getAPI.getLiveChannelPrograms(id, day);
            } else if (type.equalsIgnoreCase(RequestType.GET_LIST_LIVE_CHANNELS)) {
                String id = param.get("id") + "";
                String attr = param.get("attr") + "";
                String page = param.get("page") + "";
                String pagesize = param.get("pagesize") + "";
                observable = getAPI.getLiveChannels(id, attr, page, pagesize);
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
                            } catch (Exception e) {
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
                            Ln.d(throwable, "加载失败");
                            Result result = new Result();
                            result.setSuccess(false);
                            iResultRecvHandler.onRecvResult(result, type, param);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
