package com.example.myapplication.map;
import java.lang.String;

public class BusRoute {
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
}

