package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class IntroLayout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(intent); //인트로 실행 후 IntroActivity 화면으로
                finish();
            }
        }, 2000); //1초 인트로 실행
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}