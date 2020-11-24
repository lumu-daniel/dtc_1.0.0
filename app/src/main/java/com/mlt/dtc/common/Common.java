package com.mlt.dtc.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.github.infinitebanner.InfiniteBannerView;
import com.google.gson.Gson;
import com.mlt.dtc.R;
import com.mlt.dtc.interfaces.FetchWeatherObjectCallback;
import com.mlt.dtc.model.BottomMenu;
import com.mlt.dtc.model.ClickObject;
import com.mlt.dtc.model.Response.FetchCurrentWeatherResponse;
import com.mlt.dtc.model.SideBannerObject;
import com.mlt.dtc.model.TopBannerObject;
import com.mlt.dtc.networking.NetWorkRequest;
import com.mlt.dtc.utility.Constant;
import com.mlt.dtc.utility.EncryptDecrpt;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.content.ContentValues.TAG;
import static android.os.Looper.getMainLooper;
import static com.mlt.dtc.utility.Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static com.mlt.dtc.utility.Constant.multimediaPath;



public
class Common {

    //Get File path for log
    public static String path;
    public static File file;
    public static File directory;
    private static String result;

    /**
     * topBannerListOfImage
     * @return
     */
    public static ArrayList<TopBannerObject> topBannerList(){
        ArrayList<TopBannerObject> topBannerList=new ArrayList<>();
        try {
            File dir = new File(multimediaPath+"/topbanner/");
            File dirMI = new File(multimediaPath+"/TBMainImage/");
            File[] files = dir.listFiles();
            File[] filesMI = dirMI.listFiles();

            if(files!=null){
                for (File topBannerfile :files){
                    if(topBannerfile.getName().startsWith("topbanner")){
                        for(File MIFile: filesMI){
                            if(MIFile.getName().equals(topBannerfile.getName())){
                                topBannerList.add(new TopBannerObject(topBannerfile,MIFile));
                            }
                        }
                    }

                }
            }

        }catch (Exception e){
            e.printStackTrace();;
        }
        return topBannerList;
    }

