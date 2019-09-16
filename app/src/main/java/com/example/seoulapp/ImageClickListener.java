package com.example.seoulapp;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public class ImageClickListener implements View.OnClickListener {

    Context context;

    // imageID는 확대해서 보여줄 이미지의 리소스 ID입니다.
    String shopProduction;

    public ImageClickListener(Context context, String production) {
        this.context = context;
        this.shopProduction = production;
    }

    public void onClick(View v) {

        // 확대된 이미지를 보여주는 액티비티를 실행하기 위해 인텐트 객체를 정의
        // 그리고 이 액티비티에 전달할 imageID값을 이 객체에 저장
        // 인텐트 객체를 정의 후 이 액티비티를 실행.

        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("production", shopProduction);
        context.startActivity(intent);
    }
}
