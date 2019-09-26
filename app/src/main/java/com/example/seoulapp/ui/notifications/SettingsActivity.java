package com.example.seoulapp.ui.notifications;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;

public class SettingsActivity extends AppCompatActivity {

  TextView tvEditProfile;
  ImageView ivRegProduction;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    tvEditProfile = (TextView)findViewById(R.id.tvEditProfile);
    tvEditProfile.setOnClickListener(new goEditProfile());
    ivRegProduction = (ImageView)findViewById(R.id.regProduction);
    ivRegProduction.setOnClickListener(new goRegProduction());
  }

  class goRegProduction implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      Intent intentRegProduction = new Intent(SettingsActivity.this, RegisterProduction.class);
      startActivity(intentRegProduction);
    }

  }

  class goEditProfile implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      Intent intentEditProfile = new Intent(SettingsActivity.this, EditProfileActivity.class);
      startActivity(intentEditProfile);
    }
  }

  void logout() {
    SharedPreferences preferences = getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.clear();
    editor.commit();
    finish();

    Intent intentLogin = new Intent(getApplicationContext(), MainActivity.class);
    intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    startActivity(intentLogin);
  }

  public void showDialog(View view) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("로그아웃 하시겠습니까?");
    builder.setPositiveButton("예",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                logout();
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
              }
            });
    builder.setNegativeButton("아니오",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
              }
            });
    builder.show();
  }
}