package com.example.seoulapp.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.example.seoulapp.ClearEditText;
import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EditProfileActivity extends AppCompatActivity {

  static String strEmail;

  private final int GET_GALLERY_IMAGE = 200;
  private File tempFile;

  ImageView ivMyProfile;
  ImageView ivCamera;
  TextView nicknameIm;
  TextView nicknamePo;
  ClearEditText cetNickname;
  Button bMyProfileComplete;

  String fileName;
  String[] userInfo;
  File f;

  boolean nicknameCheck = false;
  boolean nicknameWrongCheck = false;

  CognitoCachingCredentialsProvider credentialsProvider;
  AmazonS3 s3;
  TransferUtility transferUtility;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);

    // 와이파이 새로 접속할 때마다 변경
   new JSONTask1().execute("http://172.30.1.10:3000/myProfile");


    SharedPreferences auto = getApplicationContext().getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
    strEmail = auto.getString("inputId", "null");

    ivMyProfile = (ImageView)findViewById(R.id.ivMyProfile);
    ivMyProfile.setBackground(new ShapeDrawable(new OvalShape()));
    if (Build.VERSION.SDK_INT >= 21) {
      ivMyProfile.setClipToOutline(true);
    }
    ivMyProfile.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
      }
    });

    ivCamera = (ImageView)findViewById(R.id.ivCamera);
    ivCamera.setBackground(new ShapeDrawable(new OvalShape()));
    if (Build.VERSION.SDK_INT >= 21) {
      ivCamera.setClipToOutline(true);
    }

    cetNickname = (ClearEditText)findViewById(R.id.cetNickname);

    nicknamePo = findViewById(R.id.nicknameText);
    nicknameIm = findViewById(R.id.nicknameText2);

    nicknamePo.setVisibility(View.INVISIBLE);
    nicknameIm.setVisibility(View.INVISIBLE);

    cetNickname.addTextChangedListener(nicknameTextWatcher);

    bMyProfileComplete = (Button)findViewById(R.id.bMyProfileComplete);
    bMyProfileComplete.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        if (f != null) {
          TransferObserver obsever = transferUtility.upload(
                  "com.example.seoulapp/userProfileImage",
                  fileName,
                  f
          );
        }

        // 와이파이 새로 접속할 때마다 변경

        new JSONTask2().execute("http://172.30.1.10:3000/setMyProfile");

        Log.d("EditProfileActivity", "JSONTask2 실행");
        
        Intent iSettings = new Intent(EditProfileActivity.this, SettingsActivity.class);
        iSettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

  TextWatcher nicknameTextWatcher = new TextWatcher() {
    @Override
    public void afterTextChanged(Editable edit) {

      String s = edit.toString();
      nicknameCheck = true;

      if (!nicknameWrongCheck && s.length() > 0 && nicknameCheck ) {
        bMyProfileComplete.setEnabled(true);
      }
      else if (s.length() <= 0) {
        nicknameCheck = false;
        bMyProfileComplete.setEnabled(false);
      }
      else if(!nicknameCheck)
      {
        bMyProfileComplete.setEnabled(false);
      }
      new JSONTask3().execute("http://172.30.1.10:3000/nickname");

    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

      new JSONTask3().execute("http://172.30.1.10:3000/nickname");
    }
  };

  public class JSONTask1 extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... urls) {
      try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("email", strEmail);

        HttpURLConnection con = null;
        BufferedReader reader = null;

        try {
          URL url = new URL(urls[0]);
          con = (HttpURLConnection) url.openConnection();
          con.setRequestMethod("POST");//POST방식으로 보냄
          con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
          con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
          con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
          con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
          con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
          con.connect();

          //서버로 보내기위해서 스트림 만듬
          OutputStream outStream = con.getOutputStream();
          //버퍼를 생성하고 넣음
          BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
          writer.write(jsonObject.toString());
          writer.flush();
          writer.close();//버퍼를 받아줌

          InputStream stream = con.getInputStream();

          reader = new BufferedReader(new InputStreamReader(stream));

          StringBuffer buffer = new StringBuffer();

          String line = "";
          while ((line = reader.readLine()) != null) {
            buffer.append(line);
          }

          return buffer.toString();
        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (con != null) {
            con.disconnect();
          }
          try {
            if (reader != null) {
              reader.close();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      ivMyProfile = (ImageView)findViewById(R.id.ivMyProfile);
      userInfo = result.split("\\|");
      fileName = userInfo[2];

      cetNickname.setText(userInfo[1]);
      String userProfileURL = "https://s3.ap-northeast-2.amazonaws.com/com.example.seoulapp/userProfileImage/" + fileName;
      Glide.with(EditProfileActivity.this).load(userProfileURL).into(ivMyProfile);
    }
  }

  public class JSONTask2 extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... urls) {
      try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("email", strEmail);
        jsonObject.accumulate("fileName", fileName);
        jsonObject.accumulate("newNickname", cetNickname.getText());

        HttpURLConnection con = null;
        BufferedReader reader = null;

        try {
          URL url = new URL(urls[0]);
          con = (HttpURLConnection) url.openConnection();
          con.setRequestMethod("POST");//POST방식으로 보냄
          con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
          con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
          con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
          con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
          con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
          con.connect();

          //서버로 보내기위해서 스트림 만듬
          OutputStream outStream = con.getOutputStream();
          //버퍼를 생성하고 넣음
          BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
          writer.write(jsonObject.toString());
          writer.flush();
          writer.close();//버퍼를 받아줌

          InputStream stream = con.getInputStream();

          reader = new BufferedReader(new InputStreamReader(stream));

          StringBuffer buffer = new StringBuffer();

          String line = "";
          while ((line = reader.readLine()) != null) {
            buffer.append(line);
          }

          return buffer.toString();
        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (con != null) {
            con.disconnect();
          }
          try {
            if (reader != null) {
              reader.close();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      Toast.makeText(EditProfileActivity.this, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
    }
  }

  public class JSONTask3 extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... urls) {
      try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("nickname", cetNickname.getText());

        HttpURLConnection con = null;
        BufferedReader reader = null;

        try {
          URL url = new URL(urls[0]);
          con = (HttpURLConnection) url.openConnection();
          con.setRequestMethod("POST");//POST방식으로 보냄
          con.setRequestProperty("Cache-Control", "no-cache");//캐시 설정
          con.setRequestProperty("Content-Type", "application/json");//application JSON 형식으로 전송
          con.setRequestProperty("Accept", "text/html");//서버에 response 데이터를 html로 받음
          con.setDoOutput(true);//Outstream으로 post 데이터를 넘겨주겠다는 의미
          con.setDoInput(true);//Inputstream으로 서버로부터 응답을 받겠다는 의미
          con.connect();

          //서버로 보내기위해서 스트림 만듬
          OutputStream outStream = con.getOutputStream();
          //버퍼를 생성하고 넣음
          BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
          writer.write(jsonObject.toString());
          writer.flush();
          writer.close();//버퍼를 받아줌

          InputStream stream = con.getInputStream();

          reader = new BufferedReader(new InputStreamReader(stream));

          StringBuffer buffer = new StringBuffer();

          String line = "";
          while ((line = reader.readLine()) != null) {
            buffer.append(line);
          }

          return buffer.toString();
        } catch (MalformedURLException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
          if (con != null) {
            con.disconnect();
          }
          try {
            if (reader != null) {
              reader.close();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);

      if(result.equals("nicknameSuccess"))
      {
        Log.d("dd", "nicknameSuccess");
        nicknamePo.setVisibility(View.VISIBLE);
        nicknameIm.setVisibility(View.INVISIBLE);
        nicknameWrongCheck = false;
        if (!nicknameWrongCheck && nicknameCheck) {
          bMyProfileComplete.setEnabled(true);
        }
      }
      else if(result.equals("nicknameFail"))
      {
        if (cetNickname.getText().toString().equals(userInfo[1])) {
          nicknameWrongCheck = false;
          bMyProfileComplete.setEnabled(true);
        } else {
          Log.d("dd", "nicknameFail");
          nicknameIm.setVisibility(View.VISIBLE);
          nicknamePo.setVisibility(View.INVISIBLE);
          nicknameWrongCheck = true;
          bMyProfileComplete.setEnabled(false);
        }
      }
      else if(result.equals("nicknameNull"))
      {
        nicknameIm.setVisibility(View.INVISIBLE);
        nicknamePo.setVisibility(View.INVISIBLE);
      }
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

      ivMyProfile = (ImageView)findViewById(R.id.ivMyProfile);
      Uri selectedImageUri = data.getData();
      ivMyProfile.setImageURI(selectedImageUri);
      Log.d("EditProfileActivity", "이미지 등록");

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
    f = tempFile;
    fileName = userInfo[0] + "_" + f.getName();
  }
}