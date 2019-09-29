package com.example.seoulapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.seoulapp.ui.notifications.RegisterProduction;
import com.example.seoulapp.ui.notifications.SettingShop;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class ShopDetaildInfo extends AppCompatActivity implements OnClickListener {

    //레이아웃을 선언하기 위한 레이아웃 변수 선언
    LinearLayout feedPage, QAPage, QnADetailPage, noAnswerPage, yesAnswerPage, regAnswerPage;
    TextView shop_name;
    String[] shopProduction;
    String[] shopProductionURL;
    QnAListViewAdapter adapter;
    static ListView QnAListView;
    int shopNumber;
    String[] QnAInfo;

    TextView QnAAnswerHagi;
    TextView QnADetailTitle;
    TextView QnADetailContent;
    TextView QnADetailQuestioner;
    TextView QnADetailAnswer;
    TextView QnADetailShopName;
    TextView QnAListShowBtn;
    TextView QnAAnswerRegBtn;
    TextView QnADetailProduction;
    TextView qnaAnswerRegister;
    String[] favoriteShop;
    EditText qnaAnswerEdit;
    String preUserEmail;

    ImageView QuestionHagi;
    ImageView canStar;
    ImageView binStar;
    ImageView shop_profile;
    ImageView proReg;
    ImageView yesAnswer;
    ImageView noAnswer;
    private int img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detaild_info);

        // 와이파이 새로 접속할때마다 변경
        new JSONTaskHostEmail().execute("http://192.168.43.72:3000/getHostEmail");
        new JSONTask2().execute("http://192.168.43.72:3000/shopNumber");
        new JSONTaskProduction().execute("http://192.168.43.72:3000/getShopProduction");
        new JSONTaskQnA().execute("http://192.168.43.72:3000/getQnAInfo");
        new JSONTaskFavoriteShop().execute("http://192.168.43.72:3000/getFavoriteCheck");




        // 선언한 변수에 생성한 레이아웃 설정

        ArrayList<QnAListViewItem> QnAListViewItemList = new ArrayList<QnAListViewItem>();

        // 버튼 클릭 이벤트 처리
        Intent intent = getIntent();

        shop_profile = findViewById(R.id.shop_profile);
        // ImageView shop_rep1 = findViewById(R.id.product_image1);
        // ImageView shop_rep2 = findViewById(R.id.product_image2);
        // ImageView shop_rep3 = findViewById(R.id.product_image3);
        shop_name = findViewById(R.id.shop_name);
        TextView shop_building = findViewById(R.id.shop_building);
        TextView shop_floor = findViewById(R.id.shop_floor);
        TextView shop_rocation = findViewById(R.id.shop_rocation);
        TextView shop_category = findViewById(R.id.shop_category);
        TextView shop_style = findViewById(R.id.shop_style);
        TextView shop_intro = findViewById(R.id.shop_intro);
        final ImageView QNA_btn = findViewById(R.id.QA_btn);
        final ImageView production_btn = findViewById(R.id.production_btn);
        qnaAnswerRegister = findViewById(R.id.qnaAnswerRegister);
        qnaAnswerRegister.setVisibility(INVISIBLE);


        qnaAnswerEdit = findViewById(R.id.qnaAnswerEdit);
        canStar = findViewById(R.id.canStar);
        binStar = findViewById(R.id.binStar);
        proReg = findViewById(R.id.pro_reg_btn);
        proReg.setVisibility(INVISIBLE);
        noAnswerPage = findViewById(R.id.noAnswerPage);
        yesAnswerPage = findViewById(R.id.yesAnswerPage);
        yesAnswer = findViewById(R.id.answerYes);
        noAnswer = findViewById(R.id.answerNo);
        QnAAnswerHagi = findViewById(R.id.register_answer);
        regAnswerPage = findViewById(R.id.regAnswerPage);
        regAnswerPage.setVisibility(View.INVISIBLE);

        QnADetailTitle = findViewById(R.id.qnaTitle);
        QnADetailContent = findViewById(R.id.qnaContent);
        QnADetailQuestioner = findViewById(R.id.qnaQuestioner);
        QnADetailAnswer = findViewById(R.id.qnaAnswer);
        QnADetailShopName = findViewById(R.id.qnashopName);
        QnAListShowBtn = findViewById(R.id.qnaListShow);
        QnADetailProduction = findViewById(R.id.qnaProduction);

        feedPage = findViewById(R.id.feedPage);
        QAPage = findViewById(R.id.QAPage);
        QnAListView = findViewById(R.id.QAList);

        QnADetailPage = findViewById(R.id.QnaDetailPage);
        QAPage.setVisibility(INVISIBLE);
        QnADetailPage.setVisibility(INVISIBLE);
        production_btn.setVisibility(INVISIBLE);
        QuestionHagi = findViewById(R.id.QA_hagi_btn);
        canStar.setVisibility(View.INVISIBLE);

        //img=Integer.parseInt(intent.getStringExtra("profileImage"));
        //shop_profile.setImageResource(img);
        Glide.with(getApplicationContext())
                .load(intent.getStringExtra("profileImage"))
                .apply(RequestOptions.circleCropTransform())
                .into(shop_profile);
        //Glide.with(getApplicationContext()).load(intent.getStringExtra("representation1")).into(shop_rep1);
        //Glide.with(getApplicationContext()).load(intent.getStringExtra("representation2")).into(shop_rep2);
        //Glide.with(getApplicationContext()).load(intent.getStringExtra("representation3")).into(shop_rep3);
        shop_name.setText(intent.getStringExtra("name"));
        shop_building.setText(intent.getStringExtra("building"));
        shop_floor.setText(intent.getStringExtra("floor"));
        shop_rocation.setText(intent.getStringExtra("rocation"));
        shop_category.setText(intent.getStringExtra("category"));
        shop_style.setText(intent.getStringExtra("style"));
        shop_intro.setText(intent.getStringExtra("intro"));


        SharedPreferences auto = this.getSharedPreferences(MainActivity.name, Context.MODE_PRIVATE);
        preUserEmail = auto.getString("inputId", "null");

        QnAAnswerHagi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                regAnswerPage.setVisibility(VISIBLE);
                noAnswerPage.setVisibility(INVISIBLE);
                yesAnswerPage.setVisibility(INVISIBLE);
                qnaAnswerRegister.setVisibility(VISIBLE);

            }
        });




        binStar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ShopDetaildInfo.this , "매장이 즐겨찾기에 추가되었습니다.", Toast.LENGTH_SHORT).show();

                binStar.setVisibility(INVISIBLE);
                canStar.setVisibility(VISIBLE);

                new JSONTaskFavoriteShop().execute("http://192.168.43.72:3000/insertFavoriteShop");

            }
        });

        canStar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view){

                Toast.makeText(ShopDetaildInfo.this , "매장이 즐겨찾기에서 제외되었습니다.", Toast.LENGTH_SHORT).show();

                binStar.setVisibility(VISIBLE);
                canStar.setVisibility(INVISIBLE);

                new JSONTaskFavoriteShop().execute("http://192.168.43.72:3000/deleteFavoriteShop");

            }
        });

        shop_profile.setBackground(new ShapeDrawable(new OvalShape()));
        if (Build.VERSION.SDK_INT >= 21) {
            shop_profile.setClipToOutline(true);
        }

        QNA_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                QAPage.setVisibility(VISIBLE);
                feedPage.setVisibility(INVISIBLE);

                QNA_btn.setVisibility(INVISIBLE);
                production_btn.setVisibility(VISIBLE);


            }
        });

        production_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                QAPage.setVisibility(INVISIBLE);
                feedPage.setVisibility(VISIBLE);

                QNA_btn.setVisibility(VISIBLE);
                production_btn.setVisibility(INVISIBLE);


            }
        });

        QnAListShowBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                QnADetailPage.setVisibility(INVISIBLE);
                feedPage.setVisibility(INVISIBLE);
                QAPage.setVisibility(VISIBLE);

                qnaAnswerRegister.setVisibility(INVISIBLE);

            }
        });

     QnAListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

             qnaAnswerRegister.setVisibility(INVISIBLE);

             QnAListViewItem item = (QnAListViewItem)parent.getItemAtPosition(position);

             qnaAnswerRegister.setVisibility(INVISIBLE);
             feedPage.setVisibility(INVISIBLE);
             QAPage.setVisibility(INVISIBLE);
             QnADetailPage.setVisibility(VISIBLE);

             if(item.getQnAAnswerExist().equals("0"))
             {
                 yesAnswerPage.setVisibility(View.INVISIBLE);
                 noAnswerPage.setVisibility(View.VISIBLE);
                 QnADetailTitle.setText(item.getQnATitle());
                 QnADetailContent.setText(item.getQnAContent());
                 QnADetailQuestioner.setText(item.getQnAUserNickname());
             }
             else{
                 yesAnswerPage.setVisibility(View.VISIBLE);
                 noAnswerPage.setVisibility(View.INVISIBLE);

                 QnADetailTitle.setText(item.getQnATitle());
                 QnADetailContent.setText(item.getQnAContent());
                 QnADetailQuestioner.setText(item.getQnAUserNickname());
                 QnADetailAnswer.setText(item.getQnAAnswer());
                 QnADetailShopName.setText(item.getQnAShopName());
           }
         }
     });

     QuestionHagi.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent intentQuestion = new Intent(ShopDetaildInfo.this, QuestionRegister.class);
             intentQuestion.putExtra("shopName", shop_name.getText().toString());
             startActivity(intentQuestion);

             qnaAnswerRegister.setVisibility(INVISIBLE);

         }
     });


        proReg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){
                qnaAnswerRegister.setVisibility(INVISIBLE);
                Intent productionRegIntent = new Intent(ShopDetaildInfo.this, RegisterProduction.class);
                productionRegIntent.putExtra("shopName",getIntent().getStringExtra("name"));
                startActivity(productionRegIntent);
            }
        });

        qnaAnswerRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new JSONTaskRegAnswer().execute("http://192.168.43.72:3000/insertQnAAnswer");

            }
        });
    }
    @Override
    public void onClick(View arg0){
        // TODO Auto-generated method stub

        // 버튼이 클릭 됐을 때 이벤트 처리

    }

    public class JSONTaskFavoriteCheck extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("userEmail", preUserEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
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

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
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
            shopProduction =  result.split("\\|");


        }
    }

    public class JSONTaskProduction extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopName", getIntent().getStringExtra("name"));


                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
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

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
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
            shopProduction =  result.split("\\|");

            shopProductionURL = new String[shopProduction.length];


            for(int i = 0; i < shopProduction.length; i++){
               shopProductionURL[i] = shopProduction[i];
            }



            for(int i = 0; i < shopProductionURL.length; i++){
                Log.d("shopProductionURL : ", shopProductionURL[i]);
            }

            GridView gridViewImages = (GridView)findViewById(R.id.gridViewImages);
            ImageGridAdapter imageGridAdapter = new ImageGridAdapter(ShopDetaildInfo.this, shopProductionURL);
            gridViewImages.setAdapter(imageGridAdapter);

        }
    }

    public class JSONTaskRegAnswer extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("answer", qnaAnswerEdit.getText());
                jsonObject.accumulate("nickName", QnADetailQuestioner.getText());
                jsonObject.accumulate("title", QnADetailTitle.getText());
                jsonObject.accumulate("shopName", shop_name.getText());
                jsonObject.accumulate("context", QnADetailContent.getText());
                jsonObject.accumulate("production", QnADetailContent);
                Log.d("Answer : ", qnaAnswerEdit.getText().toString());
                Log.d("nickName : ", QnADetailQuestioner.getText().toString());
                Log.d("title : ", QnADetailTitle.getText().toString());
                Log.d("shopName : ", shop_name.toString());



                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
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

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
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

            if(result.equals("answer register success"))
            {
                Log.d("results", result);
                QnADetailAnswer.setText(qnaAnswerEdit.getText().toString());
                regAnswerPage.setVisibility(INVISIBLE);
                noAnswerPage.setVisibility(INVISIBLE);
                yesAnswerPage.setVisibility(VISIBLE);
                qnaAnswerRegister.setVisibility(INVISIBLE);
            }
        }
    }

    public class JSONTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopNumber", shopNumber);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
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

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
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

            shopNumber = Integer.parseInt(result);
        }
    }

    public class JSONTaskQnA extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopName", shop_name.getText());

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
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

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
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

            if (result.equals("")) {

            }
            else {
                String[] QnA;
                QnA = result.split("&&&");
                adapter = new QnAListViewAdapter();
                QnAListView.setAdapter(adapter);

                for (int i = 0; i < QnA.length; i++) {
                    QnAInfo = QnA[i].split("\\|");
                    for(int j = 0; j < QnAInfo.length; j++)
                    {
                        Log.d("QnAInfo", QnAInfo[j]);
                    }
                    adapter.addItem(QnAInfo[5], QnAInfo[6], QnAInfo[7], QnAInfo[4], QnAInfo[3], QnAInfo[1], QnAInfo[9]);
                }
            }
        }
    }

    public class JSONTaskFavoriteShop extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopName", getIntent().getStringExtra("name"));
                jsonObject.accumulate("userEmail", preUserEmail);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
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

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
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
            favoriteShop = result.split("\\|");

            int check = 0;
            for(int j = 0; j < favoriteShop.length; j++)
            {
                if(favoriteShop[j].equals(shop_name.getText().toString()))
                {
                    canStar.setVisibility(View.VISIBLE);
                    binStar.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public class JSONTaskHostEmail extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("shopName", getIntent().getStringExtra("name"));

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try{
                    //URL url = new URL("http://192.168.25.16:3000/users");
                    URL url = new URL(urls[0]);
                    //연결을 함
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

                    //서버로 부터 데이터를 받음
                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while((line = reader.readLine()) != null){
                        buffer.append(line);
                    }

                    return buffer.toString();//서버로 부터 받은 값을 리턴해줌 아마 OK!!가 들어올것임

                } catch (MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(con != null){
                        con.disconnect();
                    }
                    try {
                        if(reader != null){
                            reader.close();//버퍼를 닫아줌
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

            if(preUserEmail.equals(result))
            {
             QuestionHagi.setVisibility(View.INVISIBLE);
             proReg.setVisibility(View.VISIBLE);
            }
            else{
                QuestionHagi.setVisibility(View.VISIBLE);
                proReg.setVisibility(View.INVISIBLE);
                QnAAnswerHagi.setVisibility(View.INVISIBLE);
            }

        }
    }
}
