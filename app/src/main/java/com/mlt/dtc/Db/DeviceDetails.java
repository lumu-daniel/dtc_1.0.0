package com.mlt.dtc.Db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DeviceDetails {

    @PrimaryKey(autoGenerate = true) int keyId;
    @ColumnInfo(name = "MLTDeviceSN") String MLTDeviceSN;
    @ColumnInfo(name = "AdvVersion") String AdvVersion;
    @ColumnInfo(name = "ApkVersion") String ApkVersion;
    @ColumnInfo(name = "DeviceMacAddress") String DeviceMacAddress;
    @ColumnInfo(name = "MainVideoVersion") String MainVideoVersion;
    @ColumnInfo(name = "TopBannerVersion") String TopBannerVersion;

    public String getMLTDeviceSN() {
        return MLTDeviceSN;
    }

    public void setMLTDeviceSN(String MLTDeviceSN) {
        this.MLTDeviceSN = MLTDeviceSN;
    }

    public String getAdvVersion() {
        return AdvVersion;
    }

    public void setAdvVersion(String advVersion) {
        AdvVersion = advVersion;
    }

    public String getApkVersion() {
        return ApkVersion;
    }

    public void setApkVersion(String apkVersion) {
        ApkVersion = apkVersion;
    }

    public String getDeviceMacAddress() {
        return DeviceMacAddress;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        DeviceMacAddress = deviceMacAddress;
    }

    public String getMainVideoVersion() {
        return MainVideoVersion;
    }

    public void setMainVideoVersion(String mainVideoVersion) {
        MainVideoVersion = mainVideoVersion;
    }

    public String getTopBannerVersion() {
        return TopBannerVersion;
    }

    public void setTopBannerVersion(String topBannerVersion) {
        TopBannerVersion = topBannerVersion;
    }
}