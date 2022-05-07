package com.example.myapplication.notice;

public class Notice {
    private String BusNum;
    private String BusTime;
    private String EbusStopNum;
    private String SbusStopNum;
    private String Uid;
    private int u_type;

    public Notice() {
        BusNum = "1";
        BusTime = "1";
        EbusStopNum = "1";
        SbusStopNum = "1";
        Uid = "1";
        u_type = 0;
    }

    public String getBusNum() {
        return BusNum;
    }

    public void setBusNum(String busNum) {
        this.BusNum = busNum;
    }

    public String getBusTime() {
        return BusTime;
    }

    public void setBusTime(String busTime) {
        this.BusTime = busTime;
    }

    public String getEbusStopNum() {
        return EbusStopNum;
    }

    public void setEbusStopNum(String ebusStopNum) {
        this.EbusStopNum = ebusStopNum;
    }

    public String getSbusStopNum() {
        return SbusStopNum;
    }

    public void setSbusStopNum(String sbusStopNum) {
        this.SbusStopNum = sbusStopNum;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }

    public int getU_type() {
        return u_type;
    }

    public void setU_type(int u_type) {
        this.u_type = u_type;
    }
}
