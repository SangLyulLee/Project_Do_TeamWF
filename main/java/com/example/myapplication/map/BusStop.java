package com.example.myapplication.map;

public class BusStop {
    private String busStopNum;
    private String busStopName;
    private String iat;
    private String lng;

    public BusStop() { }

    public String getBusStopNum() { return busStopNum; }

    public void setBusStopNum(String busStopNum) { this.busStopNum = busStopNum; }

    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public String getIat() {
        return iat;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}