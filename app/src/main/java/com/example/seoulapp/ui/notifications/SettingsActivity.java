package com.example.seoulapp.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.seoulapp.R;

public class SettingsActivity extends AppCompatActivity {
  TextView tvEditProfile;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    tvEditProfile = (TextView)findViewById(R.id.tvEditProfile);
    tvEditProfile.setOnClickListener(new goEditProfile());
  }

  class goEditProfile implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      Intent intentEditProfile = new Intent(SettingsActivity.this, EditProfileActivity.class);
      startActivity(intentEditProfile);
    }
  }
}