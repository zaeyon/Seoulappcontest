package com.example.seoulapp.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.seoulapp.R;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {

  private final int GET_GALLERY_IMAGE = 200;
  private File tempFile;
  ImageView ivMyProfile;
  File f;

  CognitoCachingCredentialsProvider credentialsProvider;
  AmazonS3 s3;
  TransferUtility transferUtility;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);

    ivMyProfile = (ImageView)findViewById(R.id.ivMyProfile);
    ivMyProfile.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
      }
    });

    credentialsProvider = new CognitoCachingCredentialsProvider(
            getApplicationContext(),
            "ap-northeast-2:cc1b25cd-c9e7-430f-9666-474b1d523655",
            Regions.AP_NORTHEAST_2
    );
    s3 = new AmazonS3Client(credentialsProvider);
    s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
    s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");
  }
}