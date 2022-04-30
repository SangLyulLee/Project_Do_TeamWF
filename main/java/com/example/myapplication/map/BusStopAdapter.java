package com.example.myapplication.map;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Menu1;
import com.example.myapplication.R;

import java.util.ArrayList;

public class BusStopAdapter extends RecyclerView.Adapter<BusStopAdapter.CustomViewHolder>{

    private ArrayList<BusStop> arrayList;
    private Context context;

    public BusStopAdapter(ArrayList<BusStop> arrayList, Menu1 menu1) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.busstopname.setText(String.valueOf(arrayList.get(position).getBusStopName()));
        holder.busstopnum.setText(String.valueOf(arrayList.get(position).getBusStopNum()));
        holder.busstopwd.setText(String.valueOf(arrayList.get(position).getIat()));
        holder.busstopgd.setText(String.valueOf(arrayList.get(position).getLng()));
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView busstopname;
        TextView busstopnum;
        TextView busstopwd;
        TextView busstopgd;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.busstopname = itemView.findViewById(R.id.busstopname);
            this.busstopnum = itemView.findViewById(R.id.busstopnum);
            this.busstopgd = itemView.findViewById(R.id.busstopgd);
            this.busstopwd = itemView.findViewById(R.id.busstopwd);
        }
    }
}
