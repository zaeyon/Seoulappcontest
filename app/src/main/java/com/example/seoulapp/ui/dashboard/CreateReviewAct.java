package com.example.seoulapp.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

    Context context;
    InputMethodManager Imm;
    EditText reviewwriting;
    EditText storewriting;
    ImageView imgView;
    LinearLayout layout;
    TextView id;
    ListView listView;

    int GET_GALLERY_IMAGE = 200;
    int REQUEST_ACT = 1;
    //File ReviewImgStore;
    private File tempFile;
    String fileName;
    String strEmail; //판별용 아이디
    TextView nickname;
    String[] User_Image;
    String[] User_Id;
    String[] User_Content;
    String[] Review_StoreName;
    String[] like;
    String[] allView;
    Fragment list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_view);

        View view = getLayoutInflater().inflate(R.layout.activity_edit_profile, null); //무슨 역할일까

        //공백 누르면 키보드 ㅂㅂ
        Imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        SharedPreferences auto = getApplicationContext().getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        strEmail = auto.getString("inputId", "null");
        id = findViewById(R.id.User_id);
        reviewwriting = findViewById(R.id.ReviewWriting); //
        storewriting = findViewById(R.id.StoreWriting); //
        layout = findViewById(R.id.ReviewLayout);
        imgView = findViewById(R.id.Insert_img); //사용자가 올릴 이미지
        nickname = (TextView) view.findViewById(R.id.cetNickname); //되었음!!!!


        Toolbar toolbar = (Toolbar) findViewById(R.id.ReviewToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //mToolbar = (Toolbar)findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);*/

        getSupportActionBar().setTitle("Writing");
        //mToolbar.setTitleTextColor(Color.rgb(30,135,188));*/
        //  s3 = new AmazonS3Client(credentialsProvider);

        imgView = findViewById(R.id.Insert_img);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        storewriting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storewriting.setHint("상가가 여러 개라면 ,로 구별해주세요!");
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri mImageUri = data.getData();
            //new ReviewImgUpload().setmImageUrl(mImageUri.toString());
            imgView.setImageURI(mImageUri);
        } //이걸 어디서 저장하죠>
    }


    public void linearOnClick(View v) { //공백 누름 시 키보드 닫기는 효과 줌
        Imm.hideSoftInputFromWindow(reviewwriting.getWindowToken(), 0);
        Imm.hideSoftInputFromWindow(storewriting.getWindowToken(), 0);
    } //공백을 넣어줘야겠당...


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.backbutton_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* fileName = "1";
        transferUtility = new TransferUtility(s3, getApplicationContext());*/
        switch (item.getItemId()) {

            case R.id.gotoReview: // 선택하면 firebase에 저장된다!
                String write_Story = reviewwriting.getText().toString();
                String write_Store = storewriting.getText().toString();
                if (write_Store.equals("") || write_Story.equals("")) {
                    Toast.makeText(this, "입력이 덜 되었습니다!", Toast.LENGTH_LONG).show();
                } else {
                    new JSONTaskReviewUpload().execute("http://192.168.43.72:3000/setReviewFile"); //
                }

        }
        return false;
    }

    public class JSONTaskReviewUpload extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Email", strEmail); //Id
                jsonObject.accumulate("UserImg", "a");
                jsonObject.accumulate("UserContent", reviewwriting.getText().toString()); //story
                jsonObject.accumulate("StoreName", storewriting.getText().toString()); //store
                jsonObject.accumulate("UserNickName", nickname.getText().toString()); //nickname
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
            if(result !=null){
                ReviewAdapter adapter = new ReviewAdapter();
                adapter.refresh();
            }
            onBackPressed();
        }
    }
}