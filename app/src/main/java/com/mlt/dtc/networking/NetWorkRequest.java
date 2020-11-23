package com.mlt.dtc.networking;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public
class NetWorkRequest {
    private static Retrofit retrofit = new Retrofit.Builder().baseUrl("http://dtcwbsvc.networkips.com/ServiceModule/DTCService.svc/")
            .client(new OkHttpClient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
