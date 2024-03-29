package com.example.myapplication.vision;

import static java.lang.Math.abs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.example.myapplication.api_ver.get_api;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class blind_route extends AppCompatActivity {

    String[][] des_station_list = new String[5][4];
    String[][] start_station_list = new String[5][4];
    String[] start_route;

    double longitude = 0, latitude = 0;
    TextToSpeech tts;
    Intent intent;
    SpeechRecognizer mRecognizer;
    RecognitionListener listener;
    final int PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedIntancdState) {
        super.onCreate(savedIntancdState);
        setContentView(R.layout.blind_route);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String speech_str = getIntent().getStringExtra("speech").toString();

        //gps 기반 위치 파악
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(blind_route.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //    String provider = location.getProvider();
            if (location == null) {
                latitude = 34.800774;
                longitude = 126.370871;
            } else {
                longitude = abs(location.getLongitude());
                latitude = location.getLatitude();
                double altitude = location.getAltitude();
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
        }

        // 목적지 좌표 받기
        Context context = this;
        Location des_location = addrToPoint(context, speech_str);

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.RECORD_AUDIO}, PERMISSION);
            }

            // RecognizerIntent 객체 생성
            intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
        }
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() { //tts구현
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) { //tts 잘되면
                    tts.setLanguage(Locale.KOREAN);     //한국어로 설정
                    //tts.setSpeechRate(0.8f); //말하기 속도 지정 1.0이 기본값
                }
            }
        });

