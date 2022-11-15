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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class blind_route extends AppCompatActivity {

    String[] des_station;
    String[][] des_station_list = new String[10][4];
    String[] des_route;

    String[] start_station;
    String[][] start_station_list = new String[10][4];
    String[] start_route;

    String[] total_bus = new String[10];
    String[] total = new String[4];
    String[] bus = new String[3];

    List<String> list = new ArrayList<>();
    int num = 0;
    double longitude = 0, latitude = 0;

    SpeechRecognizer mRecognizer;
    final int PERMISSION = 1;
    int result;

    @Override
    protected void onCreate(Bundle savedIntancdState) {
        super.onCreate(savedIntancdState);
        setContentView(R.layout.blind_route);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() { }
        };
        timer.schedule(timerTask, 1000);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ListView buslist = (ListView) findViewById(R.id.list);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String speech_str = getIntent().getStringExtra("speech").toString();

        //gps 기반 위치 파악
        //테스트용으로 사용한 좌표 나중에 주석 표기 지우기
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(blind_route.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String provider = location.getProvider();
            if (location == null) {
                latitude = 34.800774;
                longitude = 126.370871;
            } else {
                longitude = abs(location.getLongitude());
                latitude = location.getLatitude();
                double altitude = location.getAltitude();
            }
            //String provider = location.getProvider();
            //if (location == null) {
            latitude = 34.800774;
            longitude = 126.370871;
            /*} else {
                longitude = abs(location.getLongitude());
                latitude = location.getLatitude();
                double altitude = location.getAltitude();
            }*/
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
        // 도착지 정류장 5개
        boolean desEx = true;
        boolean startEx = true;
        String[] api_split = get_api.getBusStation_ByGps(des_location.getLatitude(), des_location.getLongitude()).split("\n");
        System.out.println("api_split = " + api_split[0]);
        if (api_split.length < 2) {
            System.out.println("목적지 근처에 정류장이 없습니다");
            desEx = false;
        } else {
            for (int i = 0; i < api_split.length; i++) {
                String[] api_split2 = api_split[i].split(" ");
                des_station_list[i][0] = api_split2[0];
                des_station_list[i][1] = api_split2[1];
                des_station_list[i][2] = api_split2[2];
                des_station_list[i][3] = api_split2[3];
            }
        }
        // 출발지 정류장 5개
        api_split = get_api.getBusStation_ByGps(36.341, 127.39).split("\n");
        if (api_split.length < 2) {
            System.out.println("출발지 근처에 정류장이 없습니다");
            startEx = false;
        } else {
            for (int i = 0; i < api_split.length; i++) {
                String[] api_split2 = api_split[i].split(" ");
                start_station_list[i][0] = api_split2[0];
                start_station_list[i][1] = api_split2[1];
                start_station_list[i][2] = api_split2[2];
                start_station_list[i][3] = api_split2[3];
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
            if (!(desEx && startEx)) {
                break;
            }
            start_route = get_api.getBusStationRoute(start_station_list[k][0], start_station_list[k][1]).split("\n");
            //                                             도시코드            ,         정류소ID
        //    System.out.println("start_route : " + start_route[0]);
            String[][] sRouteInfo = new String[start_route.length][3];
            if (start_route[0].equals("")) {
                System.out.println(k + 1 + "번 출발지에 도착 예정인 버스가 없습니다.");
                continue;
            }
            // [0] : 남은 시간(분) / [1] : routeID
            for (int i = 0; i < start_route.length; i++) {
                String[] sBusSplit = start_route[i].split(" ");
                sRouteInfo[i][0] = sBusSplit[0];
                sRouteInfo[i][1] = sBusSplit[1];
                sRouteInfo[i][2] = sBusSplit[2];
            //    System.out.println("sRouteInfo[" + i + "] : " + sRouteInfo[i][0] + ", " + sRouteInfo[i][1]);
            }

            String[] sBusRoute;
            for (int i = 0; i < sRouteInfo.length; i++) {
                // i 루트
                sBusRoute = get_api.getBusRoute(start_station_list[k][0], sRouteInfo[i][1], "1").split("\n");
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
                    if (sBR_node[0].equals(start_station_list[k][3])) {
                    //    System.out.println("출발 정류장 존재, " + routeNum + 1);
                        sNode_route = true;
                        sBR_index = routeNum;

                        for (; routeNum < sBusRoute.length; routeNum++) {
                            sBR_node = sBusRoute[routeNum].split(" ");
                            for (des_index = 0; des_index < des_station_list.length; des_index++) {
                            //    System.out.println("sBR_node[0] = " + sBR_node[0]);
                            //    System.out.println("des_station_list[" + des_index + "][3] = " + des_station_list[des_index][3]);
                                if (sBR_node[0].equals(des_station_list[des_index][3])) {
                    //                System.out.println("도착 정류장 존재, " + routeNum + 1);
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

                    if (sNode_route && eNode_route) {
                        // 루트에서 출발 정류장이 도착 정류장보다 먼저 지나가는가
                        if (sBR_index + 1 < eBR_index + 1) {
                            int routeTime = Integer.parseInt(sRouteInfo[i][0]) + ((eBR_index - sBR_index) * 2);
                            //          도착남은시간             +    (출발~도착 정류장 수 * 2)
                            if (fastTime == 0) {
                    //            System.out.println("fastTime = 0");
                                fastTime = routeTime;
                                fastRoute_cityCode = start_station_list[k][0];
                                fastRoute_nodeId = start_station_list[k][1];
                                fastRoute_routeId = sRouteInfo[i][1];
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
                                    fastRoute_routeId = sRouteInfo[i][1];
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
    //    System.out.println("탐색된 가장 빠른 루트");
    //    System.out.println("startRoute_nodenm : " + startRoute_nodenm + ", endRoute_nodenm : " + endRoute_nodenm);
        String[] fastRouteInfo;
        fastRouteInfo = get_api.getStaionBus(fastRoute_cityCode, fastRoute_routeId, fastRoute_nodeId).split(" ");
    //    System.out.println("fastRouteInfo : " + fastRouteInfo[0] + ", " + fastRouteInfo[1]);
        // [0] : 남은 시간, [1] : routeno
        Intent intent1 = new Intent(blind_route.this, blind_notice.class);
        intent1.putExtra("startRoute_nodenm", startRoute_nodenm);
        intent1.putExtra("endRoute_nodenm", endRoute_nodenm);
        intent1.putExtra("fastRouteInfo[1]", fastRouteInfo[1]);
        intent1.putExtra("fastRouteInfo[0]", fastRouteInfo[0]);
        intent1.putExtra("fastRoute_routeId", fastRoute_routeId);
        intent1.putExtra("startnodeID", fastRoute_nodeId);
        intent1.putExtra("endnodeID", endRoute_nodeId);
        intent1.putExtra("fastRoute_cityCode", fastRoute_cityCode);
        startActivity(intent1);
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
        //음성인식


        ArrayAdapter<String> adpater = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        buslist.setAdapter(adpater);

        //목적지 입력
        EditText des_text = (EditText) findViewById(R.id.des_text);
        Button des_btn = (Button) findViewById(R.id.des_btn);
        des_text.setText(speech_str);
        des_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adpater.clear();
                //목적지 입력받아온 des로 지도에서 해당 좌표 얻어와주시면 됩니다.
                String des = des_text.getText().toString();

                //좌표 기반 근처 정류소 검색 후 리스트 저장
                //지도에서 받아온 좌표값 lati, long 입력하면 사용 가능
                String[] api_split = get_api.getBusStation_ByGps(34.791213, 126.380686).split("\n");

                for (int i = 0; i < api_split.length; i++) {
                    String[] api_split2 = api_split[i].split(" ");
                    des_station_list[i][0] = api_split2[0];
                    des_station_list[i][1] = api_split2[1];
                    des_station_list[i][2] = api_split2[2];
                    des_station_list[i][3] = api_split2[3];
                }

                //   [0]        [1]       [2]
                //[도시코드]  [도시코드]  [도시코드]
                //[정류소ID]  [정류소ID] [정류소ID]
                //[정류소이름][정류소이름][정류소이름]

                for (int i = 0; i < des_station_list.length; i++) {
                    list.add(des_station_list[i][2] + "\n" + des_station_list[i][3]);
                }
                num = 0;
                adpater.notifyDataSetChanged();
            }
        });

        //출발지 입력
        EditText start_text = (EditText) findViewById(R.id.start_text);
        Button start_btn = (Button) findViewById(R.id.start_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adpater.clear();
                //좌표 기반 근처 정류소 검색 후 리스트 저장
                String[] api_split = get_api.getBusStation_ByGps(latitude, longitude).split("\n");

                if (api_split.length < 2) {
                    list.add("근처 정류장이 없습니다");
                } else {
                    for (int i = 0; i < api_split.length; i++) {
                        String[] api_split2 = api_split[i].split(" ");
                        start_station_list[i][0] = api_split2[0];
                        start_station_list[i][1] = api_split2[1];
                        start_station_list[i][2] = api_split2[2];
                        start_station_list[i][3] = api_split2[3];
                    }
                    for (int i = 0; i < start_station_list.length; i++) {
                        list.add(start_station_list[i][2] + "\n" + start_station_list[i][3]);
                    }
                }
                num = 1;
                adpater.notifyDataSetChanged();
            }
        });

        //목적지, 출발지 리스트 선택 후
        buslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (num == 0) {
                    des_station = new String[10];
                    for (int i = 0; i < 3; i++) {
                        des_station[i] = des_station_list[position][i];
                    }
                } else if(num == 1) {
                    start_station = new String[10];
                    for (int i = 0; i < 3; i++) {
                        start_station[i] = start_station_list[position][i];
                    }
                }else if(num == 3){
                    //bus [버스번호][출발정류소][도착정류소]
                    bus[0] = total[1];
                    bus[1] = start_station[1];
                    bus[2] = des_station[1];

                    for(int i = 0; i < bus.length; i++)
                        System.out.println(bus[i]);
                }
                adpater.clear();
            }
        });

        //버스 찾기
        Button route = (Button) findViewById(R.id.route_btn);
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_route = get_api.getBusStationRoute(start_station[0], start_station[1]).split(" ");

                des_route = get_api.getBusStationRoute(des_station[0], des_station[1]).split(" ");

                for (int i = 0; i < des_route.length; i += 2) {
                    if (Arrays.asList(start_route).contains(des_route[i]) == true) {
                        total_bus = get_api.getStaionBus(start_station[0], start_route[i], start_station[1]).split("\n\n");
                        for(int j = 0; j < total_bus.length; j++){
                            total = total_bus[j].split("\n");
                            list.add(total[0] + "\n"+ total[2] + "\n" + total[3]);

                        }
                    }
                    if(total_bus.length == 0)
                        list.add("경로가 없습니다.");
                }
                num = 3;
                adpater.notifyDataSetChanged();
            }
        });

        Button back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent = new Intent(blind_route.this, blind_main.class);
                startActivity(back_intent);
            }
        });
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

