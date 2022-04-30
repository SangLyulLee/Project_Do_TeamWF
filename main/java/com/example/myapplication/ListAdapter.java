package com.example.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.map.Route;
import com.example.myapplication.map.Route_list;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    private ArrayList<Route_list> route_list = new ArrayList<>();

    @Override
    public int getCount() {
        return route_list.size();
    }

    @Override
    public Route_list getItem(int i) {
        return route_list.get(i);
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
            convertView = inflater.inflate(R.layout.list_item2, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView2);
        TextView route_text = (TextView) convertView.findViewById(R.id.textView2);

        imageView.setImageDrawable(route_list.get(finalPosition).getRoute_image());
        route_text.setText(route_list.get(finalPosition).getRoute_text());

        return convertView;
    }

    public void addList(Drawable img, String text) {
        Route_list routeList = new Route_list();

        routeList.setRoute_image(img);
        routeList.setRoute_text(text);

        route_list.add(routeList);
    }
}
