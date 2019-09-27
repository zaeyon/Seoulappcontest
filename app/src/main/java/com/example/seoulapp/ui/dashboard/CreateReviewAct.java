package com.example.seoulapp.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
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

public class CreateReviewAct extends AppCompatActivity {
    InputMethodManager Imm;
    EditText et1;
    EditText et2;
    ImageView imgView;
    LinearLayout layout;

    int GET_GALLERY_IMAGE = 200;

    File ReviewImgStore;
    private File tempFile;
    String fileName;
    String strEmail;

    CognitoCachingCredentialsProvider credentialsProvider;
    AmazonS3 s3;
    TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_view);

        SharedPreferences SP = getApplicationContext().getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = SP.getString("inputId","null"); //현재 이메일 갖고오기

        //공백 클릭하면 키보드 ㅃ
        Imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        et1 = findViewById(R.id.ReviewWriting); //
        et2 = findViewById(R.id.StoreWriting); //
        layout = findViewById(R.id.ReviewLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ReviewToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //mToolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);*/

    /*  ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); //
        actionBar.setHomeAsUpIndicator(R.drawable.backbutton);

        getSupportActionBar().setTitle("Writing");
        //mToolbar.setTitleTextColor(Color.rgb(30,135,188));*/

        s3 = new AmazonS3Client(credentialsProvider);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:0a39045d-9522-4600-a2d2-21adf8805297", // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전
        );
        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        imgView = findViewById(R.id.Insert_img);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            imgView.setImageURI(selectedImageUri);

            Cursor cursor = null;

            try {
                String[] proj = {MediaStore.Images.Media.DATA};

                assert selectedImageUri != null;
                cursor = getContentResolver().query(selectedImageUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                ReviewImgStore = new File(cursor.getString(column_index)); //이미지 저장 공간
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

    }

    public void linearOnClick(View v) {
        Imm.hideSoftInputFromWindow(et1.getWindowToken(), 0);
        Imm.hideSoftInputFromWindow(et2.getWindowToken(), 0);
    } //공백을 넣어줘야겠당...

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backbutton_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        fileName = "1";
        transferUtility = new TransferUtility(s3, getApplicationContext());
        switch (item.getItemId()) {
            case R.id.gotoReview :
                if (ReviewImgStore != null) {
                    TransferObserver observer = transferUtility.upload( //값은 들어감..
                            "com.example.seoulapp/ReviewImage",
                            fileName,
                            ReviewImgStore
                    );
                }
                new JSONTaskReviewupload().execute("http://192.168.43.102/setUploadImg");
                // new JSONTaskDepositReview().execute("http://192.168.43.102/getReviewCount");
                new JSONTaskReviewReturnagain().execute("http://192.168.43.102/UpdateImage");
                new JSONTaskStoryStoreSave().execute("http://192.168.43.102./StoreReviewInfo");

                return true;
            default:
                return false;
        }
    }

    public class JSONTaskReviewupload extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail); //Id
                jsonObject.accumulate("UserImg", ReviewImgStore.getName());//User
                jsonObject.accumulate("UserContent",et1.getText()); //story
                jsonObject.accumulate("StoreName",et2.getText()); //store

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

            class JSONTaskDepositReview extends AsyncTask<String,String,String>{

                @Override
                protected String doInBackground(String... urls) {
                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    int count =0;
                    Log.d("1111111111", String.valueOf(count));

                    fileName = count + "_" + ReviewImgStore.getName(); //UserImg의 이름
                    count++;
                    Log.d("file",fileName);
                    super.onPostExecute(result);
                }
            }
        }
    }


    public class JSONTaskReviewReturnagain extends AsyncTask <String, String, String>{

        @Override
        protected String doInBackground(String... urls) { //update 한단 말이여
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("UserImg", ReviewImgStore.getName());//User

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

        }
    }
    public class JSONTaskStoryStoreSave extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("email", strEmail); //Id
                jsonObject.accumulate("UserImg", ReviewImgStore.getName());//User
                jsonObject.accumulate("UserContent", et1.getText()); //story
                jsonObject.accumulate("StoreName", et2.getText()); //store

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

            class JSONTaskDepositReview extends AsyncTask<String, String, String> {

                @Override
                protected String doInBackground(String... urls) {
                    return null;
                }

                @Override
                protected void onPostExecute(String result) {
                    int count = 0;
                    Log.d("1111111111", String.valueOf(count));

                    fileName = count + "_" + ReviewImgStore.getName(); //UserImg의 이름
                    count++;
                    Log.d("file", fileName);
                    super.onPostExecute(result);
                }
            }
        }
    }

}

