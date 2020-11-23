package com.mlt.dtc.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.infinitebanner.InfiniteBannerView;
import com.google.gson.Gson;
import com.mlt.dtc.R;
import com.mlt.dtc.model.BottomMenu;
import com.mlt.dtc.model.ClickObject;
import com.mlt.dtc.model.TopBannerObject;
import com.mlt.dtc.utility.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static com.mlt.dtc.utility.Constant.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static com.mlt.dtc.utility.Constant.multimediaPath;


public
class Common {

    //Get File path for log
    public static String path;
    public static File file;
    public static File directory;

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

    public static String getUUID() {
        //get Randomly generated value
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }


    public static void TimeoutAlertDialog(Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.error_dialog))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }



}
