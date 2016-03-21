package com.andy.LuFM.network;

import com.squareup.okhttp.ResponseBody;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Url;
import rx.Observable;

/**
 * Normal api for LuFM
 */
public interface NormalGetAPI {
    @GET("recommends/guides/section/{section}")
    Observable<retrofit.Response<ResponseBody>> getRecommendInfo(@Path("section") String section);

    @GET("channellives/{id}")
    Observable<retrofit.Response<ResponseBody>> getLiveChannelInfo(@Path("id") String id);

    @GET("channelondemands/{id}")
    Observable<retrofit.Response<ResponseBody>> getVirtualChannelInfo(@Path("id") String id);

    @GET("podcasters/{id}")
    Observable<retrofit.Response<ResponseBody>> getPodcasterBaseInfo(@Path("id") String id);

    @GET("channelondemands/{id}/programs/order/0/curpage/{page}/pagesize/{pagesize}")
    Observable<retrofit.Response<ResponseBody>> getReloadVirtualProgramsSchedule(@Path("id") String id, @Path("page") String page, @Path("pagesize") String pagesize);

    @GET("channelondemands/{id}/programs/order/0/curpage/{page}/pagesize/{pagesize}")
    Observable<retrofit.Response<ResponseBody>> getVirtualProgramSchedule(@Path("id") String id, @Path("page") String page, @Path("pagesize") String pagesize);


    @GET("mediacenterlist")
    Observable<retrofit.Response<ResponseBody>> getListMediacenter();

    @GET("topics/{id}")
    Observable<retrofit.Response<ResponseBody>> getSpecialTopicChannels(@Path("id") String id);

    @GET("recommends/nowplaying/day/{day}")
    Observable<retrofit.Response<ResponseBody>> getRecommendPlaying(@Path("day") String day);

    @GET
    Observable<retrofit.Response<ResponseBody>> getAddress(@Url String url);
}
