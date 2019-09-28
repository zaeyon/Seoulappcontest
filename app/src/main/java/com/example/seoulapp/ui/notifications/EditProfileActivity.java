package com.example.seoulapp.ui.notifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.seoulapp.ClearEditText;
import com.example.seoulapp.MainActivity;
import com.example.seoulapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class EditProfileActivity extends AppCompatActivity {

  static String strEmail;

  private final int GET_GALLERY_IMAGE = 200;
  private File tempFile;

  ImageView ivMyProfile;
  ImageView ivCamera;
  TextView nicknameIm;
  TextView nicknamePo;
  ClearEditText cetNickname;
  Button bMyProfileComplete, imgsel;
  String path;

  private Uri filePath;
  String fileName;
  String profileUrl;
  String[] userInfo;
  File f;

  boolean nicknameCheck = false;
  boolean nicknameWrongCheck = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_profile);


    SharedPreferences auto = getApplicationContext().getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
    strEmail = auto.getString("inputId", "null");


    // 와이파이 새로 접속할 때마다 변경
    new JSONTask1().execute("http://192.168.43.102:3000/myProfile");

    ivMyProfile = (ImageView)findViewById(R.id.ivMyProfile);
    bMyProfileComplete = (Button)findViewById(R.id.bMyProfileComplete);
    imgsel = (Button)findViewById(R.id.selimg);

    ivMyProfile.setBackground(new ShapeDrawable(new OvalShape()));
    if (Build.VERSION.SDK_INT >= 21) {
      ivMyProfile.setClipToOutline(true);
    }
    /*
    ivMyProfile.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);

        Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
        fintent.setType("image/jpeg");
        try {
          startActivityForResult(fintent, 100);
        } catch (ActivityNotFoundException e) {
        }
      }
    });
     */

    ivCamera = (ImageView)findViewById(R.id.ivCamera);
    ivCamera.setBackground(new ShapeDrawable(new OvalShape()));
    if (Build.VERSION.SDK_INT >= 21) {
      ivCamera.setClipToOutline(true);
    }

    cetNickname = (ClearEditText)findViewById(R.id.cetNickname);

    nicknamePo = findViewById(R.id.nicknameText);
    nicknameIm = findViewById(R.id.nicknameText2);

    nicknamePo.setVisibility(INVISIBLE);
    nicknameIm.setVisibility(INVISIBLE);

    cetNickname.addTextChangedListener(nicknameTextWatcher);

    bMyProfileComplete.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        uploadFile();

        // 와이파이 새로 접속할 때마다 변경
        new JSONTask2().execute("http://192.168.43.102:3000/setMyProfile");
        Log.d("EditProfileActivity", "JSONTask2 실행");

        uploadFile();

        Intent iSettings = new Intent(EditProfileActivity.this, SettingsActivity.class);
        iSettings.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(iSettings);
      }
    });

    imgsel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);

      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //request코드가 0이고 OK를 선택했고 data에 뭔가가 들어 있다면
    if(requestCode == 0 && resultCode == RESULT_OK){
      filePath = data.getData();
      try {
        //Uri 파일을 Bitmap으로 만들어서 ImageView에 집어 넣는다.
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
        ivMyProfile.setImageBitmap(bitmap);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  //upload the file
  private void uploadFile() {
    //업로드할 파일이 있으면 수행
    if (filePath != null) {
      //업로드 진행 Dialog 보이기
      final ProgressDialog progressDialog = new ProgressDialog(this);
      progressDialog.setTitle("업로드중...");
      progressDialog.show();

      //storage
      FirebaseStorage storage = FirebaseStorage.getInstance();

      //Unique한 파일명을 만들자.
      SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
      Date now = new Date();
      String filename = formatter.format(now) + ".png";
      //storage 주소와 폴더 파일명을 지정해 준다.
      final StorageReference storageRef = storage.getReferenceFromUrl("gs://dong-dong-c7d7e.appspot.com").child("images/" + filename);
      //올라가거라...
      storageRef.putFile(filePath)
              //성공시
              .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  progressDialog.dismiss(); //업로드 진행 Dialog 상자 닫기
                  Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();

                  storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                      Uri downloadUrl = uri;
                      profileUrl = downloadUrl.toString();
                      Log.d("repfileUrl1 : ", profileUrl);
                    }
                  });
                }
              })
              //실패시
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  progressDialog.dismiss();
                  Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                }
              })
              //진행중
              .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                  @SuppressWarnings("VisibleForTests") //이걸 넣어 줘야 아랫줄에 에러가 사라진다. 넌 누구냐?
                          double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                  //dialog에 진행률을 퍼센트로 출력해 준다
                  progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                }
              });
    } else {
      Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
    }

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

      new JSONTask3().execute("http://192.168.43.102:3000/nickname");
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

      new JSONTask3().execute("http://192.168.43.102:3000/nickname");

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
      Glide.with(EditProfileActivity.this).load(userInfo[3]).into(ivMyProfile);
    }
  }

  public class JSONTask2 extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... urls) {
      try {
        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("email", strEmail);
        jsonObject.accumulate("fileUrl", profileUrl);
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
        nicknamePo.setVisibility(VISIBLE);
        nicknameIm.setVisibility(INVISIBLE);
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
          nicknameIm.setVisibility(VISIBLE);
          nicknamePo.setVisibility(INVISIBLE);
          nicknameWrongCheck = true;
          bMyProfileComplete.setEnabled(false);
        }
      }
      else if(result.equals("nicknameNull"))
      {
        nicknameIm.setVisibility(INVISIBLE);
        nicknamePo.setVisibility(INVISIBLE);
      }
    }
  }

  private void setImage() {
    f = tempFile;
    fileName = userInfo[0] + "_" + f.getName();
  }
}