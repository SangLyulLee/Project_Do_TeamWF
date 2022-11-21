package com.example.myapplication.api_ver;

import static java.lang.Math.abs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.ListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.api_notice.Node_ArriInfo;
import com.example.myapplication.map.RouteMapActivity;

public class Menu2_api extends AppCompatActivity {
    private String input_str;
    String[] api_split, api_split2, api_split3, api_split4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu2);

        ListView list = (ListView) findViewById(R.id.listView2_menu2);
        ListAdapter listAdapter = new ListAdapter();
        list.setAdapter(listAdapter);

        String citycode = getIntent().getStringExtra("citycode");

        Button routeMap_btn = (Button) findViewById(R.id.routeMap_btn);
        routeMap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Menu2_api.this, "노선 검색 후 이용해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        EditText edit2 = (EditText) findViewById(R.id.editText2);
        Button route_btn = (Button) findViewById(R.id.route_btn);

        route_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input_str = edit2.getText().toString();
                if (input_str.equals("")) {
                    listAdapter.list_clear();
                    listAdapter.notifyDataSetChanged();
                }
                else {
                    api_split = get_api.getBusRouteNoList(citycode, input_str.toUpperCase(), "1").split("\n");
                    for (int i=0; i<api_split.length; i++) {
                    }
                    if (api_split[api_split.length-1].equals("")) {
                        Toast.makeText(Menu2_api.this, "없는 버스 번호 입니다.", Toast.LENGTH_SHORT).show();
                        listAdapter.list_clear();
                    }
                    else {
                        api_split2 = api_split[api_split.length - 1].split(" ");
                        api_split3 = get_api.getBusRoute(citycode, api_split2[0], "1").split("\n");
                        listAdapter.list_clear();
                        for (int i = 0; i < api_split3.length; i++) {
                            api_split4 = api_split3[i].split(" ");
                            if (api_split4[6].equals("0"))
                                listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd0), Integer.toString(i + 1) + ". " + api_split4[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                            else if (api_split4[6].equals("1"))
                                listAdapter.addList(ContextCompat.getDrawable(getApplicationContext(), R.drawable.updowncd1), Integer.toString(i + 1) + ". " + api_split4[3], ContextCompat.getDrawable(getApplicationContext(), R.drawable.non));
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                }

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        int position = i;
                        if (position == listAdapter.getCount()-1) {
                            Toast.makeText(Menu2_api.this, "마지막 정류장은 선택할 수 없습니다.\n 다시 선택해주세요.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            api_split4 = api_split3[position].split(" ");
                            Intent intent = new Intent(Menu2_api.this, Node_ArriInfo.class);
                            intent.putExtra("nodeid", api_split4[2]);
                            intent.putExtra("snodeord", api_split4[5]);
                            intent.putExtra("citycode", citycode);
                            startActivity(intent);
                        }
                    }
                });

                routeMap_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentMap = new Intent(Menu2_api.this, RouteMapActivity.class);
                        intentMap.putExtra("busNum", 1);
                        intentMap.putExtra("routeid", api_split2[0]);
                        intentMap.putExtra("citycode", citycode);
                        intentMap.putExtra("api_bool", "1");
                        startActivity(intentMap);
                    }
                });
            }
        });
    }
}
