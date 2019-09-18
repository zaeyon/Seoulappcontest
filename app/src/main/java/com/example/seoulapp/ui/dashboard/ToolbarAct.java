/*package com.example.seoulapp.ui.dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.seoulapp.R;

public class ToolbarAct extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //toolbar 자체를 xml 파일로 꾸며서 덮는 건 안되나
        setContentView(R.layout.toolbar); //아니 지금 toolbar 설정하는 건데 왜 fragment로 설정해놓냐;;
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar(); //null
        assert actionBar != null;

        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbutton);

        getSupportActionBar().setTitle("Writing");
        //mToolbar.setTitleTextColor(Color.rgb(30,135,188));


    }
}*/
