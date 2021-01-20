package com.mlt.dtc.Db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DeviceDetails.class}, version = 1)
public abstract class DeviceMngtDB extends RoomDatabase {
    public abstract DeviceDao deviceDao();
}
