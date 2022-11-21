package com.example.myapplication.api_notice;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.api_ver.get_api;

import java.util.ArrayList;

public class Node_ArriInfo extends AppCompatActivity {
    boolean search = false;
    ArrayList<String> vehiclenoList = new ArrayList<>();
    int a = 0;
    int b = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.node_arriinfo);

        String citycode = getIntent().getStringExtra("citycode");
        String nodeId = getIntent().getStringExtra("nodeid");

        ListView list = (ListView) findViewById(R.id.nodeArriInfo_list);
        ListAdapter_api listAdapter_api = new ListAdapter_api();
        listAdapter_api.list_clear();
        list.setAdapter(listAdapter_api);

        int ve_pos = 0;
        String[] api_split = get_api.getBusStationRoute(citycode, nodeId, "1").split("\n");
        for (int i=0; i<api_split.length; i++) {
            String[] api_split2 = api_split[i].split(" ");
            if (api_split2.length != 1) {
                if (api_split2[5].equals("저상버스")) {
                    listAdapter_api.addList(api_split2[0], api_split2[1], api_split2[2], api_split2[3], api_split2[4]);

                    String[] routeData = get_api.getBusRoute(citycode, api_split2[3], "1").split("\n");
                    for (int k=0; k<routeData.length; k++) {
                        String[] routeData_list = routeData[k].split(" ");
                        if (routeData_list[2].equals(nodeId)) {
                            String snodeord = routeData_list[5];
                            String[] busServiceData = get_api.getBusServiceData(citycode, api_split2[3], "1").split("\n");
                            for (int j = 0; j < busServiceData.length; j++) {
                                // nodeid, nodeord, 차량번호
                                String[] busSD_List = busServiceData[j].split(" ");
                                if (Integer.parseInt(snodeord) < (Integer.parseInt(busSD_List[1]) + Integer.parseInt(api_split2[0])))
                                    a = -(Integer.parseInt(snodeord) - (Integer.parseInt(busSD_List[1]) + Integer.parseInt(api_split2[0])));
                                else
                                    a = Integer.parseInt(snodeord) - (Integer.parseInt(busSD_List[1]) + Integer.parseInt(api_split2[0]));
                                if (a < b) {
                                    if (vehiclenoList.size() == ve_pos) {
                                        vehiclenoList.add(busSD_List[2]);
                                    }
                                    else {
                                        vehiclenoList.set(ve_pos, busSD_List[2]);
                                    }
                                    b = a;
                                }
                            }
                            ve_pos++;
                            b = 999;
                            search = true;
                            break;
                        }
                    }
                }
            }
        }
        for (int i=0; i<vehiclenoList.size(); i++) {
            System.out.println("veNo["+i+"] = " + vehiclenoList.get(i));
        }

        if (!search) {
            listAdapter_api.addList("저상 버스가", "없습니다.", "", "", "운행 중인");
        }
        else {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Node_List node_list = listAdapter_api.getItem(i);
                    String veNo = vehiclenoList.get(i);

                    if (node_list.getarrp().equals("1")) {
                        Toast.makeText(Node_ArriInfo.this, "전 정류장을 지난 버스는 알림 신청이 불가능합니다.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(Node_ArriInfo.this);
                        dlg.setTitle("알림 신청 확인");

                        dlg.setMessage("탑승 정류장 : "+node_list.getNodeNm()+"\n버스 번호 : "+node_list.getrouteNo()+"\n입력하신 정보가 맞습니까?");
                        dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Node_ArriInfo.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Node_ArriInfo.this, EbusSet_api.class);
                                intent.putExtra("citycode", citycode);
                                intent.putExtra("nodeid", nodeId);
                                intent.putExtra("routeid", node_list.getRouteId());
                                intent.putExtra("routeno", node_list.getrouteNo());
                                System.out.println("veno = "+veNo);
                                intent.putExtra("veNo", veNo);
                                startActivity(intent);
                            }
                        });
                        dlg.show();
                    }
                }
            });
        }
    }
}
