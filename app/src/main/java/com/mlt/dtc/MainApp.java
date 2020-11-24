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
import com.mlt.dtc.networking.NetWorkRequest;

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
import static com.mlt.dtc.common.Common.getdateTime;
import static com.mlt.dtc.utility.Constant.TAG;

public
class MainApp extends Application {

    public static String AndroidSerialNo;
    public static boolean  internetCheck;
    private Boolean state;
    private Handler mHandler;
    private int count;
    private Runnable runnable;
    Gson gson = new Gson();



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




}
