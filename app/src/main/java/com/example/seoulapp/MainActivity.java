package com.example.seoulapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}
