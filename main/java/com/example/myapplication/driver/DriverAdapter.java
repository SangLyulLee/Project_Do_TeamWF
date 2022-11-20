package com.example.myapplication.driver;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.map.BusTime;

import java.util.ArrayList;

public class DriverAdapter extends BaseAdapter {

    private ArrayList<BusTime> arrayList = new ArrayList<>();

    @Override
    public int getCount() { return arrayList.size(); }

    @Override
    public BusTime getItem(int i) { return arrayList.get(i); }

    @Override
    public long getItemId(int i) { return 0; }

    public void list_clear() {
        arrayList.clear();
    }

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
        text_t.setText(arrayList.get(finalPosition).getHours()+"시 "+arrayList.get(finalPosition).getMinutes()+"분");

        return convertView;
    }

    public void addList(BusTime bT) {
        BusTime busTime = new BusTime();

        busTime.setHours(bT.getHours());
        busTime.setMinutes(bT.getMinutes());

        arrayList.add(busTime);
    }
}
