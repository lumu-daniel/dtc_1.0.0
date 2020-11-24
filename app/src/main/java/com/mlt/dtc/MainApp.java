package com.mlt.dtc;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mlt.dtc.common.Common;
import com.mlt.dtc.interfaces.FetchWeatherObjectCallback;
import com.mlt.dtc.interfaces.ResultsCallback;
import com.mlt.dtc.model.request.AuthenticateRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.mlt.dtc.activity.MainActivity.reloadPage;

public
class MainApp extends Application {

    public static String AndroidSerialNo;
    public static boolean  internetCheck;
    private Boolean state;
    private Handler mHandler;
    private int count;
    private Runnable runnable;
    Gson gson = new Gson();
    private String JSON_RESULT = "";


    private static Retrofit retrofit = new Retrofit.Builder().baseUrl("http://dtcwbsvc.networkips.com/ServiceModule/DTCService.svc/")
            .client(new OkHttpClient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                isNetworkConnected(new ResultsCallback() {
                    @Override
                    public String onResponseSuccess(String data) {
                        internetCheck =true;
//                        sendDetails();
                        mHandler.postDelayed(runnable,3600000);
                        return null;
                    }

                    @Override
                    public String onResponseFailure(String t) {
                        mHandler.postDelayed(runnable,3600000);
                        return null;
                    }
                });
            }
        };
        AndroidSerialNo = android.os.Build.SERIAL;
//        getDevicetoken = FirebaseInstanceId.getInstance().getToken();
        requestForPermissions();


    }


    private void requestForPermissions() {
        Dexter.withContext(getApplicationContext())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Log.e("TAG", "onPermissionsChecked: Granted" );
                            mHandler.post(runnable);
                            reloadPage(getApplicationContext());

                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Log.e("TAG", "onPermissionsChecked: Denied" );
                            mHandler.post(runnable);
                            reloadPage(getApplicationContext());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void isNetworkConnected(ResultsCallback callback) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Boolean networkState = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
        if (!networkState.booleanValue()) {
            callback.onResponseFailure("");
        } else {
            state = false;
            try {
                new Thread(()->{
                    URL url = null;
                    try {
                        url = new URL("http://www.google.com");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.connect();
                        if (connection.getResponseCode() != HttpURLConnection.HTTP_ACCEPTED) {

                            callback.onResponseSuccess("");
                            connection.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e1) {
                e1.printStackTrace();
                callback.onResponseFailure("");
            }
        }
    }
//
//    public static void getFetchWeatherResponse( String vendorId, Context context, FetchWeatherObjectCallback fetchWeatherObjectCallback ){
//        Call<List<FetchCurrentWeatherResponse>> categoryProductCall = marketPlaceNetworkRequest.GetVendorProducts( vendorId );
//        categoryProductCall.enqueue(new Callback<List<FetchCurrentWeatherResponse>>() {
//            @Override
//            public void onResponse(Call<List<FetchCurrentWeatherResponse>> call, Response<List<FetchCurrentWeatherResponse>> response) {
//                if( response.isSuccessful() ){
//                    productObjectCallback.successful( response.body() );
//                }else{
//                    productObjectCallback.failure( response.message() );
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<FetchCurrentWeatherResponse>> call, Throwable t) {
//                String errorMessage = t.getLocalizedMessage();
//                if( errorMessage == null || errorMessage.equals("timeout") ) {
//                    productObjectCallback.failure( context.getString( R.string.network_error_message ) );
//                }else if( errorMessage.contains("Unable to resolve host") ) {
//                    productObjectCallback.failure( context.getString( R.string.network_error_message ) );
//                }else{
//                    productObjectCallback.failure( errorMessage );
//                }
//            }
//        });
//    }
//


    public void getweatherUpdate(String functionname) {

        String DateTime = Common.getdateTime();
        AuthenticateRequest authenticateRequest = new AuthenticateRequest();
        authenticateRequest.setUsername("nips_inventory");
        authenticateRequest.setPassword("nips@2016");
        authenticateRequest.setSecureHash(Common.getencryptedsecureHash(DateTime, "B15m1L2ah"));
        authenticateRequest.setTimestamp(DateTime);

        gson = new GsonBuilder().create();
        final JSONObject req = new JSONObject();
        try {
            req.put("username", authenticateRequest.getUsername());
            req.put("password", authenticateRequest.getPassword());
            req.put("timestamp", authenticateRequest.getTimestamp());
            req.put("secureHash", authenticateRequest.getSecureHash());

        } catch (JSONException e) {

        }

        final JSONObject object = new JSONObject();
        try {
            object.put("request", req);

        } catch (JSONException e) {

        }
        JSON_RESULT = functionname;


//        ServiceWrapper.serviceCall(NetworkURL.URL + functionname, object, context, new ServiceCallback() {//NetworkURL.URL
//            @Override
//            public void onSuccess(JSONObject obj) throws JSONException {
//                FetchWeatherResponse res = gson.fromJson(obj.getJSONObject(JSON_RESULT + "Result").toString(), FetchWeatherResponse.class);
//                try {
//                    //Method call to get weather details
//                    fetchweather = res.response;
//
//                    for (int i = 0; i < fetchweather.size(); i++) {
//                        weatherDetailsList = new FetchWeatherResponse.FetchWeather();
//                        weatherDetailsList.setTemperature(Math.round(fetchweather.get(i).getTemperature()));
//                        weatherDetailsList.setWeather(fetchweather.get(i).getWeather());
//                        weatherDetailsList.setCity(fetchweather.get(i).getCity());
//                        weatherDetailsList.setHumidity(Math.round(fetchweather.get(i).getHumidity()));
//                        weatherDetailsList.setCountry(fetchweather.get(i).getCountry());
//                        weatherDetailsList.setTempMax(Math.round(fetchweather.get(i).getTempMax()));
//                        weatherDetailsList.setTempMin(Math.round(fetchweather.get(i).getTempMin()));
//                        weatherDetailsList.setWindSpeed(Math.round(fetchweather.get(i).getWindSpeed()));
//                        //weatherDetailsList.setWeatherImage(hmweatherimagedayForecast.get(fetchweather.get(i).getWeather()));
//                        weatherDetailsListviewAList.add(weatherDetailsList);
//                    }
////                    getweatherForecastUpdate("fetchWeatherForecast", 2039);
////                    setWeatherbyCity(0);
//
//                    view.successful(weatherDetailsListviewAList,0);
//
//                } catch (Exception e) {
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(String obj) {
//
//            }
//        });
    }
}
