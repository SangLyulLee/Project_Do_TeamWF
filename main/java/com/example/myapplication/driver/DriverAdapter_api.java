package com.example.myapplication.driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class DriverAdapter_api extends BaseAdapter {

    private ArrayList<String> citynameList = new ArrayList<>();

    @Override
    public int getCount() { return citynameList.size(); }

    @Override
    public String getItem(int i) { return citynameList.get(i); }

    @Override
    public long getItemId(int i) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        final int finalPosition = position;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.driver_list, parent, false);
        }
        TextView text_n = (TextView) convertView.findViewById(R.id.text_num);
        TextView text_t = (TextView) convertView.findViewById(R.id.text_time);

        text_n.setText(Integer.toString(finalPosition+1));
        text_t.setText(citynameList.get(finalPosition));

        return convertView;
    }

    public void addList(String cityname) {
        citynameList.add(cityname);
    }

    public void clear() {
        citynameList.clear();
    }
}
