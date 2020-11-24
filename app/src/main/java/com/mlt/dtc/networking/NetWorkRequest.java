package com.mlt.dtc.networking;

import com.mlt.dtc.model.Response.FetchCurrentWeatherResponse;
import com.mlt.dtc.utility.Constant;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public
interface NetWorkRequest {
    @Headers({
            Constant.CONTENT_TYPE + ": " +  Constant.APPLICATION_JSON
    })
    @POST("fetchCurrentWeatherInfrormation")
    Call<FetchCurrentWeatherResponse> GetFetchWeatherResponse(@Body String body);


}
