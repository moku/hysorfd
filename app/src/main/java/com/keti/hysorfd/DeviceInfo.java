package com.keti.hysorfd;

public class DeviceInfo {
    String mId;
    String mBattery;
    String mTemperature;
    String mRssi;

    public DeviceInfo(String id, String battery, String temperature, String rssi){
        mId = id;
        mBattery = battery;
        mTemperature = temperature;
        mRssi = rssi;
    }
}
