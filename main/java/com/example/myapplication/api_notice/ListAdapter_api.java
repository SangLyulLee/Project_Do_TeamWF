package com.example.myapplication.api_notice;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.map.Route_list;

import java.util.ArrayList;

public class ListAdapter_api extends BaseAdapter {

    private ArrayList<Node_List> node_list = new ArrayList<>();

    @Override
    public int getCount() {
        return node_list.size();
    }

    @Override
    public Node_List getItem(int i) {
        return node_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        final int finalPosition = position;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item3, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        TextView textView2 = (TextView) convertView.findViewById(R.id.textView2);
        TextView textView3 = (TextView) convertView.findViewById(R.id.textView3);

        textView.setText(node_list.get(finalPosition).getrouteNo());
        textView2.setText(node_list.get(finalPosition).getarrp());
        textView3.setText(node_list.get(finalPosition).getarrt());

        return convertView;
    }

    public void addList(String arrp, String arrt, String nodeNm, String routeId, String routeNo) {
        Node_List nodeList = new Node_List();

        nodeList.setarrp(arrp);
        nodeList.setarrt(arrt);
        nodeList.setNodeNm(nodeNm);
        nodeList.setRouteId(routeId);
        nodeList.setrouteNo(routeNo);

        node_list.add(nodeList);
    }

    public void list_clear() {
        node_list.clear();
    }
}