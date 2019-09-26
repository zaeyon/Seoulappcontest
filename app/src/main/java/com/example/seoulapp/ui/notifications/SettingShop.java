package com.example.seoulapp.ui.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.seoulapp.R;

public class SettingShop extends AppCompatActivity {


    ImageView regProductionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_shop);

        regProductionBtn = findViewById(R.id.regProduction);
        regProductionBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){

                Intent productionRegIntent = new Intent(SettingShop.this, RegisterProduction.class);
                startActivity(productionRegIntent);
            }
        });
    }
}