/////////////////////////////////////////////////////////////////
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        wakeLock.acquire();
        System.out.println("목적지 : " + speech_str);
        // 도착지 정류장 5개
        boolean desEx = true;
        boolean startEx = true;
        String[] api_split = get_api.getBusStation_ByGps(des_location.getLatitude(), des_location.getLongitude(), 5).split("\n");
        System.out.println("도착 정류장");
        for (int i=0; i<api_split.length; i++) {
            System.out.println("api_split["+i+"] = " + api_split[i]);
        }
        if (api_split.length < 2) {
            System.out.println("목적지 근처에 정류장이 없습니다");
            desEx = false;
            tts.speak("목적지 근처에 정류장이 없습니다. 다시 진행해주세요.", TextToSpeech.QUEUE_ADD, null);
            while (true) {
                if (!tts.isSpeaking()) {
                    break;
                }
            }
            Intent intent = new Intent(blind_route.this, blind_main.class);
            startActivity(intent);
            finish();
        } else {
            int getBusStation_error = 0;
            for (int i = 0; i < api_split.length; i++) {
                String[] api_split2 = api_split[i].split(" ");
                if (api_split2.length < 4) {
                    System.out.println("목적지 정보 오류");
                    getBusStation_error++;
                    continue;
                }
                des_station_list[i][0] = api_split2[0];
                des_station_list[i][1] = api_split2[1];
                des_station_list[i][2] = api_split2[2];
                des_station_list[i][3] = api_split2[3];
            }
            if (getBusStation_error == 5) {
                System.out.println("목적지 정보 오류 발생");
                tts.speak("목적지 정보에 오류가 발생 했습니다. 다시 진행해주세요.", TextToSpeech.QUEUE_ADD, null);
                while (true) {
                    if (!tts.isSpeaking()) {
                        break;
                    }
                }
                Intent intent = new Intent(blind_route.this, blind_main.class);
                startActivity(intent);
                finish();
            }
        }
        // 출발지 정류장 5개
        api_split = get_api.getBusStation_ByGps(latitude, longitude, 2).split("\n");
        System.out.println("출발 정류장 _ latitude : "+ latitude + ", longitude : " + longitude);
        for (int i=0; i<api_split.length; i++) {
            System.out.println("api_split["+i+"] = " + api_split[i]);
        }
        if (api_split.length < 2) {
            System.out.println("출발지 근처에 정류장이 없습니다");
            startEx = false;
            tts.speak("출발지 근처에 정류장이 없습니다. 다시 진행해주세요.", TextToSpeech.QUEUE_ADD, null);
            while (true) {
                if (!tts.isSpeaking()) {
                    break;
                }
            }
            Intent intent = new Intent(blind_route.this, blind_main.class);
            startActivity(intent);
            finish();
        } else {
            for (int i = 0; i < api_split.length; i++) {
                String[] api_split2 = api_split[i].split(" ");
                if (api_split2.length == 1) {
                    System.out.println("API 서버 오류");
                }
                else {
                    start_station_list[i][0] = api_split2[0];
                    start_station_list[i][1] = api_split2[1];
                    start_station_list[i][2] = api_split2[2];
                    start_station_list[i][3] = api_split2[3];
                }
            }
        }

        int fastTime = 0;
        String fastRoute_cityCode = "";
        String fastRoute_nodeId = "";
        String fastRoute_routeId = "";
        String startRoute_nodenm = "";
        String endRoute_nodenm = "";
        String startRoute_nodeId = "";
        String endRoute_nodeId = "";
        // 가까운 정류장 5개 선별하여 자동으로 탐색하려면? , 걸리는 시간 + 도착 시간(지나는 정류장 수*2) 비교하여 가장 빠른 버스
        for (int k = 0; k < 5; k++) {
            // 각 정류장 5개를 지나는 루트들 선별
            System.out.println("루트 선별 중...(" + (k+1) + "/5)");
            if (!(desEx && startEx)) {
                break;
            }
            start_route = get_api.getBusStationRoute(start_station_list[k][0], start_station_list[k][1], "1").split("\n");
            //                                             도시코드            ,         정류소ID
        //    System.out.println("start_route : " + start_route[0]);
            String[][] sRouteInfo = new String[start_route.length][6];
            if (start_route[0].equals("")) {
                System.out.println(k + 1 + "번 출발지에 도착 예정인 버스가 없습니다.");
                continue;
            }
            // [0] : 남은 정류장 수 / [1] : 남은 시간(분) / [2] : nodenm / [3] : routeID / [4] : routeno / [5] : 버스타입
            for (int i = 0; i < start_route.length; i++) {
                String[] sBusSplit = start_route[i].split(" ");
                if (sBusSplit.length == 1) {
                    System.out.println("API 서버 오류");
                }
                else {
                    sRouteInfo[i][0] = sBusSplit[0];
                    sRouteInfo[i][1] = sBusSplit[1];
                    sRouteInfo[i][2] = sBusSplit[2];
                    sRouteInfo[i][3] = sBusSplit[3];
                    sRouteInfo[i][4] = sBusSplit[4];
                    sRouteInfo[i][5] = sBusSplit[5];
                    //    System.out.println("sRouteInfo[" + i + "] : " + sRouteInfo[i][0] + ", " + sRouteInfo[i][1]);
                }
            }

            String[] sBusRoute;
            for (int i = 0; i < sRouteInfo.length; i++) {
                // i 루트
                sBusRoute = get_api.getBusRoute(start_station_list[k][0], sRouteInfo[i][3], "1").split("\n");
            //    System.out.println("sBusRoute.length = " + sBusRoute.length);
            //    System.out.println("sBusRoute0 : " + sBusRoute[0]);
            //    System.out.println("sBusRoute1 : " + sBusRoute[1]);
                int routeNum = 0, sBR_index = 0, eBR_index = 0;
                boolean sNode_route = false;
                boolean eNode_route = false;
                int des_index = 0;
                String[] sBR_node;
                //sBR_node[0] = "";
                //sBR_node[1] = "";
                for (routeNum = 0; routeNum < sBusRoute.length; routeNum++) {       ////////////////////////routeNum<sBusRoute.length
                    sBR_node = sBusRoute[routeNum].split(" ");
                        //System.out.println("sBR_node[0] = "+sBR_node[0]+", sBR_node[1] = "+sBR_node[1]);
                        // 같은 루트 내에 두 정류장이 존재하는가
                        //for (routeNum=0; routeNum < sBusRoute.length; routeNum++) {
                        //    sBR_node = sBusRoute[routeNum].split(" ");
                    if (sBR_node.length == 1) {
                        System.out.println("API 서버 오류");
                    }
                    else {
                        if (sBR_node[4].equals(start_station_list[k][3])) {
                            System.out.println("출발 정류장 존재, " + routeNum + 1);
                            sNode_route = true;
                            sBR_index = routeNum;

                            for (; routeNum < sBusRoute.length; routeNum++) {
                                sBR_node = sBusRoute[routeNum].split(" ");
                                for (des_index = 0; des_index < des_station_list.length; des_index++) {
                                    //    System.out.println("sBR_node[1] = " + sBR_node[1]);
                                    //    System.out.println("des_station_list[" + des_index + "][3] = " + des_station_list[des_index][3]);
                                    if (sBR_node[4].equals(des_station_list[des_index][3])) {
                                        System.out.println("도착 정류장 존재, " + routeNum + 1);
                                        eNode_route = true;
                                        eBR_index = routeNum;
                                        break;
                                    }
                                }
                                if (eNode_route) {
                                    break;
                                }
                            }
                        }
                    }

                    if (sNode_route && eNode_route) {
                        // 루트에서 출발 정류장이 도착 정류장보다 먼저 지나가는가
                        if (sBR_index + 1 < eBR_index + 1) {
                            int routeTime = Integer.parseInt(sRouteInfo[i][1]) + (eBR_index - sBR_index);
                            //          도착남은시간             +    (출발~도착 정류장 수 * 1)
                            if (fastTime == 0) {
                    //            System.out.println("fastTime = 0");
                                fastTime = routeTime;
                                fastRoute_cityCode = start_station_list[k][0];
                                fastRoute_nodeId = start_station_list[k][1];
                                fastRoute_routeId = sRouteInfo[i][3];
                                startRoute_nodenm = start_station_list[k][2];
                                startRoute_nodeId = start_station_list[k][1];
                                endRoute_nodenm = des_station_list[des_index][2];
                                endRoute_nodeId = des_station_list[des_index][1];
                            } else {
                                if (fastTime > routeTime) {
                    //                System.out.println("fastTime > routeTime");
                                    fastTime = routeTime;
                                    fastRoute_cityCode = start_station_list[k][0];
                                    fastRoute_nodeId = start_station_list[k][1];
                                    fastRoute_routeId = sRouteInfo[i][3];
                                    startRoute_nodenm = start_station_list[k][2];
                                    endRoute_nodenm = des_station_list[des_index][2];
                                    endRoute_nodeId = des_station_list[des_index][1];
                                }
                            }
                        }
                    }
                }
            }
        }
       // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        wakeLock.release();
        if (startRoute_nodeId.equals("")) {
            System.out.println("탐색된 루트 없음");
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    tts.speak("탐색된 루트가 없습니다. 다시 진행해주세요.", TextToSpeech.QUEUE_ADD, null);
                    while (true) {
                        if (!tts.isSpeaking()) {
                            break;
                        }
                    }
                    Intent intent = new Intent(blind_route.this, blind_main.class);
                    startActivity(intent);
                    finish();
                }
            };
            timer.schedule(timerTask, 1000);
        }
        if (!fastRoute_cityCode.equals("")) {
            System.out.println("루트 선별 완료");
            //    System.out.println("startRoute_nodenm : " + startRoute_nodenm + ", endRoute_nodenm : " + endRoute_nodenm);
            String fastRouteInfo_arrT = null, fastRouteInfo_Routeno = null;
            String[] fastRouteInfo;
            fastRouteInfo = get_api.getStaionBus(fastRoute_cityCode, fastRoute_routeId, fastRoute_nodeId, "1").split("\n");

            String[] fastRouteData = fastRouteInfo[0].split(" ");
            if (fastRouteData.length == 1) {
                System.out.println("API 서버 오류");
                tts.speak("서버 오류입니다. 다시 신청해주세요.", TextToSpeech.QUEUE_ADD, null);
                while (true) {
                    if (!tts.isSpeaking()) {
                        break;
                    }
                }
                Intent intent = new Intent(blind_route.this, blind_main.class);
                startActivity(intent);
                finish();
            }
            else if (Integer.parseInt(fastRouteData[0]) == 0) {
                if (fastRouteInfo.length == 1) {
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            tts.speak("탐색된 루트가 없습니다. 다시 진행해주세요.", TextToSpeech.QUEUE_ADD, null);
                            while (true) {
                                if (!tts.isSpeaking()) {
                                    break;
                                }
                            }
                            Intent intent = new Intent(blind_route.this, blind_main.class);
                            startActivity(intent);
                            finish();
                        }
                    };
                    timer.schedule(timerTask, 1000);
                }
                else {
                    fastRouteData = fastRouteInfo[1].split(" ");
                }
            }
            else {
                fastRouteInfo_arrT = fastRouteData[0];
                fastRouteInfo_Routeno = fastRouteData[1];

                Intent intent1 = new Intent(blind_route.this, blind_notice.class);
                intent1.putExtra("startRoute_nodenm", startRoute_nodenm);
                intent1.putExtra("endRoute_nodenm", endRoute_nodenm);
                intent1.putExtra("fastRouteInfo[1]", fastRouteInfo_Routeno);
                intent1.putExtra("fastRouteInfo[0]", fastRouteInfo_arrT);
                intent1.putExtra("fastRoute_routeId", fastRoute_routeId);
                intent1.putExtra("startnodeID", fastRoute_nodeId);
                intent1.putExtra("endnodeID", endRoute_nodeId);
                intent1.putExtra("fastRoute_cityCode", fastRoute_cityCode);
                startActivity(intent1);
                finish();
            }
        }
    }
//////////////////////////////////////////////////////////////////////////

    public static Location addrToPoint(Context context, String speech_str) {
        Location location = new Location("");
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(speech_str, 3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null) {
            for (int i = 0; i < addresses.size(); i++) {
                Address lating = addresses.get(i);
                location.setLatitude(lating.getLatitude());
                location.setLongitude(lating.getLongitude());
            }
        }
        return location;
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}

