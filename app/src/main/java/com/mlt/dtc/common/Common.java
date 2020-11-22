package com.mlt.dtc.common;

import android.os.Environment;

import com.mlt.dtc.utility.Constant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public
class Common {
    public static String path;
    public static File directory;
    public static File file;
    public static  String multimediaPath = "sdcard/"+ Environment.DIRECTORY_DCIM +"/Multimedia/";

    //Button Clicked Event log
    public static void WriteTextInTextFile(File file, String buttonClicked) {

        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(Constant.SEP_NEWLINE + Constant.DateAndTime + "|" + getdateTime() + "|" + " " + Constant.ButtonEventClicked + "|" + buttonClicked);//appends the string to the file
            fw.close();
        } catch (IOException ex) {

        }
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
}
