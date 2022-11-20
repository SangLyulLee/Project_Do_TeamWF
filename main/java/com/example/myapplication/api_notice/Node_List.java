package com.example.myapplication.api_notice;

import android.graphics.drawable.Drawable;

public class Node_List {
    private String arrp;
    private String arrt;
    private String nodeNm;
    private String routeId;
    private String routeNo;

    public String getarrp() {
        return arrp;
    }

    public void setarrp(String arrp) {
        this.arrp = arrp;
    }

    public String getarrt() {
        return arrt;
    }

    public void setarrt(String arrt) {
        this.arrt = arrt;
    }

    public String getrouteNo() { return routeNo; }

    public void setrouteNo(String routeNo) { this.routeNo = routeNo; }

    public String getRouteId() { return routeId; }

    public void setRouteId(String routeId) { this.routeId = routeId; }

    public String getNodeNm() { return nodeNm; }

    public void setNodeNm(String nodeNm) { this.nodeNm = nodeNm; }
}

