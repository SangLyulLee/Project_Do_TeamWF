package com.example.myapplication.vision;

import static java.lang.Math.abs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;

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

