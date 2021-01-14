package com.mlt.dtc.Db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM DeviceDetails")
    List<DeviceDetails> getAll();

    @Query("SELECT * FROM DeviceDetails WHERE MLTDeviceSN LIKE :deviceSerialNo")
    DeviceDetails findDeviceBySerial(String deviceSerialNo);

    @Insert
    void insertAll(DeviceDetails... deviceDeatails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRow(DeviceDetails DeviceDetails);

    @Delete
    void delete(DeviceDetails deviceDetails);

    @Query("DELETE FROM DeviceDetails")
    public void nukeTable();

    @Query("DELETE FROM DeviceDetails WHERE MLTDeviceSN LIKE :serialNo")
    public void deleteBySN(String serialNo);
}
