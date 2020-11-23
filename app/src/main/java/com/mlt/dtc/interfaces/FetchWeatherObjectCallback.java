package com.mlt.dtc.interfaces;

import com.mlt.dtc.model.Response.FetchCurrentWeatherResponse;

import java.util.List;

public
interface FetchWeatherObjectCallback {
    void successful( List<FetchCurrentWeatherResponse> productList );
    void successful( FetchCurrentWeatherResponse fetchCurrentWeatherResponse );
    void failure( String errorMessage );
}
