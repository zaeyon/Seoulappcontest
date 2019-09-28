package com.example.seoulapp.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seoulapp.R;

public class SettingShop extends AppCompatActivity {

    ImageView ivEditShop;
    ImageView regProductionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_shop);

        ivEditShop = findViewById(R.id.ivEditShop);
        ivEditShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editShop = new Intent(SettingShop.this, EditShop.class);
                startActivity(editShop);
            }
        });
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
