package com.andy.LuFM.network;

import com.andy.LuFM.Utils.Constants;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by Andy.Wang on 2016/1/8.
 */
public class NormalClient {
     NormalGetAPI normalGetAPI;

    NormalClient() {
        Retrofit retrofit0 = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        normalGetAPI = retrofit0.create(NormalGetAPI.class);


    }

    public NormalGetAPI getCilent() {
        return normalGetAPI;
    }
}
