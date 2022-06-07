package com.example.myapplication.vision;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class selectedstation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedIntancdState) {
        super.onCreate(savedIntancdState);
        setContentView(R.layout.selectedstation);

        Intent intent = getIntent();
        List<String> des_buslist = new ArrayList<>();
        List<String> start_buslist = new ArrayList<>();

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String get_nodeid = intent.getStringExtra("nodeid");
        String get_nodenm = intent.getStringExtra("nodenm");
        double get_longitude = Double.valueOf(intent.getStringExtra("longitude")).doubleValue();
        double get_latitude = Double.valueOf(intent.getStringExtra("latitude")).doubleValue();

        TextView citycode = (TextView) findViewById(R.id.citycode);
        ListView des_busnodelist = (ListView) findViewById(R.id.bus_list_view);

        citycode.setText(get_nodenm);

        /*String[] split = get_api.getBusInfo(36010, get_nodeid).split("\n");
        if (split.length > 1) {
            for (int i = 0; i < split.length; i++) {
                if(i % 2 ==0)
                des_buslist.add(split[i]);
            }
        }*/
        System.out.println(des_buslist);

        //String[][] start_list = get_api.getBusStation_ByGps(get_latitude, get_longitude);

        System.out.println(start_buslist);
    }
}
