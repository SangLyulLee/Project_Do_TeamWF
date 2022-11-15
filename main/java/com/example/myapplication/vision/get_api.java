package com.example.myapplication.vision;

import android.os.StrictMode;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class get_api {
    public static String getBusStation_ByGps(double Lati, double Long) {

        String[][] list = new String[0][0];
        int i = 0;

        String api_url = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getCrdntPrxmtSttnList";
        String key = "gyX%2BmkUTfXXJLJB6ZX7T1mvepnVZRipsWQNuRY0ti3%2B%2B9OZnmSYQJ1vp0oR9Lyj7ANcayvrFVGSdcBYGnsjE%2Bw%3D%3D";
        String url = (api_url + "?serviceKey=" + key + "&pageNo=1&numOfRows=10&_type=xml&gpsLati=" + Lati + "&gpsLong=" + Long);

        StringBuilder buffer = new StringBuilder();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL uri = new URL(url);

            InputStream is = uri.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;
            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (i == 5) {
                    break;
                }
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if (tag.equals("citycode")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("nodeid")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("nodenm")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }else if (tag.equals("nodeno")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }

                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item")) {
                            buffer.append("\n");
                            i++;
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return buffer.toString();
    }

    public static String getBusStationRoute(String citycode, String nodeid) {

        String[] list = new String[0];
        int i = 0;

        String api_url = "http://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList";
        String key = "gyX%2BmkUTfXXJLJB6ZX7T1mvepnVZRipsWQNuRY0ti3%2B%2B9OZnmSYQJ1vp0oR9Lyj7ANcayvrFVGSdcBYGnsjE%2Bw%3D%3D";

        String url = (api_url + "?serviceKey=" + key + "&cityCode=" + citycode + "&nodeId=" + nodeid + "&pageNo=1&numOfRows=10&_type=xml");

        StringBuilder buffer = new StringBuilder();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL uri = new URL(url);

            InputStream is = uri.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;
            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if (tag.equals("arrtime")) {
                            xpp.next();
                            buffer.append(Integer.parseInt(xpp.getText())/60 + " ");
                        }
                        else if (tag.equals("routeid")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        else if (tag.equals("vehicletp")) {
                            // 버스 타입
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item"))
                            buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return buffer.toString();
    }

    public static String getStaionBus(String citycode, String routeid, String nodeid) {

        String[][] list = new String[0][];

        int i = 0;

        System.out.println("citycode = " + citycode + "\nrouteid = " + routeid + "\nnodeid = " + nodeid);
        String api_url = "http://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoSpcifyRouteBusArvlPrearngeInfoList";
        String key = "gyX%2BmkUTfXXJLJB6ZX7T1mvepnVZRipsWQNuRY0ti3%2B%2B9OZnmSYQJ1vp0oR9Lyj7ANcayvrFVGSdcBYGnsjE%2Bw%3D%3D";

        String url = (api_url + "?serviceKey=" + key + "&cityCode=" + citycode + "&nodeId=" + nodeid + "&routeId=" + routeid + "&pageNo=1&numOfRows=10&_type=xml");

        StringBuilder buffer = new StringBuilder();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL uri = new URL(url);
            InputStream is = uri.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;
            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if (tag.equals("arrtime")) {
                            xpp.next();
                            buffer.append(Integer.parseInt(xpp.getText())/60 + " ");
                        } else if (tag.equals("routeno")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item"))
                            buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return buffer.toString();
    }

    public static String getBusRoute(String citycode, String routeid, String pageNum) {
        String api_url = "http://apis.data.go.kr/1613000/BusRouteInfoInqireService/getRouteAcctoThrghSttnList";
        String key = "zHfs9G4Ov6Oa8b8xIEKrSgJlA79ZaKBQdKaGv5kGdHBgA%2Bv%2BEG%2Fq%2F9A7EXT7JrvAmyfkUV7E7mn%2FHSniwdqHTA%3D%3D";

        String url = (api_url + "?serviceKey=" + key + "&cityCode=" + citycode + "&routeId=" + routeid + "&numOfRows=10&pageNo=" + pageNum + "&_type=xml");

        StringBuilder buffer = new StringBuilder();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL uri = new URL(url);
            InputStream is = uri.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            String tag;
            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();
                        if (tag.equals("nodeno")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("nodeord")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        if (tag.equals("totalCount")) {
                            xpp.next();
                            if (Integer.parseInt(pageNum) < (Integer.parseInt(xpp.getText())/10)+1) {
                                buffer.append(get_api.getBusRoute(citycode, routeid, Integer.toString(Integer.parseInt(pageNum)+1)));
                            }
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item"))
                            buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return buffer.toString();
    }
}

