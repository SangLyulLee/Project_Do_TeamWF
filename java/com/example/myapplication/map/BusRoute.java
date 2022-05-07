package com.example.myapplication.map;
import java.lang.String;

public class BusRoute {
    private int busNum;
    private String busRoute[];
    private String timer[];

    public String[] getBusStop() {
        return busRoute;
    }

    public void setBusStop(String[] route) {
        this.busRoute = route;
    }

    public String[] getTimer() {
        return timer;
    }

    public void setTimer(String[] timer) {
        this.timer = timer;
    }

    public int getBusNum() {
        return busNum;
    }

    public void setBusNum(int busNum) {
        busNum = busNum;
    }
}

