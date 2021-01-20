package com.mlt.dtc.Db;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM device_details")
    Cursor getAll();

    @Query("SELECT * FROM device_details WHERE MLTDeviceSN LIKE :deviceSerialNo")
    DeviceDetails findDeviceBySerial(String deviceSerialNo);

    @Insert
    void insertAll(DeviceDetails... deviceDeatails);

    @Query("UPDATE device_details SET AdvVersion = :Adv  WHERE keyId = 0")
    int upadateAdv(String Adv);

    @Query("UPDATE device_details SET  ApkVersion = :ApkVer  WHERE keyId = 0")
    int upadateApkV(String ApkVer);

    @Query("UPDATE device_details SET MainVideoVersion = :MainVideo WHERE keyId = 0")
    int upadateMainV(String MainVideo);

    @Query("UPDATE device_details SET TopBannerVersion = :TopBannerVers  WHERE keyId = 0")
    int upadateTop(String TopBannerVers);

    @Query("UPDATE device_details SET TBMainImageVersion = :MTopBannerVers  WHERE keyId = 0")
    int upadateMainTop(String MTopBannerVers);

    @Query("UPDATE device_details SET TBMainImageVersion = :SBMainVers  WHERE keyId = 0")
    int upadateSBMain(String SBMainVers);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRow(DeviceDetails DeviceDetails);

    @Delete
    void delete(DeviceDetails deviceDetails);

    @Query("DELETE FROM device_details")
    public void nukeTable();

    @Query("DELETE FROM device_details WHERE MLTDeviceSN LIKE :serialNo")
    public void deleteBySN(String serialNo);
}
