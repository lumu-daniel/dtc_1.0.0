package com.mlt.dtc.Db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "device_details")
public class DeviceDetails {

    @PrimaryKey(autoGenerate = true) int keyId;
    @ColumnInfo(name = "MLTDeviceSN") String MLTDeviceSN;
    @ColumnInfo(name = "AdvVersion") String AdvVersion;
    @ColumnInfo(name = "ApkVersion") String ApkVersion;
    @ColumnInfo(name = "DeviceMacAddress") String DeviceMacAddress;
    @ColumnInfo(name = "MainVideoVersion") String MainVideoVersion;
    @ColumnInfo(name = "TopBannerVersion") String TopBannerVersion;
    @ColumnInfo(name = "TBMainImageVersion") String TBMainImageVersion;
    @ColumnInfo(name = "SBMainImageVersion") String SBMainImageVersion;

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

    public String getTBMainImageVersion() {
        return TBMainImageVersion;
    }

    public void setTBMainImageVersion(String TBMainImageVersion) {
        this.TBMainImageVersion = TBMainImageVersion;
    }

    public String getSBMainImageVersion() {
        return SBMainImageVersion;
    }

    public void setSBMainImageVersion(String SBMainImageVersion) {
        this.SBMainImageVersion = SBMainImageVersion;
    }
}