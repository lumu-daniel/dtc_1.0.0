package com.mlt.e200cp.interfaces;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ClientApi {
    @POST("{methodName}?{}")
    @Headers({"Accept-Charset:utf-8","Content-Type: text/xml"})
    retrofit2.Call<String> getSoapClient(@Path("methodName") String path, @Query("service") String query, @Body RequestBody body);
}