    /**
     * sideOfferImage
     * @return
     */
    public static ArrayList<SideBannerObject> sideOfferList(){
        ArrayList<SideBannerObject> sideBannerObjects=new ArrayList<>();
        try {
            File dir = new File(multimediaPath + "/adv/");
            File dirMI = new File(multimediaPath + "/SBMainImage/");
            File[] files = dir.listFiles();
            File[] MIFiles = dirMI.listFiles();
            //fileList = new ArrayList<File>();
            sideBannerObjects.clear();
            for (File file : files) {
                if (file.getName().toLowerCase().startsWith("baner") || file.getName().toUpperCase().startsWith("BANER")) {
                    // it's a match, call your function
                    for (File miFile : MIFiles) {
                        if (miFile.getName().equals(file.getName())) {
                            sideBannerObjects.add(new SideBannerObject(file, miFile));
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();;
        }
        return sideBannerObjects;
    }



    /**
     * left arrow action topBanner
     * @param infiniteBannerView
     */
    public static void leftArrow(InfiniteBannerView infiniteBannerView){
       int tab = infiniteBannerView.getCurrentPosition();
        if (tab > 0) {
            tab--;
            infiniteBannerView.setInitPosition(tab);
        } else if (tab == 0) {
            infiniteBannerView.setInitPosition(tab);
        }
    }

    /**
     * right arrow action topbanner
     * @param infiniteBannerView
     */
    public static void rightArrow(InfiniteBannerView infiniteBannerView){
        int tab = infiniteBannerView.getInitPostion();
        tab++;
        infiniteBannerView.setInitPosition(tab);
    }

    //Button Clicked Event log
    public static void WriteTextInTextFile(File file, String buttonClicked) {

        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(Constant.SEP_NEWLINE + Constant.DateAndTime + "|" + getdateTime() + "|" + " " + Constant.ButtonEventClicked + "|" + buttonClicked);//appends the string to the file
            fw.close();
        } catch (IOException ex) {

        }
    }

    //Get formatted date
    public static String getDate() {
        android.text.format.DateFormat df = new android.text.format.DateFormat();


        return (String) android.text.format.DateFormat.format("dd-MM-yyyy", new Date());
    }


    //Get formatted date time
    public static String getdateTime() {
        android.text.format.DateFormat df = new android.text.format.DateFormat();

        return (String) android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date());
    }

    public static File getFilePath() {
        //Define a path
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + Constant.DirectoryName;
        //Create a Directory
        directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        //Saved data in this text file
        file = new File(path + Constant.FileName);
        return file;
    }

    public static boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Common.showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public static int getRandomNo() {
        //get Randomly generated value
        final int min = 1;
        final int max = 100000000;
        return new Random().nextInt((max - min) + 1) + min;
    }
    public static void showDialog(final String msg, final Context context,
                                  final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                (dialog, which) -> ActivityCompat.requestPermissions((Activity) context,
                        new String[]{permission},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE));
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public static ArrayList<BottomMenu> prepareMenuData() {
        ArrayList<BottomMenu>bottomMenus=new ArrayList<>();

        BottomMenu menu = new BottomMenu(R.drawable.ic_white);
        bottomMenus.add(menu);

        menu = new BottomMenu(R.drawable.rtanew);
        bottomMenus.add(menu);

        menu = new BottomMenu(R.drawable.dtcnew);
        bottomMenus.add(menu);


        return bottomMenus;

//        mAdapter.notifyDataSetChanged();
    }

    //Get list of click logs and save it in cache for the use in the end trip
    public static void getlistofclickLog(Context context, String buttonClick, String dateTime) {
       ArrayList<ClickObject> clickLog = new ArrayList<>();
        try {
//            PreferenceConnector.writeString(getApplicationContext(),Constant.GetListClickLog,clickLog.toString());
//            writeClick(context, Constant.GetListClickLog, clickLog);
            if(!new File("/sdcard/DTC/clickData.txt").exists()){
                clickLog = new ArrayList<>();
            }
            clickLog.add(/*new JSONObject().put(buttonClick, dateTime)*/new ClickObject("1234",buttonClick,dateTime,"2344","0000"));
            writeToFile(new Gson().toJson(clickLog));
        } catch (Exception e) {

        }
    }

    private static void writeToFile(String data){
        try {
            FileOutputStream fos = new  FileOutputStream("/sdcard/DTC/clickData.txt");
            fos.write(data.getBytes());
            fos.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static AlertDialog setAvi(AlertDialog dialog, Activity activity){

        View view = View.inflate(activity, R.layout.custom_loader,null);
        ImageView avi = view.findViewById(R.id.avi);
        Glide.with((AppCompatActivity) activity).asGif().load(R.raw.i_counter_loader).into(avi);
        dialog.setView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.show();
        return dialog;

    }

    public static String getYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        return sdf.format(new Date());
        /*android.text.format.DateFormat df = new android.text.format.DateFormat();
        String format = (String) df.format("yyyy", new Date());
        return format;*/
    }




    public static void TimeoutAlertDialog(Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.error_dialog))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }


    public static void DateTimeRunning(TextView tv_Time) {
        final Handler someHandler = new Handler(getMainLooper());
        someHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DateFormat dateFormat = new SimpleDateFormat("hh.mm:ss aa");
                String dateString = dateFormat.format(new Date()).toString();
                tv_Time.setText(dateString);
//                tv_Time.setText(new SimpleDateFormat("HH:mm:ss a", Locale.US).format(new Date()));
                someHandler.postDelayed(this, 1000);
            }
        }, 10);
    }
    //Get formatted date
    public static String getDateHome() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);


        return sdf.format(new Date());
    }

    public static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        return object;
    }

    public static String getAddressFromLocation(final double latitude, final double longitude,
                                                final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        //result = result; //"Latitude: " + latitude + " Longitude: " + longitude + "\n\nAddress:\n" + result;
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Unable to get address for this lat-long."; //"Latitude: " + latitude + " Longitude: " + longitude +"\n Unable to get address for this lat-long.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };

        thread.start();
        return result;
    }


    //Write Object to Cache
    public static void writeObject(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_WORLD_READABLE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    //Get Time difference from start to end
    public static String GetTimeDifference(String datestart, String dateend) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss",Locale.ENGLISH);
        String taketime = null;
        try {
            Date date1 = simpleDateFormat.parse(datestart);
            Date date2 = simpleDateFormat.parse(dateend);

            taketime = printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return taketime;
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String completeTime = String.format(Locale.ENGLISH,"%d days, %d hours, %d minutes, %d seconds",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        return completeTime;
    }

    public static String getUUID() {
        //get Randomly generated value
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
    //Trip Details log
    public static String DriverName;
    public static String DoubleLine;
    public static String SingleLineorEmpty;

    //Shift Details log
    public static String DoubleLineorTripleLine;
    public static void WriteTextInTextFileForShift(File file, String ShiftId, String ShiftStatus, String GivenName, String FinalName) {
        try {
            FileWriter fw = new FileWriter(file, true); //the true will append the new data
            //if((Helper.isAppRunning(MainActivity.this, "example.dtc")))

            if (ShiftStatus.equals(Constant.SSEventDescriptionKey))
                DoubleLineorTripleLine = Constant.SEP_DOUBLENEWLINE;
            else {
                DoubleLineorTripleLine = Constant.SEP_TRIPLENEWLINE;
            }

            DriverName = GivenName + FinalName;
            fw.write(DoubleLineorTripleLine + Constant.SEP_NEWLINE + Constant.TextShiftStatus
                    + ShiftStatus + Constant.SEP_NEWLINE + Constant.TextShiftID + ShiftId
                    + Constant.SEP_NEWLINE + Constant.TextDriverName + DriverName + Constant.SEP_NEWLINE + Constant.DateAndTime + getdateTime() + Constant.SEP_NEWLINE);//appends the string to the file
            fw.close();
        } catch (IOException ex) {
            Log.e("Tag", ex.getMessage(), ex);
        }
    }

    public static void WriteTextInTextFileForTrip(File file, String TripID, String TripStatus, String GivenName, String FinalName) {
        try {
            FileWriter fw = new FileWriter(file, true); //the true will append the new data
            //if((Helper.isAppRunning(MainActivity.this, "example.dtc")))
            if (TripStatus.equals(Constant.TSEventDescriptionValue)) {
                SingleLineorEmpty = Constant.SEP_NEWLINE;
            } else {
                SingleLineorEmpty = "";
            }

            DriverName = GivenName + FinalName;
            fw.write(DoubleLine + Constant.TripDetails + Constant.SEP_NEWLINE + Constant.TextTripID + TripID
                    + Constant.SEP_NEWLINE + Constant.TextTripStatus
                    + TripStatus + Constant.SEP_NEWLINE + Constant.TextDriverName + DriverName + Constant.SEP_NEWLINE + Constant.DateAndTime + getdateTime() + SingleLineorEmpty);//appends the string to the file
            fw.close();
        } catch (IOException ex) {
            Log.e("Tag", ex.getMessage(), ex);
        }
    }

    public static float DistanceBetween(float parseFloat, float parseFloat1, float parseFloat2, float parseFloat3) {
        float[] result = new float[1];
        Location.distanceBetween(parseFloat, parseFloat1,parseFloat2,parseFloat3, result);
        return result[0]/1000;
    }


    //Get Formatted ExpiryDate RTA     09-2021
    public static String getFormattedExpiryDateRTA(String expiryDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String currentYear = sdf.format(new Date());
        String Century = currentYear.substring(0, 2);
        expiryDate = expiryDate.substring(0, 2) + "-" + Century + expiryDate.substring(2, 4);
        return expiryDate;
    }

    //Get formatted date time with milli seconds
    public static String getdateTimeInMilli() {
        android.text.format.DateFormat df = new android.text.format.DateFormat();

        return (String) android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss:sss", new Date());
    }

    /**
     * Generate 24 Character Random UUID
     *
     * @return
     */
    public static String shortUUID() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz123456789".toCharArray();
        StringBuilder sb = new StringBuilder(24);
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        System.out.println(output);
        return output;
    }

    /**
     * Get the Current date time format
     *
     * @return
     */
    public static String currentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss", Locale.ENGLISH);
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    //Get date time for payment for vehicle and
    public static String getdateTimeUpdatePay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH);
        return sdf.format(new Date());
      /*  android.text.format.DateFormat df = new android.text.format.DateFormat();
        String format = (String) df.format("dd-MM-yyyy HH:mm", new Date());

        return format;*/
    }

    public static String RemoveEscapeSequence(String text) {
        //Removing the escape sequences from tshe string
        String a = text.replaceAll("\r\n", "");
        String b = a.replaceAll("\r", "");
        String c = b.replaceAll("\n", "");
        String d = c.replaceAll("\n\r", "");
        String e = d.replaceAll("\t", "");
        String f = e.replaceAll("\f", "");
        String g = f.replaceAll("\b", "");
        return g;
    }

    public static CountDownTimer SetCountDownTimer(long millisInFuture, long countDownInterval, TextView textView, DialogFragment dialog) {
        // adjust the milli seconds here
        //Set timer in seconds seconds

        return new CountDownTimer(millisInFuture, countDownInterval) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished / 1000);

                int hours = seconds / (60 * 60);
                int tempMint = (seconds - (hours * 60 * 60));
                int minutes = tempMint / 60;
                seconds = tempMint - (minutes * 60);

                //Set timer in seconds seconds
                textView.setText(String.format("%02d Secs", seconds));

            }

            public void onFinish() {
                dialog.dismiss();
            }
        }.start();
    }

    public static String[] MySplit(String original, String separator) {
        Vector<String> nodes = new Vector<>();
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);
        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = nodes.elementAt(loop);
                System.out.println(result[loop]);
            }
        }
        return result;
    }

    public static CountDownTimer SetCountDownTimerDtc(long millisInFuture, long countDownInterval, TextView textView,
                                                      Class aClass, Context context) {
        // adjust the milli seconds here
        //Set timer in seconds seconds

        return new CountDownTimer(millisInFuture, countDownInterval) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                int seconds = (int) (millisUntilFinished / 1000);

                int hours = seconds / (60 * 60);
                int tempMint = (seconds - (hours * 60 * 60));
                int minutes = tempMint / 60;
                seconds = tempMint - (minutes * 60);

                //Set timer in seconds seconds
                textView.setText(String.format(Locale.ENGLISH, "%02d", seconds));

            }

            public void onFinish() {
                Intent intent = new Intent(context,aClass);
                ((AppCompatActivity)context).finish();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }.start();
    }

    //Function to encrypt datetime with the security key
    public static String getencryptedsecureHash(String dateTime, String securityKey) {

        String encryptedsecureHash = null;
        try {
            encryptedsecureHash = EncryptDecrpt.Encrypt(dateTime, securityKey);

        } catch (InvalidKeySpecException e) {


        }
        return encryptedsecureHash;
    }

    public static void ScreenBrightness(Context context, Activity activity) {
        try {
            String br = PreferenceConnector.readString(context, "br", "");
            if (br != null) {
                if (br.equals("1") || br.equals("1.0")) {
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                    params.screenBrightness = Float.parseFloat(br);
                    activity.getWindow().setAttributes(params);
                } else {
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    WindowManager.LayoutParams params = activity.getWindow().getAttributes();
                    //params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                    params.screenBrightness = Float.parseFloat(br);
                    activity.getWindow().setAttributes(params);
                }
            } else {
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
    }

    public static boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }


    /**
     * get Weather List
     * @param body
     * @param context
     * @param fetchWeatherObjectCallback
     */
    public static void getFetchWeatherResponse(String body, Context context, FetchWeatherObjectCallback fetchWeatherObjectCallback) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://dtcwbsvc.networkips.com/ServiceModule/DTCService.svc/")
                .client(new OkHttpClient())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetWorkRequest netWorkRequest = retrofit.create(NetWorkRequest.class);

        Call<FetchCurrentWeatherResponse> categoryProductCall = netWorkRequest.GetFetchWeatherResponse(body);
        categoryProductCall.enqueue(new Callback<FetchCurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<FetchCurrentWeatherResponse> call, Response<FetchCurrentWeatherResponse> response) {
                if (response.isSuccessful()) {
                    fetchWeatherObjectCallback.successful(response.body());
                } else {
                    fetchWeatherObjectCallback.successful(response.body());
                }
            }


            @Override
            public void onFailure(Call<FetchCurrentWeatherResponse> call, Throwable t) {
                String errorMessage = t.getLocalizedMessage();
                if (errorMessage == null || errorMessage.equals("timeout")) {

                } else if (errorMessage.contains("Unable to resolve host")) {

                } else {

                }
            }
        });
    }





}
