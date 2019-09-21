package com.example.seoulapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;


public class MainActivity extends Activity {

    Activity activity;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void onClickSignup(View view)
    {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }

    public void onClickLogin(View view)
    {
       Intent intent = new Intent(this, Login.class);
       startActivity(intent);
    }

    public void onClickTest(View view)
    {
        Intent intent = new Intent(this, ShopDetaildInfo.class);
        startActivity(intent);
    }
}
