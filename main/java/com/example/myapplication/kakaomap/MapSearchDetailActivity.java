package com.example.myapplication.kakaomap;

import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class MapSearchDetailActivity extends AppCompatActivity {
    final static String TAG = "MapSearchDetailTAG";

    //xml
    TextView itemCntText1, itemCntText2, itemCntText3, itemCntText4, itemCntText5, itemCntText6, itemCntText7, itemCntText8, itemCntText9;
    TextView ratingScore;
    RatingBar ratingBar;

    //value
    ArrayList<Document> bigMartList = new ArrayList<>(); //대형마트 MT1
    ArrayList<Document> gs24List = new ArrayList<>(); //편의점 CS2
    ArrayList<Document> schoolList = new ArrayList<>(); //학교 SC4
    ArrayList<Document> academyList = new ArrayList<>(); //학원 AC5
    ArrayList<Document> subwayList = new ArrayList<>(); //지하철 SW8
    ArrayList<Document> bankList = new ArrayList<>(); //은행 BK9
    ArrayList<Document> hospitalList = new ArrayList<>(); //병원 HP8
    ArrayList<Document> pharmacyList = new ArrayList<>(); //약국 PM9
    ArrayList<Document> cafeList = new ArrayList<>(); //카페

    private void initView(){
        float itemCnt1 = bigMartList.size();
        float itemCnt2 = gs24List.size();
        float itemCnt3 = schoolList.size();
        float itemCnt4 = academyList.size();
        float itemCnt5 = subwayList.size();
        float itemCnt6 = bankList.size();
        float itemCnt7 = hospitalList.size();
        float itemCnt8 = pharmacyList.size();
        float itemCnt9 = cafeList.size();
        itemCntText1.setText("" +(int) itemCnt1);
        itemCntText2.setText("" +(int) itemCnt2);
        itemCntText3.setText("" +(int) itemCnt3);
        itemCntText4.setText("" +(int) itemCnt4);
        itemCntText5.setText("" +(int) itemCnt5);
        itemCntText6.setText("" +(int) itemCnt6);
        itemCntText7.setText("" +(int) itemCnt7);
        itemCntText8.setText("" +(int) itemCnt8);
        itemCntText9.setText("" +(int) itemCnt9);


        //평균계산 최대 10점
        if(itemCnt1 > 10){
            itemCnt1 = 10;
        }
        if(itemCnt2 > 10){
            itemCnt2 = 10;
        }
        if(itemCnt3 > 10){
            itemCnt3 = 10;
        }
        if(itemCnt4 > 10){
            itemCnt4 = 10;
        }
        if(itemCnt5 > 10){
            itemCnt5 = 10;
        }
        if(itemCnt6 > 10){
            itemCnt6 = 10;
        }
        if(itemCnt7 > 10){
            itemCnt7 = 10;
        }
        if(itemCnt8 > 10){
            itemCnt8 = 10;
        }
        if(itemCnt9 > 10){
            itemCnt9 = 10;
        }
        float averageScore = Math.round((itemCnt1 + itemCnt2 + itemCnt3 + itemCnt4 + itemCnt5 + itemCnt6 + itemCnt7 + itemCnt8 + itemCnt9)/10*10 /10.0 );
        ratingScore.setText(averageScore+"");
        ratingBar.setRating(averageScore/2);
    }

}
