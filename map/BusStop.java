package com.example.myapplication.map;

public class BusStop {
    private int busStopNum;
    private String busStopName;
    private double iat;
    private double lng;

    public BusStop() { }

    public int getBusStopNum() {
        return busStopNum;
    }

    public void setBusStopNum(int busStopNum) { this.busStopNum = busStopNum; }

    public String getBusStopName() { return busStopName; }

    public void setBusStopName(String busStopName) { this.busStopName = busStopName; }

    public double getIat() { return iat; }

    public void setIat(double iat) { this.iat = iat; }

    public double getLng() { return lng; }

    public void setLng(double lng) { this.lng = lng; }
}