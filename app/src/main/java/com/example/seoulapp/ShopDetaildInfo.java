package com.example.seoulapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShopDetaildInfo extends Activity implements OnClickListener {

    //레이아웃을 선언하기 위한 레이아웃 변수 선언
    LinearLayout feedPage, QAPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detaild_info);

        // 선언한 변수에 생성한 레이아웃 설정
        feedPage = findViewById(R.id.feedPage);
        QAPage = findViewById(R.id.QAPage);


        // 버튼 선언
        TextView bt1 = findViewById(R.id.button1);
        TextView bt1_non = findViewById(R.id.button1_non);
        bt1_non.setVisibility(View.VISIBLE);
        TextView bt2 = findViewById(R.id.button2);
        TextView bt2_non = findViewById(R.id.button2_non);
        bt2.setVisibility(View.INVISIBLE);

        // 버튼 클릭 이벤트 처리
        bt1_non.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView bt2 = findViewById(R.id.button2);
                TextView bt2_non = findViewById(R.id.button2_non);
                TextView bt1 = findViewById(R.id.button1);
                TextView bt1_non = findViewById(R.id.button1_non);

                bt2.setVisibility(View.INVISIBLE);
                bt2_non.setVisibility(View.VISIBLE);
                bt1.setVisibility(View.VISIBLE);
                bt1_non.setVisibility(View.INVISIBLE);

                feedPage.setVisibility(View.VISIBLE);
                QAPage.setVisibility(View.INVISIBLE);
            }
        });

        bt2_non.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView bt1 = findViewById(R.id.button1);
                TextView bt1_non = findViewById(R.id.button1_non);
                TextView bt2 = findViewById(R.id.button2);
                TextView bt2_non = findViewById(R.id.button2_non);

                bt1.setVisibility(View.INVISIBLE);
                bt1_non.setVisibility(View.VISIBLE);
                bt2.setVisibility(View.VISIBLE);
                bt2_non.setVisibility(View.INVISIBLE);

                feedPage.setVisibility(View.INVISIBLE);
                QAPage.setVisibility(View.VISIBLE);
            }
        });
    }



    @Override
    public void onClick(View arg0){
        // TODO Auto-generated method stub

        // 버튼이 클릭 됐을 때 이벤트 처리
        switch(arg0.getId()) {

            case R.id.button1_non:
                //버튼 1이 눌렸을 때 1개의 해당 레이아웃을 보이게 나머지는 숨기게
                feedPage.setVisibility(View.VISIBLE);
                QAPage.setVisibility(View.INVISIBLE);
                break;

            case R.id.button2_non:

                feedPage.setVisibility(View.GONE);
                QAPage.setVisibility(View.VISIBLE);
                break;
        }
    }
}