package com.example.myapplication.api_ver;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.ListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.api_notice.Node_ArriInfo;
import com.example.myapplication.map.RouteMapActivity;

public class Menu1_api extends AppCompatActivity {
    double longitude = 0, latitude = 0;
    String[] stationInfo, stationList, searchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu1_api);
        // 정류장 (이름 or 번호 or 근처5개) 탐색 후 고르면 Node_ArriInfo로 이동

        EditText editText = (EditText) findViewById(R.id.editText2);
        Button route_btn = (Button) findViewById(R.id.route_btn);
        Button search_btn = (Button) findViewById(R.id.search_btn);
        Button nodeMap_btn = (Button) findViewById(R.id.nodeMap_btn);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Menu1_api.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
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
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
        }

        ListView list = (ListView) findViewById(R.id.listView2_menu2);
        ListAdapter listAdapter = new ListAdapter();
        list.setAdapter(listAdapter);

        String[] api_split = get_api.getBusStation_ByGps(latitude, longitude).split("\n");
        String[] api_split2 = api_split[0].split(" ");

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapter.list_clear();
                for (int i=0; i<api_split.length; i++) {
                    searchInfo = api_split[i].split(" ");
                    listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non), (i+1)+". "+searchInfo[2]+" ("+searchInfo[3]+")", ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                }
                listAdapter.notifyDataSetChanged();

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(Menu1_api.this, Node_ArriInfo.class);
                        intent.putExtra("citycode", api_split2[0]);
                        intent.putExtra("nodeid", searchInfo[1]);
                        startActivity(intent);
                    }
                });
            }
        });

        route_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input_str = editText.getText().toString();
                listAdapter.list_clear();
                if (!input_str.equals("")) {
                    stationInfo = get_api.getStationInfo(api_split2[0], input_str, "1").split("\n");
                    for (int i=0; i<stationInfo.length; i++) {
                        stationList = stationInfo[i].split(" ");
                        listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non), (i+1)+". "+stationList[3]+" ("+stationList[4]+")", ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                    }
                }
                listAdapter.notifyDataSetChanged();

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(Menu1_api.this, Node_ArriInfo.class);
                        intent.putExtra("citycode", api_split2[0]);
                        intent.putExtra("nodeid", stationList[2]);
                        startActivity(intent);
                    }
                });
            }
        });

        nodeMap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMap = new Intent(Menu1_api.this, RouteMapActivity.class);
                intentMap.putExtra("busNum", 1);
                intentMap.putExtra("citycode", api_split2[0]);
                intentMap.putExtra("api_bool", "2");
                startActivity(intentMap);
            }
        });
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
