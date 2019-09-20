package com.example.seoulapp.ui.notifications;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.seoulapp.ClearEditText;
import com.example.seoulapp.R;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {

  private final int GET_GALLERY_IMAGE = 200;
  private File tempFile;
  ImageView ivMyProfile;
  Button bMyProfileComplete;
  File f;
  ClearEditText cetNickname;

  CognitoCachingCredentialsProvider credentialsProvider;
  AmazonS3 s3;
  TransferUtility transferUtility;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);

    // 와이파이 새로 접속할 때마다 변경
    // new JSONTask().execute("http://192.168.43.102:3000/myProfile");

    ivMyProfile = (ImageView)findViewById(R.id.ivMyProfile);
    ivMyProfile.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
      }
    });

    NotificationsFragment nf = new NotificationsFragment();
    cetNickname = (ClearEditText)findViewById(R.id.cetNickname);
    cetNickname.setText(nf.strNickname);

    bMyProfileComplete = (Button)findViewById(R.id.bMyProfileComplete);
    bMyProfileComplete.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        TransferObserver obsever = transferUtility.upload(
                "com.example.seoulapp/userProfileImage",
                f.getName(),
                f
        );

        Intent iSettings = new Intent(EditProfileActivity.this, SettingsActivity.class);
        startActivity(iSettings);
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

    transferUtility = new TransferUtility(s3, getApplicationContext());
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

      Uri selectedImageUri = data.getData();
      ivMyProfile.setImageURI(selectedImageUri);

      Cursor cursor = null;

      try {
        String[] proj = {MediaStore.Images.Media.DATA};

        assert selectedImageUri != null;
        cursor = getContentResolver().query(selectedImageUri, proj, null, null, null);

        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        tempFile = new File(cursor.getString(column_index));
      } finally {
        if(cursor != null) {
          cursor.close();
        }
      }

      setImage();
    }
  }

  private void setImage() {
    ivMyProfile = (ImageView)findViewById(R.id.ivMyProfile);

    f = tempFile;
  }
}