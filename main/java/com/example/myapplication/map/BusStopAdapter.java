package com.example.myapplication.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.IntroActivity;
import com.example.myapplication.Menu1;
import com.example.myapplication.R;
import com.example.myapplication.SearchMenu1;

import java.util.ArrayList;

public class BusStopAdapter extends RecyclerView.Adapter<BusStopAdapter.CustomViewHolder>{

    private ArrayList<BusStop> arrayList;
    private Context context;
    private AdapterView.OnItemClickListener itemClickListener;

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                String busstopname2 = arrayList.get(position).getBusStopName();
                String busstopnum2 = arrayList.get(position).getBusStopNum();
                String busstopwd2 = arrayList.get(position).getIat();
                String busstopgd2 = arrayList.get(position).getLng();

                Intent intent =new Intent(v.getContext(),SearchMenu1.class);

                intent.putExtra("busstopname2", busstopname2);
                intent.putExtra("busstopnum2", busstopnum2);
                intent.putExtra("busstopwd2", busstopwd2);
                intent.putExtra("busstopgd2", busstopgd2);

                context.startActivity(intent);
            }
        });
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
        }
    }
}
