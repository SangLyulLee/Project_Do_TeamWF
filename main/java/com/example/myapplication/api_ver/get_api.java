package com.example.myapplication.api_ver;

import android.os.StrictMode;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class get_api {
    public static String getBusStation_ByGps(double Lati, double Long, int Nodes) {
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
                if (i == Nodes) {
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

    public static String getBusStationRoute(String citycode, String nodeid, String pageNum) {
        System.out.println("nodeid : "+nodeid);
        String api_url = "http://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList";
        String key = "gyX%2BmkUTfXXJLJB6ZX7T1mvepnVZRipsWQNuRY0ti3%2B%2B9OZnmSYQJ1vp0oR9Lyj7ANcayvrFVGSdcBYGnsjE%2Bw%3D%3D";

        String url = (api_url + "?serviceKey=" + key + "&cityCode=" + citycode + "&nodeId=" + nodeid + "&pageNo="+pageNum+"&numOfRows=10&_type=xml");

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

                        if (tag.equals("arrprevstationcnt")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        else if (tag.equals("arrtime")) {
                            xpp.next();
                            buffer.append(Integer.parseInt(xpp.getText())/60 + " ");
                        }
                        else if (tag.equals("nodenm")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        else if (tag.equals("routeid")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        else if (tag.equals("routeno")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        else if (tag.equals("vehicletp")) {
                            // 버스 타입
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        if (tag.equals("totalCount")) {
                            xpp.next();
                            if (Integer.parseInt(xpp.getText())%10 == 0) {
                                if (Integer.parseInt(pageNum) < Integer.parseInt(xpp.getText())/10) {
                                    buffer.append(get_api.getBusStationRoute(citycode, nodeid, Integer.toString(Integer.parseInt(pageNum)+1)));
                                }
                            }
                            else {
                                if (Integer.parseInt(pageNum) < (Integer.parseInt(xpp.getText()) / 10) + 1) {
                                    buffer.append(get_api.getBusStationRoute(citycode, nodeid, Integer.toString(Integer.parseInt(pageNum) + 1)));
                                }
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

    public static String getStaionBus(String citycode, String routeid, String nodeid, String pageNum) {

        System.out.println("citycode = " + citycode + "\nrouteid = " + routeid + "\nnodeid = " + nodeid);
        String api_url = "http://apis.data.go.kr/1613000/ArvlInfoInqireService/getSttnAcctoSpcifyRouteBusArvlPrearngeInfoList";
        String key = "gyX%2BmkUTfXXJLJB6ZX7T1mvepnVZRipsWQNuRY0ti3%2B%2B9OZnmSYQJ1vp0oR9Lyj7ANcayvrFVGSdcBYGnsjE%2Bw%3D%3D";

        String url = (api_url + "?serviceKey=" + key + "&cityCode=" + citycode + "&nodeId=" + nodeid + "&routeId=" + routeid + "&pageNo="+pageNum+"&numOfRows=10&_type=xml");

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
                        if (tag.equals("totalCount")) {
                            xpp.next();
                            if (Integer.parseInt(xpp.getText())%10 == 0) {
                                if (Integer.parseInt(pageNum) < Integer.parseInt(xpp.getText())/10) {
                                    buffer.append(get_api.getStaionBus(citycode, routeid, nodeid, Integer.toString(Integer.parseInt(pageNum)+1)));
                                }
                            }
                            else {
                                if (Integer.parseInt(pageNum) < (Integer.parseInt(xpp.getText()) / 10) + 1) {
                                    buffer.append(get_api.getStaionBus(citycode, routeid, nodeid, Integer.toString(Integer.parseInt(pageNum)+1)));
                                }
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
        System.out.println("buffer : "+buffer);
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
                        if (tag.equals("gpslati")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        else if (tag.equals("gpslong")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        else if (tag.equals("nodeid")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("nodenm")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("nodeno")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("nodeord")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("updowncd")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        if (tag.equals("totalCount")) {
                            xpp.next();
                            if (Integer.parseInt(xpp.getText())%10 == 0) {
                                if (Integer.parseInt(pageNum) < Integer.parseInt(xpp.getText())/10) {
                                    buffer.append(get_api.getBusRoute(citycode, routeid, Integer.toString(Integer.parseInt(pageNum)+1)));
                                }
                            }
                            else {
                                if (Integer.parseInt(pageNum) < (Integer.parseInt(xpp.getText()) / 10) + 1) {
                                    buffer.append(get_api.getBusRoute(citycode, routeid, Integer.toString(Integer.parseInt(pageNum)+1)));
                                }
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

    public static String getStaionBusData(String citycode, String routeid, String nodeid) {

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
                        } else if (tag.equals("nodenm")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("arrprevstationcnt")) {
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

    public static String getCitycode() {
        String api_url = "http://apis.data.go.kr/1613000/BusRouteInfoInqireService/getCtyCodeList";
        String key = "zHfs9G4Ov6Oa8b8xIEKrSgJlA79ZaKBQdKaGv5kGdHBgA%2Bv%2BEG%2Fq%2F9A7EXT7JrvAmyfkUV7E7mn%2FHSniwdqHTA%3D%3D";

        String url = (api_url + "?serviceKey=" + key + "&_type=xml");

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

                        if (tag.equals("citycode")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("cityname")) {
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

    public static String getBusRouteNoList(String citycode, String routeNo, String pageNo) {
        System.out.println("citycode = " + citycode + "\nrouteno = " + routeNo);
        String api_url = "http://apis.data.go.kr/1613000/BusRouteInfoInqireService/getRouteNoList";
        String key = "zHfs9G4Ov6Oa8b8xIEKrSgJlA79ZaKBQdKaGv5kGdHBgA%2Bv%2BEG%2Fq%2F9A7EXT7JrvAmyfkUV7E7mn%2FHSniwdqHTA%3D%3D";

        String url = (api_url + "?serviceKey=" + key + "&cityCode=" + citycode + "&routeNo=" + routeNo + "&numOfRows=10&pageNo=" + pageNo + "&_type=xml");

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

                        if (tag.equals("routeid")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("routeno")) {
                            xpp.next();
                            String data = xpp.getText();
                            if (routeNo.equals(data)) {
                                buffer.append(xpp.getText() + " ");
                                return buffer.toString();
                            }
                        }
                        if (tag.equals("totalCount")) {
                            xpp.next();
                            if (Integer.parseInt(xpp.getText())%10 == 0) {
                                if (Integer.parseInt(pageNo) < Integer.parseInt(xpp.getText())/10) {
                                    buffer.append(get_api.getBusRouteNoList(citycode, routeNo, Integer.toString(Integer.parseInt(pageNo)+1)));
                                }
                            }
                            else {
                                if (Integer.parseInt(pageNo) < (Integer.parseInt(xpp.getText()) / 10) + 1) {
                                    buffer.append(get_api.getBusRouteNoList(citycode, routeNo, Integer.toString(Integer.parseInt(pageNo)+1)));
                                }
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


    public static String getBusServiceData(String citycode, String routeid, String pageNum) {
        String api_url = "http://apis.data.go.kr/1613000/BusLcInfoInqireService/getRouteAcctoBusLcList";
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
                        if (tag.equals("nodeid")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("nodeord")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("vehicleno")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        if (tag.equals("totalCount")) {
                            xpp.next();
                            if (Integer.parseInt(xpp.getText())%10 == 0) {
                                if (Integer.parseInt(pageNum) < Integer.parseInt(xpp.getText())/10) {
                                    buffer.append(get_api.getBusServiceData(citycode, routeid, Integer.toString(Integer.parseInt(pageNum)+1)));
                                }
                            }
                            else {
                                if (Integer.parseInt(pageNum) < (Integer.parseInt(xpp.getText()) / 10) + 1) {
                                    buffer.append(get_api.getBusServiceData(citycode, routeid, Integer.toString(Integer.parseInt(pageNum)+1)));
                                }
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

    public static String getStationInfo(String citycode, String nodeN, String pageNum) {
        String api_url = "http://apis.data.go.kr/1613000/BusSttnInfoInqireService/getSttnNoList";
        String key = "zHfs9G4Ov6Oa8b8xIEKrSgJlA79ZaKBQdKaGv5kGdHBgA%2Bv%2BEG%2Fq%2F9A7EXT7JrvAmyfkUV7E7mn%2FHSniwdqHTA%3D%3D";

        String url;
        if (isInt(nodeN)) {
            url = (api_url + "?serviceKey=" + key + "&cityCode=" + citycode + "&nodeNo=" + nodeN + "&numOfRows=10&pageNo=" + pageNum + "&_type=xml");
        } else {
            url = (api_url + "?serviceKey=" + key + "&cityCode=" + citycode + "&nodeNm=" + nodeN + "&numOfRows=10&pageNo=" + pageNum + "&_type=xml");
        }

        StringBuilder buffer = new StringBuilder();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String bufferplus;

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
                        if (tag.equals("gpslati")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        else if (tag.equals("gpslong")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        else if (tag.equals("nodeid")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("nodenm")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        } else if (tag.equals("nodeno")) {
                            xpp.next();
                            buffer.append(xpp.getText() + " ");
                        }
                        if (tag.equals("totalCount")) {
                            xpp.next();
                            if (Integer.parseInt(xpp.getText())%10 == 0) {
                                if (Integer.parseInt(pageNum) < Integer.parseInt(xpp.getText())/10) {
                                    buffer.append(get_api.getStationInfo(citycode, nodeN, Integer.toString(Integer.parseInt(pageNum)+1)));
                                }
                            }
                            else {
                                if (Integer.parseInt(pageNum) < (Integer.parseInt(xpp.getText()) / 10) + 1) {
                                    buffer.append(get_api.getStationInfo(citycode, nodeN, Integer.toString(Integer.parseInt(pageNum)+1)));
                                }
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

    public static boolean isInt(String str) {

        try {
            @SuppressWarnings("unused")
            int x = Integer.parseInt(str);
            return true; //String is an Integer
        } catch (NumberFormatException e) {
            return false; //String is not an Integer
        }

    }
}

