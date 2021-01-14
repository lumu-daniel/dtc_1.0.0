package com.mlt.dtc;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.room.Room;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mlt.dtc.Db.DeviceMngtDB;
import com.mlt.dtc.interfaces.FireBaseNotifiers;
import com.mlt.dtc.interfaces.ResultsCallback;
import com.mlt.dtc.model.PushDetails;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import static com.mlt.dtc.activity.MainActivity.reloadPage;

public
class MainApp extends Application   {

    public static String AndroidSerialNo;
    public static MainApp mainApp;
    public static boolean  internetCheck;
    private Boolean state;
    private Handler mHandler;
    public static FireBaseNotifiers mainNotifier;
    public static PushDetails pushDetails;
    private Runnable runnable;
    public static DeviceMngtDB db;

    @Override
    public void onCreate() {
        super.onCreate();
        mainApp = this;
        MultiDex.install(this);
        mHandler = new Handler();
        pushDetails = new PushDetails();
        requestForPermissions();
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
                            db = Room.databaseBuilder(getApplicationContext(), DeviceMngtDB.class,"DeviceMngtDb")
                                    .allowMainThreadQueries()
                                    .fallbackToDestructiveMigration()
                                    .build();
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
