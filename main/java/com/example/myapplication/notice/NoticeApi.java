package com.example.myapplication.notice;

public class NoticeApi {
    private String BusNum;
    private String RouteId;
    private String SbusStopNodeId;
    private String EbusStopNodeId;
    private String CityCode;
    private String Uid;
    private int u_type;

    public NoticeApi() {
        BusNum = "1";
        RouteId = "1";
        SbusStopNodeId = "1";
        EbusStopNodeId = "1";
        CityCode = "1";
        Uid = "1";
        u_type = 0;
    }

    public String getBusNum() {
        return BusNum;
    }

    public void setBusNum(String busNum) {
        this.BusNum = busNum;
    }

    public String getRouteId() {
        return RouteId;
    }

    public void setRouteId(String routeId) {
        this.RouteId = routeId;
    }

    public String getSbusStopNodeId() {
        return SbusStopNodeId;
    }

    public void setSbusStopNodeId(String sbusStopNodeId) {
        this.SbusStopNodeId = sbusStopNodeId;
    }

    public String getEbusStopNodeId() {
        return EbusStopNodeId;
    }

    public void setEbusStopNodeId(String ebusStopNodeId) {
        this.EbusStopNodeId = ebusStopNodeId;
    }

    public String getCityCode() { return CityCode; }

    public void setCityCode(String cityCode) { this.CityCode = cityCode; }

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
