package com.example.myapplication.api_ver;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class Menu1_api extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ebus_set);
        // 정류장 (이름 or 번호 or 근처5개) 탐색 후 고르면 Node_ArriInfo로 이동

    }
}
